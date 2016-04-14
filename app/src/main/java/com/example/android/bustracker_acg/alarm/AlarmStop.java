package com.example.android.bustracker_acg.alarm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.android.bustracker_acg.R;
import com.example.android.bustracker_acg.database.AlarmDAO;
import com.example.android.bustracker_acg.database.BusTrackerDBHelper;

import net.frakbot.glowpadbackport.GlowPadView;

import java.util.Calendar;

public class AlarmStop extends AppCompatActivity {

    // LOG TAG
    private String TAG = "AlarmStop";
    // Database Helper
    private BusTrackerDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity FULL_SCREEN
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // These flags are needed so the activity can start even the phone is locked
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_alarm_stop);

        // Initialize the BusTrackerDBHelper
        db = new BusTrackerDBHelper(this);

        final GlowPadView glowPad = (GlowPadView) findViewById(R.id.alarm_stop_widget);


        glowPad.setOnTriggerListener(new GlowPadView.OnTriggerListener() {
            @Override
            public void onGrabbed(View v, int handle) {
                // Do nothing
            }

            @Override
            public void onReleased(View v, int handle) {
                // Do nothing
            }

            @Override
            public void onTrigger(View v, int target) {
                Log.e(TAG, "Target triggered! ID = " + target);



                AlarmReceiver.player.stop();
                finish();
            }

            @Override
            public void onGrabbedStateChange(View v, int handle) {
                // Do nothing
            }

            @Override
            public void onFinishFinalAnimation() {
                // Do nothing
            }
        });
        updateAlarmState_Off();

    }


    @Override
    public void onBackPressed() {}

    private void updateAlarmState_Off(){
        // Get the time from the calendar
        Calendar calendar = Calendar.getInstance();
        // Build the time string
        String time = new StringBuilder()
                .append(AlarmFragment.pad(calendar.getTime().getHours())).append(":")
                .append(AlarmFragment.pad(calendar.getTime().getMinutes())).toString();

        // Get the alarm by time
        AlarmDAO alarm = db.getAlarmDAO_byTime(time);
        // Set alarm state to 0
        alarm.setState(0);
        // Update the alarm
        db.updateAlarm(alarm);

        Log.e(TAG, alarm.getID() + " " + alarm.getTime() + " " + alarm.getState());
    }

}
