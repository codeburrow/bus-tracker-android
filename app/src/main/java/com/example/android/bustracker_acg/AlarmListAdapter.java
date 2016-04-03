package com.example.android.bustracker_acg;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.android.bustracker_acg.alarm.AlarmInterface;
import com.example.android.bustracker_acg.alarm.AlarmReceiver;
import com.example.android.bustracker_acg.database.AlarmDAO;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by giorgos on 3/26/2016.
 */

public class AlarmListAdapter extends ArrayAdapter<AlarmDAO> implements AlarmInterface {

    // LOG_TAG
    private static final String TAG = "AlarmListAdapter";
    // Context
    private Context mContext;
    // Calendar
    public Calendar calendar;


    public AlarmListAdapter(Context context, ArrayList<AlarmDAO> alarms) {
        super(context, 0, alarms);
        mContext = context;
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
                    setAlarm(alarm);
                    alarm.setState(1);
                } else {
                    cancelAlarm(alarm);
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


    @Override
    public void setAlarm(AlarmDAO alarm) {

        // Get hours and minutes from the AlarmDAO
        int alarmHours = Integer.parseInt(alarm.getTime().substring(0,2));
        int alarmMinutes = Integer.parseInt(alarm.getTime().substring(3));

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

        Intent alarmReceiverIntent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, alarm.getID(), alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmFragment.alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void cancelAlarm(AlarmDAO alarm) {
        Intent alarmReceiverIntent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, alarm.getID(), alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmFragment.alarmManager.cancel(pendingIntent);
    }
}

