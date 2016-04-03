package com.example.android.bustracker_acg.alarm;

import com.example.android.bustracker_acg.database.AlarmDAO;

/**
 * Created by giorgos on 3/31/2016.
 */
public interface AlarmInterface {

    public void setAlarm(AlarmDAO alarm);

    public void cancelAlarm(AlarmDAO alarm);

}
