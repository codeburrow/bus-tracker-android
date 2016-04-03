package com.example.android.bustracker_acg;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.android.bustracker_acg.alarm.AlarmInterface;
import com.example.android.bustracker_acg.alarm.AlarmReceiver;
import com.example.android.bustracker_acg.database.AlarmDAO;
import com.example.android.bustracker_acg.database.BusTrackerDBHelper;
import com.example.android.bustracker_acg.database.DatabaseContract;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements AlarmInterface, NavigationView.OnNavigationItemSelectedListener,
        RoutesTimesFragment.OnExpandableListItemSelectedListener,
        AlarmFragment.OnAutoSettingsButtonListener,
        SetupAutoAlarmFragment.MinutesPickerDialogFragment.OnAutoAlarmSetListener {

    // TAG
    private static final String TAG = "Main Activity";
    // Drawer
    DrawerLayout drawer;
    // Fragments
    private final WhereIsTheBusFragment whereIsTheBusFragment = new WhereIsTheBusFragment();
    private final RoutesTimesFragment routesTimesFragment = new RoutesTimesFragment();
    private final FaqFragment faqFragment = new FaqFragment();
    private final AlarmFragment alarmFragment = new AlarmFragment();
    // SharedPreferences file name
    public static final String PREFS_FILE = "UserPreferencesFile";
    // sharedPreference key for language preference
    public static final String LANGUAGE = "LanguagePreference";
    // supported languages
    public static final String GR = "GR";
    public static final String ENG = "ENG";
    // SharedPreferences
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    // back pressed flag
    private boolean doubleBackToExitPressedOnce = false;
    // General Alarm Switch
    private static SwitchCompat generalAlarmSwitch;
    // General Alarm State Changed
    public static boolean generalAlarmStateChanged;
    // Alarm Manager
    static AlarmManager alarmManager;
    // Calendar
    public static Calendar calendar;
    // Database Helper
    public static BusTrackerDBHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()");

        // Get Alarm Manager
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar/AppBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);

        // Initialize the BusTrackerDBHelper
        db = new BusTrackerDBHelper(this);
        AlarmDAO alarm = db.getAutoAlarmDAO();
        alarm.setTime(DatabaseContract.AlarmsEntry.AUTO_DEFAULT);
        db.updateAlarm(alarm);
        // Set up the drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

//            /** Called when a drawer has settled in a completely closed state. */
//            @Override
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
//            }
//
//            /** Called when a drawer has settled in a completely open state. */
//            @Override
//            public void onDrawerOpened(View view) {
//                super.onDrawerOpened(view);
//            }

            /** Called when a drawer moves. */
            @Override
            public void onDrawerSlide(View view, float slideOffset) {
                super.onDrawerSlide(view, slideOffset);
//                Log.e(TAG, slideOffset + "");

                if (generalAlarmStateChanged){
                    generalAlarmStateChanged = false;
                    generalAlarmSwitch.setChecked(checkAlarms());
                }
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Find view of alarm switch on drawer
        generalAlarmSwitch = (SwitchCompat) navigationView.getMenu().getItem(2).getActionView().findViewById(R.id.alarm_switch);
        generalAlarmSwitch.setChecked(checkAlarms());
        generalAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Check custom alarms only
                    ArrayList<Integer> alarmStatesList = db.getAllAlarmStates();
                    for (int i = 1; i < alarmStatesList.size(); i ++) {
                        if( alarmStatesList.get(i) == 1){
                            return;
                        }
                    }

                    // Update auto alarm state to 1 : ON
                    db.updateAutoAlarm(1);
                } else {
                    // Update all alarms states to 0 : OFF
                    db.updateAlarmStates_Off();
                }
                // Update the AlarmFragment
                displayAlarmUpdated(isChecked);
                // Update the Actual Alarms
                actualAlarmsUpdated(isChecked);
            }
        });

        // Get SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Check the language
        checkLanguages(navigationView);

        // Display Where is the bus Fragment
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Checking for fragment count on back stack
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                if (getSupportFragmentManager().getBackStackEntryAt(0).getName().equals("RouteTimesMapFragment")) {
                    getSupportFragmentManager().popBackStack();
                    displayRoutesTimes();
                } else if (getSupportFragmentManager().getBackStackEntryAt(0).getName().equals("SetupAutoAlarmFragment")) {
                    getSupportFragmentManager().popBackStack();
                    displayAlarm();
                    if (db.getAutoAlarmDAO().getTime().equals(DatabaseContract.AlarmsEntry.AUTO_DEFAULT)){
                        AlarmFragment.autoAlarmSwitch.setChecked(false);
                    }
                }
            } else if (!doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Press BACK again to exit.", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                super.onBackPressed();
                return;
            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        boolean languageChanged = false;

        switch(id) {
            case R.id.nav_where_is_the_bus:
                displayWhereIsTheBus();
                break;
            case R.id.nav_routes_times:
                displayRoutesTimes();
                break;
            case R.id.nav_alarm:
                displayAlarm();
                break;
            case R.id.nav_faq:
                displayFAQ();
                break;
            case R.id.nav_greek:
                Log.e(TAG, "Greek");
                if (!sharedPreferences.getString(LANGUAGE, ENG).equals(GR)){
                    editor.putString(LANGUAGE, GR).commit();
                    languageChanged = true;
                }
                break;
            case R.id.nav_english:
                Log.e(TAG, "English");
                if (!sharedPreferences.getString(LANGUAGE, ENG).equals(ENG)) {
                    editor.putString(LANGUAGE, ENG).commit();
                    languageChanged = true;
                }
                break;

            default:
                displayWhereIsTheBus();
        }



        // Highlight the selected item, update the title
        if (item.isCheckable()) {
            item.setChecked(true);
            // Update toolbar
            setTitle(item.getTitle());
        }

        // Close the drawer
        drawer.closeDrawer(GravityCompat.START);

        if (languageChanged) {
            // restartActivity
            restartActivity();
        }
        return true;
    }


    // Check if there is any active alarm
    public static boolean checkAlarms(){
        ArrayList<Integer> alarmStatesList = db.getAllAlarmStates();

        for (int alarmState : alarmStatesList) {
            if( alarmState == 1){
                return true;
            }
        }
        return false;

    }

    // Check which language is selected
    public void checkLanguages(NavigationView navigationView){
        if (sharedPreferences.getString(LANGUAGE, ENG).equals(GR)){
            navigationView.getMenu().findItem(R.id.nav_greek).setChecked(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_english).setChecked(true);
        }
    }

    // Update fragments when language changes
    protected void restartActivity(){
        Log.e(TAG, "Ready to Restart");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e){
            Log.i(TAG, e.getMessage());
        }
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    // Display Where Is The Bus Fragment
    protected void displayWhereIsTheBus() {
        // Checking for fragment count on back stack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (whereIsTheBusFragment.isAdded()){
            // if the fragment is already in container
            fragmentTransaction.show(whereIsTheBusFragment);
        } else {
            // fragment needs to be added to frame container
            fragmentTransaction.add(R.id.fragment_container, whereIsTheBusFragment);
        }

        // Hide the other added fragments
        if (routesTimesFragment.isAdded()) { fragmentTransaction.hide(routesTimesFragment); }
        if (alarmFragment.isAdded()) { fragmentTransaction.hide(alarmFragment); }
        if (faqFragment.isAdded()) { fragmentTransaction.hide(faqFragment); }

        // Commit changes
        fragmentTransaction.commit();
    }

    // Display Routes Times Fragment
    protected void displayRoutesTimes() {
        // Checking for fragment count on back stack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (routesTimesFragment.isAdded()){
            // if the fragment is already in container
            fragmentTransaction.show(routesTimesFragment);
        } else {
            // fragment needs to be added to frame container
            fragmentTransaction.add(R.id.fragment_container, routesTimesFragment);
        }

        // Hide the other added fragments
        if (whereIsTheBusFragment.isAdded()) { fragmentTransaction.hide(whereIsTheBusFragment); }
        if (alarmFragment.isAdded()) { fragmentTransaction.hide(alarmFragment); }
        if (faqFragment.isAdded()) { fragmentTransaction.hide(faqFragment); }

        // Commit changes
        fragmentTransaction.commit();
    }

    // Display Alarm Fragment
    protected void displayAlarm() {
        // Checking for fragment count on back stack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (alarmFragment.isAdded()){
            // if the fragment is already in container
            fragmentTransaction.show(alarmFragment);
        } else {
            // fragment needs to be added to frame container
            fragmentTransaction.add(R.id.fragment_container, alarmFragment);
        }

        // Hide the other added fragments
        if (whereIsTheBusFragment.isAdded()) { fragmentTransaction.hide(whereIsTheBusFragment); }
        if (faqFragment.isAdded()) { fragmentTransaction.hide(faqFragment); }
        if (routesTimesFragment.isAdded()) { fragmentTransaction.hide(routesTimesFragment); }

        // Commit changes
        fragmentTransaction.commit();
    }

    // Display Updated Alarm Fragment
    // Returns true if the Fragment is added (FragmentManager) and updated
    protected boolean displayAlarmUpdated(boolean isChecked) {
        if (alarmFragment.isAdded()) {
            // Update AlarmFragment auto switch - GUI
            AlarmFragment.autoAlarmSwitch.setChecked(isChecked);
            // Update AlarmFragment list adapter - GUI
            AlarmFragment.updateAdapter();

            return true;
        }

        return false;
    }

    // Update the Actual Alarms
    protected void actualAlarmsUpdated(boolean isChecked){
        if (isChecked){
            AlarmDAO autoAlarm = db.getAutoAlarmDAO();
            if (autoAlarm.getTime().equals(DatabaseContract.AlarmsEntry.AUTO_DEFAULT)){
                displayAlarm();
                displaySetupAutoAlarm();
                // Close the drawer
                drawer.closeDrawer(GravityCompat.START);
            } else {
                if (!alarmFragment.isAdded()){
                    Log.e(TAG, "Main Set Alarm");
                    setAlarm(autoAlarm);
                }
            }
        } else {
            ArrayList<AlarmDAO> alarms = db.getAllAlarmsDAO();
            for (AlarmDAO alarm : alarms){
                cancelAlarm(alarm);
            }
        }
    }

    // Display FAQ Fragment
    protected void displayFAQ() {
        // Checking for fragment count on back stack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (faqFragment.isAdded()){
            // if the fragment is already in container
            fragmentTransaction.show(faqFragment);
        } else {
            // fragment needs to be added to frame container
            fragmentTransaction.add(R.id.fragment_container, faqFragment);
        }

        // Hide the other added fragments
        if (whereIsTheBusFragment.isAdded()) { fragmentTransaction.hide(whereIsTheBusFragment); }
        if (routesTimesFragment.isAdded()) { fragmentTransaction.hide(routesTimesFragment); }
        if (alarmFragment.isAdded()) { fragmentTransaction.hide(alarmFragment); }

        // Commit changes
        fragmentTransaction.commit();
    }

    protected void displayRouteTimesMap(int groupPosition, int childPosition) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // Hide routesTimesFragment
        fragmentTransaction.hide(routesTimesFragment);

        RoutesTimesMapFragment mapFragment = new RoutesTimesMapFragment();
        Bundle args = new Bundle();
        args.putInt("groupPosition", groupPosition);
        args.putInt("childPosition", childPosition);
        mapFragment.setArguments(args);

        // Add the fragment to the 'activity_schedule' FrameLayout
        // Commit changes
        fragmentTransaction
                .add(R.id.fragment_container, mapFragment).addToBackStack("RouteTimesMapFragment").commit();
    }

    // Display Setup Auto Alarm Fragment
    public void displaySetupAutoAlarm() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // Hide AlarmFragment
        fragmentTransaction.hide(alarmFragment);

        SetupAutoAlarmFragment setupAutoAlarmFragment = new SetupAutoAlarmFragment();

        // Add the fragment to the 'activity_schedule' FrameLayout
        // Commit changes
        fragmentTransaction
                .add(R.id.fragment_container, setupAutoAlarmFragment).addToBackStack("SetupAutoAlarmFragment").commit();
    }

    // The RoutesTimesFragment interface
    @Override
    public void onExpandableListItemSelected(int groupPosition, int childPosition) {
        displayRouteTimesMap(groupPosition, childPosition);
    }

    // The AlarmFragment interface
    @Override
    public void onAutoSettingsButtonClicked() {
        displaySetupAutoAlarm();
    }


    // The SetupAlarmFragment.MinutesPickerDialogFragment interface
    @Override
    public void onAutoAlarmSetClicked() {
        displayAlarm();
    }

    @Override
    public void setAlarm(AlarmDAO alarm) {

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


        calendar.set(Calendar.HOUR_OF_DAY, alarmHours);
        calendar.set(Calendar.MINUTE, alarmMinutes);
        calendar.set(Calendar.SECOND, 0);

        Log.e(TAG, calendar.getTime().toString());

        Intent alarmReceiverIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarm.getID(), alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void cancelAlarm(AlarmDAO alarm) {
        Intent alarmReceiverIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarm.getID(), alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
}
