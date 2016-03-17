package com.example.android.bustracker_acg;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Drawer
    DrawerLayout drawer;
    // Fragments
    WhereIsTheBusFragment whereIsTheBusFragment;
    FaqFragment faqFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            whereIsTheBusFragment = new WhereIsTheBusFragment();
            faqFragment = new FaqFragment();
        }

        // Set a Toolbar to replace the ActionBar/AppBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayFragmentWITB();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment= null;
        switch(id) {
            case R.id.nav_where_is_the_bus:
                displayFragmentWITB();
                break;
            case R.id.nav_routes_times:
//                fragmentClass = RoutesTimesFragment.class;
                break;
            case R.id.nav_alarm:
//                fragmentClass = AlarmFragment.class;
                break;
            case R.id.nav_faq:
                displayFragmentFAQ();
                break;
            default:
                displayFragmentWITB();
        }

        // Fragment Transaction
        // replace() destroys the fragments - so use add
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_container, fragment)
//                .commit();

        setTitle(item.getTitle());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void displayFragmentWITB() {
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
        // Commit changes
        fragmentTransaction.commit();
    }

    protected void displayFragmentFAQ() {
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
        // Commit changes
        fragmentTransaction.commit();
    }

}
