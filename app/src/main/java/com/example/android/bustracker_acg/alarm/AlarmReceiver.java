package com.example.android.bustracker_acg.alarm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.io.IOException;


public class AlarmReceiver extends WakefulBroadcastReceiver {

    public static MediaPlayer player;


    @Override
    public void onReceive(final Context context, Intent intent) {
        // This will start the AlarmStop activity
        Intent alarmStopIntent = new Intent(context, AlarmStop.class);
        alarmStopIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(alarmStopIntent);

        player = new MediaPlayer();
        try {
            player.setDataSource(context, getAlarmSound());
            final AudioManager audio = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audio.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                player.setAudioStreamType(AudioManager.STREAM_ALARM);
                player.prepare();
                player.start();
                player.setLooping(true);
            }
        } catch (IOException e) {
            Log.e("Error....", "Check code...");
        }

        //this will send a notification message
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmService.class.getName());
        // A WakefulBroadcastReceiver uses the method startWakefulService()
        // to start the service that does the work
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }



    /**
     * @return the alarm sound set in the device
     */
    private Uri getAlarmSound() {
        Uri alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alertSound == null) {
            alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alertSound == null) {
                alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alertSound;
    }
}
