package com.example.android.bustracker_acg.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.bustracker_acg.MainActivity;
import com.example.android.bustracker_acg.R;
import com.example.android.bustracker_acg.database.AlarmDAO;
import com.example.android.bustracker_acg.database.DatabaseContract;

import java.util.ArrayList;
import java.util.Calendar;


public class AlarmFragment extends Fragment implements AlarmInterface {

    // LOG_TAG
    protected static final String TAG = "Alarm Fragment";
    // Construct the data source
    private static ArrayList<AlarmDAO> alarms = new ArrayList<AlarmDAO>();
    // AlarmListAdapter
    protected static AlarmListAdapter alarmListAdapter;
    // Automatic Switch
    public static SwitchCompat autoAlarmSwitch;
    // Automatic AlarmDAO
    private static AlarmDAO autoAlarmDAO;
    // TextView for Automatic time
    public static TextView autoAlarmTimeTextView;
    // Calendar
    public static Calendar calendar;
    // Maximum number of alarms
    private static final int MAX_ALARMS = 4;
    // Position in alarms to be deleted
    static int deletePosition;
    // Alarm Manager
    static AlarmManager alarmManager;
    // mCallback – the interface member that contains a reference to the parent activity’s implementation of the interface
    OnAutoSettingsButtonListener mCallback;

    // Every fragment must have a default empty constructor.
    public AlarmFragment() {
    }

    // OnAutoSettingsButtonListener – this is our interface to communicate back to the activity.
    // It lets us notify the activity about a selected item
    public interface OnAutoSettingsButtonListener {
        public void onAutoSettingsButtonClicked();
    }


    /*
    Overriding the lifecycle methods
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(TAG, "onAttach()");

        // makes sure the MainActivity implements the callback interface.
        // If not, it throws an exception
        try{
            mCallback = (OnAutoSettingsButtonListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " The MainActivity activity must "
                    + "implement OnAutoSettingsButtonListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()");

        // Get alarms DAO
        alarms = MainActivity.db.getAllAlarmsDAO_autoException();
        // Create the adapter to convert the array to views
        alarmListAdapter = new AlarmListAdapter(getActivity(), alarms);
        // Get Alarm Manager
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView()");
        return inflater.inflate(R.layout.fragment_alarm, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated()");


        // Attach the adapter to the ListView
        final ListView listView = (ListView) view.findViewById(R.id.alarm_list_view);
        listView.setAdapter(alarmListAdapter);
        // Attach an OnItemLongClickListener to the ListView
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                updateAdapter();

                deletePosition = pos;

                DeleteAlarmDialogFragment deleteAlarmDialogFragment = new DeleteAlarmDialogFragment();
                deleteAlarmDialogFragment.show(getFragmentManager(), "DeleteAlarm");

                return true;
            }
        });


        // Automatic Alarm Switch
        autoAlarmSwitch = (SwitchCompat) view.findViewById(R.id.auto_alarm_switch);
        // AutoAlarmTimeTextView
        autoAlarmTimeTextView = (TextView) view.findViewById(R.id.auto_alarm_time_text_view);
        // Update setChecked of auto alarm switch
        updateAutoAlarm();


        // Attach an setOnCheckedChangeListener to Automatic Alarm Switch
        autoAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (autoAlarmDAO.getTime().equals(DatabaseContract.AlarmsEntry.AUTO_DEFAULT)){
                        mCallback.onAutoSettingsButtonClicked();
                    } else {
                        MainActivity.db.updateAlarmStates_Off();
                        MainActivity.db.updateAutoAlarm(1);
                        setAlarm(autoAlarmDAO);
                    }
                } else {
                    cancelAlarm(autoAlarmDAO);
                    MainActivity.db.updateAutoAlarm(0);
                }

                // Update Adapter
                updateAdapter();
                // Set flag to true, so the switch on drawer can be updated
                MainActivity.generalAlarmStateChanged = true;
            }
        });

        // Auto alarm settings ImageButton
        final ImageButton autoSettingsButton = (ImageButton) view.findViewById(R.id.auto_alarm_settings);
        autoSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Set up your auto alarm settings");

                mCallback.onAutoSettingsButtonClicked();
            }
        });

        // Add a new alarm ImageButton
        final ImageButton createAlarmButton = (ImageButton) view.findViewById(R.id.create_alarm_button);
        createAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MAX_ALARMS > MainActivity.db.getAlarmsCount()) {
                    // Get a Calendar instance
                    calendar = Calendar.getInstance();
                    //  Show our TimePicker dialog
                    new TimePickerDialog(getActivity(),
                            TimePickerDialog.THEME_DEVICE_DEFAULT_DARK,
                            mTimeSetListener,
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            DateFormat.is24HourFormat(getActivity())).show();

                } else {
                    Toast.makeText(getActivity(), MAX_ALARMS - 1 + " alarms are enough", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated()");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.e(TAG, "onViewStateRestored()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart()  ============");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach()");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //do when hidden
            Log.e(TAG, "do when hidden");
        } else {
            //do when shown
            Log.e(TAG, "do when shown");
        }
    }


    // the callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    /*
                       Method onTimeSet() is called once when dialog is dismissed
                       and is called twice when Done button is clicked.
                       So we have to use the method below
                       This method will return true only once
                    */
                    if (view.isShown()) {
                        // Add alarm to db
                        addAlarmListItem(hourOfDay, minute);
                        // Set Alarm
                        setAlarm(MainActivity.db.getLastAlarmDAO());
                        Log.e(TAG, "Alarm ID - first Added: " + MainActivity.db.getLastAlarmDAO().getID());
                    }
                }
            };


    private void addAlarmListItem(int hours, int minutes) {

        updateAdapter();

        // Build the time string
        String time = new StringBuilder()
                .append(pad(hours)).append(":")
                .append(pad(minutes)).toString();

        // Check if the selected time already exists
        for (AlarmDAO alarm : alarms) {
            if (alarm.getTime().equals(time)) {
                Toast.makeText(getActivity(), time + " already exists!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        MainActivity.db.addAlarm(time, 1);
        alarmListAdapter.add(MainActivity.db.getLastAlarmDAO());
        alarmListAdapter.notifyDataSetChanged();
        MainActivity.generalAlarmStateChanged = true;
    }


    @Override
    public void setAlarm(AlarmDAO alarm) {

        // Get hours and minutes from the AlarmDAO
        int alarmHours = Integer.parseInt(alarm.getTime().substring(0,2));
        int alarmMinutes = Integer.parseInt(alarm.getTime().substring(3));

        // Get a calendar instance
        calendar = Calendar.getInstance();

        if (calendar.getTime().getHours() > alarmHours) {
            calendar.add(Calendar.DATE, 1);
        } else if (calendar.getTime().getHours() == alarmHours) {
            if (calendar.getTime().getMinutes() >= alarmMinutes) {
                calendar.add(Calendar.DATE, 1);
            }
        }

        calendar.set(Calendar.HOUR_OF_DAY, alarmHours);
        calendar.set(Calendar.MINUTE, alarmMinutes);
        calendar.set(Calendar.SECOND, 0);

        Log.e(TAG, calendar.getTime().toString());

        Intent alarmReceiverIntent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), alarm.getID(), alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void cancelAlarm(AlarmDAO alarm){
        Intent alarmReceiverIntent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), alarm.getID(), alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }


    public static void updateAdapter() {
        alarmListAdapter.clear();
        alarms = MainActivity.db.getAllAlarmsDAO_autoException();
        for (AlarmDAO alarm : alarms) {
            alarmListAdapter.insert(alarm, alarmListAdapter.getCount());
        }
        alarmListAdapter.notifyDataSetChanged();
    }

    public static void updateAutoAlarm(){
        // Automatic Alarm DAO
        autoAlarmDAO = MainActivity.db.getAutoAlarmDAO();

        if (autoAlarmDAO.getState() == 1) {
            autoAlarmSwitch.setChecked(true);
        } else {
            autoAlarmSwitch.setChecked(false);
        }
        autoAlarmTimeTextView.setText(autoAlarmDAO.getTime());
    }



    /**
     * @param c
     * @return the appropriate String representation of the hour or minute.
     * <p/>
     * The pad() method that we called from the updateDisplay()
     * It will prefix a zero to the number if it's a single digit
     */
    public static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    // DialogFragment for deleting an alarm
    public static class DeleteAlarmDialogFragment extends DialogFragment implements AlarmInterface {

        // Alarm Time Text View
        private TextView alarmTimeTextView;
        // Delete Button
        private Button deleteButton;
        // CANCEL Button
        private Button cancelButton;
        // AlarmDAO to be deleted
        private AlarmDAO alarm;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);

            View view = inflater.inflate(R.layout.dialog_fragment_delete_alarm, null, false);

            alarm = alarms.get(deletePosition);

            alarmTimeTextView = (TextView) view.findViewById(R.id.alarm_text_view);
            alarmTimeTextView.setText(alarm.getTime());

            deleteButton = (Button) view.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "DELETE CLICKED");

                    // Cancel the alarm from alarm manager
                    cancelAlarm(alarm);
                    // Delete the alarm from db
                    deleteAlarm();

                    // Dismiss the Dialog
                    dismiss();
                }
            });

            cancelButton = (Button) view.findViewById(R.id.cancel_button);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // User cancelled the dialog
                    Log.e(TAG, "CANCEL CLICKED");

                    // Dismiss the Dialog
                    dismiss();
                }
            });

            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            return view;
        }

        @Override
        public void setAlarm(AlarmDAO alarm) {

        }

        @Override
        public void cancelAlarm(AlarmDAO alarm) {
            Intent alarmReceiverIntent = new Intent(getActivity(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), alarm.getID(), alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }

        private void deleteAlarm() {
            MainActivity.db.deleteAlarm(alarms.get(deletePosition));
            alarmListAdapter.remove(alarms.get(deletePosition));
            alarmListAdapter.notifyDataSetChanged();
            MainActivity.generalAlarmStateChanged = true;
        }

//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the Builder class for convenient dialog construction
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
////            builder.setMessage(R.string.dialog_fire_missiles)
//            builder.setTitle("Delete Alarm ?")
//                    .setMessage(alarms.get(deletePosition).getTime())
//                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    Log.e(TAG, "DELETE CLICKED");
//
//                                    // Cancel the alarm from alarm manager
//                                    cancelAlarm(alarms.get(deletePosition).getID());
//                                    // Delete the alarm from db
//                                    deleteAlarm();
//
//                                }
//
//                                private void deleteAlarm() {
//                                    MainActivity.db.deleteAlarm(alarms.get(deletePosition));
//                                    alarmListAdapter.remove(alarms.get(deletePosition));
//                                    alarmListAdapter.notifyDataSetChanged();
//                                    MainActivity.generalAlarmStateChanged = true;
//                                }
//
//                                public void cancelAlarm(int alarmID) {
//                                    Intent alarmReceiverIntent = new Intent(getActivity(), AlarmReceiver.class);
//                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), alarmID, alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                                    alarmManager.cancel(pendingIntent);
//                                }
//                            }
//                    )
//                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // User cancelled the dialog
//                            Log.e(TAG, "CANCEL CLICKED");
//                        }
//                    });
//            // Create the AlertDialog object and return it
//            return builder.create();
//        }

    }



}
