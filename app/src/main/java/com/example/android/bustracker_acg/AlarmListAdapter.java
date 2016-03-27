package com.example.android.bustracker_acg;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by giorgos on 3/26/2016.
 */

public class AlarmListAdapter extends ArrayAdapter<Calendar> {


    public AlarmListAdapter(Context context, ArrayList<Calendar> calendars) {
        super(context, 0, calendars);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Calendar calendar = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.alarm_list_item, parent, false);
        }
        // Lookup view for data population
        TextView alarmTime = (TextView) convertView.findViewById(R.id.alarm_time_text_view);
        SwitchCompat alarmSwitch = (SwitchCompat) convertView.findViewById(R.id.alarm_switch);
        // Populate the data into the template view using the data object
        alarmTime.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        alarmSwitch.setChecked(true);
        // Return the completed view to render on screen
        return convertView;
    }


}
