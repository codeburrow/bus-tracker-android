package com.example.android.bustracker_acg.alarm;

import com.example.android.bustracker_acg.database.AlarmDAO;

public interface AlarmInterface {

    public void setAlarm(AlarmDAO alarm);

    public void cancelAlarm(AlarmDAO alarm);

}
