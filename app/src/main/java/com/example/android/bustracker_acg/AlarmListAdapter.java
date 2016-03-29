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

import com.example.android.bustracker_acg.database.AlarmDAO;

import java.util.ArrayList;

/**
 * Created by giorgos on 3/26/2016.
 */

public class AlarmListAdapter extends ArrayAdapter<AlarmDAO> {


    public AlarmListAdapter(Context context, ArrayList<AlarmDAO> alarms) {
        super(context, 0, alarms);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final AlarmDAO alarmDAO = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.alarm_list_item, parent, false);
        }

        // Lookup view for data population
        final TextView alarmTimeTextView = (TextView) convertView.findViewById(R.id.alarm_time_text_view);
        final SwitchCompat alarmSwitch = (SwitchCompat) convertView.findViewById(R.id.alarm_switch);

        // Populate the data into the template view using the data object
        alarmTimeTextView.setText(alarmDAO.getTime());

        if (alarmDAO.getState() == 1) {
            alarmSwitch.setChecked(true);
        } else {
            alarmSwitch.setChecked(false);
        }

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AlarmDAO alarm = MainActivity.db.getAlarmDAO_byTime(alarmTimeTextView.getText().toString());

                if (isChecked) {
                    //modify your enableAlarm method to take in the time as a String
                    Log.e("AlarmON", alarmTimeTextView.getText().toString() + ", ID:" + alarm.getID());

                    alarm.setState(1);
                } else {
                    Log.e("AlarmOFF", alarmTimeTextView.getText().toString() + ", ID:" + alarm.getID());

                    alarm.setState(0);
                }


                Log.e("Update", alarm.getID() + " " + alarm.getTime() + " " + alarm.getState());
                MainActivity.db.updateAlarm(alarm);
                MainActivity.generalAlarmStateChanged = true;
            }
        });


        // Return the completed view to render on screen
        return convertView;
    }


}
