package com.example.android.bustracker_acg;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RoutesTimesFragment.OnExpandableListItemSelectedListener{

    // TAG
    private static final String TAG = "MainActivity";
    // Drawer
    DrawerLayout drawer;
    // Fragments
    private final WhereIsTheBusFragment whereIsTheBusFragment = new WhereIsTheBusFragment();
    private final RoutesTimesFragment routesTimesFragment = new RoutesTimesFragment();
    private final FaqFragment faqFragment = new FaqFragment();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()");

        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar/AppBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);

        // set up the drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // get SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // check the language
        languageSetChecked(navigationView);

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
                getSupportFragmentManager().popBackStack();
                displayRoutesTimes();
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
//                fragmentClass = AlarmFragment.class;
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

    public void languageSetChecked(NavigationView navigationView){
        if (sharedPreferences.getString(LANGUAGE, ENG).equals(GR)){
            navigationView.getMenu().findItem(R.id.nav_greek).setChecked(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_english).setChecked(true);
        }
    }

    // update fragments when language changes
    protected void restartActivity(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e){
            Log.i(TAG, e.getMessage());
        }
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    // display Where Is The Bus Fragment
    protected void displayWhereIsTheBus() {
        //Checking for fragment count on back stack
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

        // Hide fragment faqFragment
        if (faqFragment.isAdded()) { fragmentTransaction.hide(faqFragment); }
        if (routesTimesFragment.isAdded()) { fragmentTransaction.hide(routesTimesFragment); }

        // Commit changes
        fragmentTransaction.commit();
    }

    // display Routes Times Fragment
    protected void displayRoutesTimes() {
        //Checking for fragment count on back stack
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

        // Hide fragment faqFragment
        if (faqFragment.isAdded()) { fragmentTransaction.hide(faqFragment); }
        if (whereIsTheBusFragment.isAdded()) { fragmentTransaction.hide(whereIsTheBusFragment); }

        // Commit changes
        fragmentTransaction.commit();
    }


    protected void displayFAQ() {
        //Checking for fragment count on back stack
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

        // Hide fragment faqFragment
        if (whereIsTheBusFragment.isAdded()) { fragmentTransaction.hide(whereIsTheBusFragment); }
        if (routesTimesFragment.isAdded()) { fragmentTransaction.hide(routesTimesFragment); }

        // Commit changes
        fragmentTransaction.commit();
    }

    protected void displayRouteTimesMapFragment(int groupPosition, int childPosition) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // Hide fragment faqFragment
        if (whereIsTheBusFragment.isAdded()) { fragmentTransaction.hide(whereIsTheBusFragment); }
        if (routesTimesFragment.isAdded()) { fragmentTransaction.hide(routesTimesFragment); }
        if (faqFragment.isAdded()) { fragmentTransaction.hide(faqFragment); }

        // Commit changes
        RoutesTimesMapFragment mapFragment = new RoutesTimesMapFragment();
        Bundle args = new Bundle();
        args.putInt("groupPosition", groupPosition);
        args.putInt("childPosition", childPosition);
        mapFragment.setArguments(args);

        // Add the fragment to the 'activity_schedule' FrameLayout
        fragmentTransaction
                .add(R.id.fragment_container, mapFragment).addToBackStack(null).commit();
    }

    // The RoutesTimesFragment interface
    @Override
    public void onExpandableListItemSelected(int groupPosition, int childPosition) {
        displayRouteTimesMapFragment(groupPosition, childPosition);
    }
}
