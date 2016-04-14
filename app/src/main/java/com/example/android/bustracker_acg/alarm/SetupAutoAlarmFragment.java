package com.example.android.bustracker_acg.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.NumberPicker;

import com.example.android.bustracker_acg.MainActivity;
import com.example.android.bustracker_acg.R;
import com.example.android.bustracker_acg.adapters.RoutesTimesExpandableListAdapter;
import com.example.android.bustracker_acg.database.AlarmDAO;
import com.example.android.bustracker_acg.database.BusTrackerDBHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class SetupAutoAlarmFragment extends Fragment {

    // LOG_TAG
    protected static final String TAG = "Auto Alarm Fragment";
    // ExpandableListView
    private ExpandableListView expandableListView;
    // Number of groups in the expandable list view adapter
    int expandableListViewAdapterSize;
    // Station Time
    private static String stationTime = "";


    // Every fragment must have a default empty constructor.
    public SetupAutoAlarmFragment() {
    }



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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView()");
        return inflater.inflate(R.layout.fragment_setup_auto_alarm, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated()");

        // Get the list view
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandable_list_view);

        // Need these to set the group indicator correctly
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        /*
         * setIndicatorBounds(int, int) does not work properly for Android 4.3.
         * They introduced a new method setIndicatorBoundsRelative(int, int)
         * which works ok for 4.3.
         */
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expandableListView.setIndicatorBounds(width - GetPixelFromDips(90), width - GetPixelFromDips(10));
        } else {
            expandableListView.setIndicatorBoundsRelative(width - GetPixelFromDips(90), width - GetPixelFromDips(30));
        }

        SetAdapterAsyncTask setAdapterAsyncTask = new SetAdapterAsyncTask();
        setAdapterAsyncTask.execute();

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


    // This method helps to setIndicatorBounds
    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }


    private class SetAdapterAsyncTask extends
            AsyncTask<Void, Void, RoutesTimesExpandableListAdapter> {

        // LOG TAG
        private static final String TAG = "SetAdapterAsyncTask";

        @Override
        protected RoutesTimesExpandableListAdapter doInBackground(Void... params) {
            // Check SharedPreferences for the language
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.PREFS_FILE, Activity.MODE_PRIVATE);
            // get the language
            String language = sharedPreferences.getString(MainActivity.LANGUAGE, MainActivity.ENG);
            // ExpandableListAdapter
            RoutesTimesExpandableListAdapter listAdapter;
            // Routes Header
            ArrayList<String> listDataHeader;
            // Routes Info - Times & Stations
            HashMap<String, ArrayList<String>> listDataChildStation, listDataChildTime;
            // Database Helper
            BusTrackerDBHelper db = new BusTrackerDBHelper(getActivity());

            /*
             * Preparing the list data
             */
            if (language.equals(MainActivity.GR)) {
                listDataHeader = db.getAllRouteNamesGR();
            } else {
                listDataHeader = db.getAllRouteNamesENG();
            }
            listDataChildStation=new HashMap<>();
            listDataChildTime=new HashMap<>();


            for(int i = 0; i < listDataHeader.size(); i++){
                if (language.equals(MainActivity.GR)) {
                    listDataChildStation.put(listDataHeader.get(i), db.getAllRouteStopNamesGR(i + 1));
                } else if (language.equals(MainActivity.ENG)){
                    listDataChildStation.put(listDataHeader.get(i), db.getAllRouteStopNamesENG(i + 1));
                }
                listDataChildTime.put(listDataHeader.get(i), db.getAllRouteStopTimes(i + 1));
            }

            // Create the custom expandable list adapter
            listAdapter = new RoutesTimesExpandableListAdapter(getActivity(),
                    listDataHeader,
                    listDataChildStation,
                    listDataChildTime);

            return listAdapter;
        }


        @Override
        protected void onPostExecute(final RoutesTimesExpandableListAdapter listAdapter) {
            super.onPostExecute(listAdapter);

            expandableListViewAdapterSize = listAdapter.getGroupCount();

            expandableListView.setAdapter(listAdapter);

            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {

                    stationTime = listAdapter.getChild(groupPosition, childPosition).get(1).toString();

                    MinutesPickerDialogFragment minutePickerDialogFragment =
                            new MinutesPickerDialogFragment();
                    minutePickerDialogFragment.show(getFragmentManager(), "SelectMinutes");

                    Log.e(TAG, stationTime);

                    return false;
                }
            });


        }
    }


    // DialogFragment for deleting an alarm
    public static class MinutesPickerDialogFragment extends DialogFragment implements AlarmInterface {

        // Calendar
        Calendar calendar;
        // NumberPicker
        NumberPicker np;
        // Button
        Button okButton;
        // mCallback – the interface member that contains a reference to the parent activity’s implementation of the interface
        OnAutoAlarmSetListener mCallback;

        // OnAutoAlarmSetListener – this is our interface to communicate back to the activity.
        // It lets us notify the activity about a selected item
        public interface OnAutoAlarmSetListener {
            public void onAutoAlarmSetClicked();
        }


        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            // makes sure the MainActivity implements the callback interface.
            // If not, it throws an exception
            try{
                mCallback = (OnAutoAlarmSetListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " The MainActivity activity must "
                        + "implement OnAutoSettingsButtonListener");
            }
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);

            calendar = Calendar.getInstance();

            View view = inflater.inflate(R.layout.dialog_fragment_minutes_picker, null, false);

            np = (NumberPicker) view.findViewById(R.id.number_picker);
            // Populate NumberPicker values from minimum and maximum value range
            // Set the minimum value of NumberPicker
            np.setMinValue(5);
            // Set the maximum value of NumberPicker
            np.setMaxValue(40);
            // Set the default value of NumberPicker
            np.setValue(15);
            // Gets whether the selector wheel wraps when reaching the min/max value.
            np.setWrapSelectorWheel(false);
            // Disable soft keyboard on NumberPicker
            np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

            okButton = (Button) view.findViewById(R.id.ok_button);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the alarmDAO for the automatic alarm
                    AlarmDAO alarm = MainActivity.db.getAutoAlarmDAO();

                    // Set the alarm
                    setAlarm(alarm);

                    // Update db and UI
                    Log.e("Update", alarm.getID() + " " + alarm.getTime() + " " + alarm.getState());
                    MainActivity.db.updateAlarm(alarm);
                    MainActivity.generalAlarmStateChanged = true;

                    AlarmFragment.updateAutoAlarm();

                    // Callback to MainActivity
                    mCallback.onAutoAlarmSetClicked();

                    dismiss();
                }
            });

            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            return view;
        }

        @Override
        public void setAlarm(AlarmDAO alarm) {
            alarm.setTime(stationTime);
            Log.e(TAG, "Automatic: " + alarm.getTime());
            Log.e(TAG, "Minutes earlier: " + np.getValue());

            // Get hours and minutes from the AlarmDAO
            int alarmHours = Integer.parseInt(alarm.getTime().substring(0,2));
            int alarmMinutes = Integer.parseInt(alarm.getTime().substring(3));

            // Get a Calendar instance
            calendar = Calendar.getInstance();

            if (calendar.getTime().getHours() > alarmHours) {
                calendar.add(Calendar.DATE, 1);
            } else if (calendar.getTime().getHours() == alarmHours) {
                if (calendar.getTime().getMinutes() >= alarmMinutes) {
                    calendar.add(Calendar.DATE, 1);
                }
            }


            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(alarm.getTime().substring(0, 2)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(alarm.getTime().substring(3)));
            calendar.set(Calendar.SECOND, 0);

            // Subtract the minutes from the number picker
            calendar.add(Calendar.MINUTE, -np.getValue());

            Intent alarmReceiverIntent = new Intent(getActivity(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), alarm.getID(), alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmFragment.alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            String autoTime = new StringBuilder()
                    .append(AlarmFragment.pad(calendar.get(Calendar.HOUR_OF_DAY))).append(":")
                    .append(AlarmFragment.pad(calendar.get(Calendar.MINUTE))).toString();
            alarm.setTime(autoTime);
            alarm.setState(1);

            Log.e(TAG, "Automatic: " + alarm.getTime() + " with length: " + alarm.getTime().length());
            Log.e(TAG, "Calendar: " + calendar.getTime());

        }

        @Override
        public void cancelAlarm(AlarmDAO alarm) {

        }
    }


}
