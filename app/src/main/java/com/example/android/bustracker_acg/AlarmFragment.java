package com.example.android.bustracker_acg;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.bustracker_acg.database.AlarmDAO;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by giorgos on 3/26/2016.
 */

public class AlarmFragment extends Fragment {

    // LOG_TAG
    protected static final String TAG = "Alarm Fragment";
    // Construct the data source
    static ArrayList<AlarmDAO> alarms = new ArrayList<AlarmDAO>();
    // AlarmListAdapter
    protected static AlarmListAdapter alarmListAdapter;
    // Automatic Switch
    protected static SwitchCompat autoAlarmSwitch;
    // Calendar
    Calendar calendar;
    // Maximum number of alarms
    private static final int MAX_ALARMS = 4;
    // Position in alarms to be deleted
    static int deletePosition;

    // Every fragment must have a default empty constructor.
    public AlarmFragment(){}


    /*
    Overriding the lifecycle methods
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(TAG, "onAttach()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()");

        // Initialize the BusTrackerDBHelper
//        db = new BusTrackerDBHelper(getActivity());

        // Get alarms DAO
        alarms = MainActivity.db.getAllAlarmsDAO_autoException();

        // Create the adapter to convert the array to views
        alarmListAdapter = new AlarmListAdapter(getActivity(), alarms);


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


        autoAlarmSwitch = (SwitchCompat) view.findViewById(R.id.auto_alarm_switch);
        final AlarmDAO autoAlarmDAO = MainActivity.db.getAutoAlarmDAO();
        if (autoAlarmDAO.getState() == 1){
            Log.e("STARTAlarmON", autoAlarmDAO.getID() + " " + autoAlarmDAO.getTime() + " " + autoAlarmDAO.getState());
            autoAlarmSwitch.setChecked(true);
        } else {
            Log.e("STARTAlarmOFF", autoAlarmDAO.getID() + " " + autoAlarmDAO.getTime() + " " + autoAlarmDAO.getState());
            autoAlarmSwitch.setChecked(false);
        }

        autoAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    autoAlarmDAO.setState(1);
                    MainActivity.db.updateAlarm(autoAlarmDAO);
                    Log.e("AlarmON", autoAlarmDAO.getID() + " " + autoAlarmDAO.getTime() + " " + autoAlarmDAO.getState());
                } else {
                    autoAlarmDAO.setState(0);
                    MainActivity.db.updateAlarm(autoAlarmDAO);
                    Log.e("AlarmOFF", autoAlarmDAO.getID() + " " + autoAlarmDAO.getTime() + " " + autoAlarmDAO.getState());
                }

                MainActivity.generalAlarmStateChanged = true;
            }

        });

        ImageButton createAlarmButton = (ImageButton) view.findViewById(R.id.create_alarm_button);
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


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                updateAdapter();

                Log.e(TAG, "long clicked position: " + pos);

                deletePosition = pos;

                DeleteAlarmDialogFragment deleteAlarmDialogFragment = new DeleteAlarmDialogFragment();
                deleteAlarmDialogFragment.show(getFragmentManager(), "DeleteAlarm");

                return true;
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
                        addAlarm(hourOfDay, minute);
                    }
                }
            };


    private void addAlarm(int hours, int minutes) {

        updateAdapter();

        // Build the time string
        String time = new StringBuilder()
                .append(pad(hours)).append(":")
                .append(pad(minutes)).toString();

        // Check if the selected time already exists
        for (AlarmDAO alarm : alarms){
            if (alarm.getTime().equals(time)){
                Toast.makeText(getActivity(), time + " already exists!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        MainActivity.db.addAlarm(time,1);
        alarmListAdapter.add(MainActivity.db.getLastAlarmDAO());
        alarmListAdapter.notifyDataSetChanged();
        MainActivity.generalAlarmStateChanged = true;
    }

    public static void updateAdapter(){
        alarmListAdapter.clear();
        alarms = MainActivity.db.getAllAlarmsDAO_autoException();
        for (AlarmDAO alarm : alarms){
            alarmListAdapter.insert(alarm, alarmListAdapter.getCount());
        }
        alarmListAdapter.notifyDataSetChanged();
    }


    /**
     * @param c
     * @return the appropriate String representation of the hour or minute.
     *
     * The pad() method that we called from the updateDisplay()
     * It will prefix a zero to the number if it's a single digit
     */
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }


    public static class DeleteAlarmDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setMessage(R.string.dialog_fire_missiles)
            builder.setMessage("Delete Alarm: " + alarms.get(deletePosition).getTime() + " ?")
                    .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.e(TAG, "DELETE CLICKED");

                            // Delete the alarm from db
                            deleteAlarm();

                        }

                        private void deleteAlarm() {
                            // TEST
//                            AlarmDAO al = AlarmFragment.alarms.get(deletePosition);
//                            Log.e(TAG, al.getID() + " " + al.getTime() + " " + al.getState());


                            MainActivity.db.deleteAlarm(AlarmFragment.alarms.get(deletePosition));
                            alarmListAdapter.remove(alarms.get(deletePosition));
                            alarmListAdapter.notifyDataSetChanged();
                            MainActivity.generalAlarmStateChanged = true;
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            Log.e(TAG, "CANCEL CLICKED");
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }


}
