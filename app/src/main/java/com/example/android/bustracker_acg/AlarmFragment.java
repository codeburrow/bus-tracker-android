package com.example.android.bustracker_acg;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by giorgos on 3/26/2016.
 */

public class AlarmFragment extends Fragment {

    // LOG_TAG
    protected static final String TAG = "Alarm Fragment";
    // Construct the data source
    ArrayList<String> alarms = new ArrayList<String>();
    // AlarmListAdapter
    static AlarmListAdapter alarmListAdapter;
    // Hour and Minute
    private int mHours;
    private int mMinutes;



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

        // Create the adapter to convert the array to views
        alarmListAdapter = new AlarmListAdapter(getActivity(), alarms);

        // Get a Calendar instance
        final Calendar calendar = Calendar.getInstance();
        // Get the current time
        mHours = calendar.get(Calendar.HOUR_OF_DAY);
        mMinutes = calendar.get(Calendar.MINUTE);

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
        ListView listView = (ListView) view.findViewById(R.id.alarm_list_view);
        listView.setAdapter(alarmListAdapter);


        ImageButton createAlarmButton = (ImageButton) view.findViewById(R.id.create_alarm_button);
        createAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  Show our TimePicker dialog
                new TimePickerDialog(getActivity(),
                        TimePickerDialog.THEME_DEVICE_DEFAULT_DARK,
                        mTimeSetListener,
                        mHours,
                        mMinutes,
                        DateFormat.is24HourFormat(getActivity())).show();

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
                    // Method onTimeSet() is called once when dialog is dismissed
                    // and is called twice when Done button is clicked.
                    // So we have to use the method below
                    if (view.isShown()) {
                        // This method will return true only once
                        mHours = hourOfDay;
                        mMinutes = minute;
                        updateAdapter();
                    }
                }
            };

    // updates the time we display in the TextView
    private void updateAdapter() {
        alarmListAdapter.add(
                new StringBuilder()
                        .append(pad(mHours)).append(":")
                        .append(pad(mMinutes)).toString());
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

}
