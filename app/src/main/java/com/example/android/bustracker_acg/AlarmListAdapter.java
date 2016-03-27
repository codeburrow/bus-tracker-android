package com.example.android.bustracker_acg;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by giorgos on 3/26/2016.
 */

public class AlarmListAdapter extends ArrayAdapter<String> {


    public AlarmListAdapter(Context context, ArrayList<String> alarms) {
        super(context, 0, alarms);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String alarm = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.alarm_list_item, parent, false);
        }
        // Lookup view for data population
        final TextView alarmTimeTextView = (TextView) convertView.findViewById(R.id.alarm_time_text_view);
        SwitchCompat alarmSwitch = (SwitchCompat) convertView.findViewById(R.id.alarm_switch);
        // Populate the data into the template view using the data object
        alarmTimeTextView.setText(alarm);
        alarmSwitch.setChecked(true);

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //modify your enableAlarm method to take in the time as a String
                    Log.e("AlarmON", alarmTimeTextView.getText().toString());
                } else {
                    Log.e("AlarmOFF", alarmTimeTextView.getText().toString());
                }
            }
        });


        // Return the completed view to render on screen
        return convertView;
    }


}
