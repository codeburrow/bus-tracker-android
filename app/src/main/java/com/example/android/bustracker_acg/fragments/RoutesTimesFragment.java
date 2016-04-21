package com.example.android.bustracker_acg.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.android.bustracker_acg.MainActivity;
import com.example.android.bustracker_acg.R;
import com.example.android.bustracker_acg.adapters.RoutesTimesExpandableListAdapter;
import com.example.android.bustracker_acg.database.BusTrackerDBHelper;

import java.util.ArrayList;
import java.util.HashMap;


public class RoutesTimesFragment extends Fragment {


    // LOG_TAG
    protected static final String TAG = "Routes Times Fragment";
    // mCallback – the interface member that contains a reference to the parent activity’s implementation of the interface
    OnExpandableListItemSelectedListener mCallback;
    // ExpandableListView
    private ExpandableListView expandableListView;
    // Number of groups in the expandable list view adapter
    int expandableListViewAdapterSize;


    // Every fragment must have a default empty constructor.
    public RoutesTimesFragment() {
    }


    // OnExpandableListItemSelectedListener – this is our interface to communicate back to the activity.
    // It lets us notify the activity about a selected item
    public interface OnExpandableListItemSelectedListener {
        public void onExpandableListItemSelected(int groupPosition, int childPosition);
    }


    /*
    Overriding the lifecycle methods
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(TAG, "onAttach()");

        // makes sure the MainActivity implements the callback interface.
        // If not, it throws an exception
        try{
            mCallback = (OnExpandableListItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " The MainActivity activity must "
                    + "implement OnExpandableListItemSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView()");
        return inflater.inflate(R.layout.fragment_routes_times, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated()");

        // Get the list view
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandable_list_view);

        // Need these to set the group indicator correctly
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        /*
         * setIndicatorBounds(int, int) does not work properly for Android 4.3.
         * They introduced a new method setIndicatorBoundsRelative(int, int)
         * which works ok for 4.3.
         */
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expandableListView.setIndicatorBounds(width - GetPixelFromDips(90), width - GetPixelFromDips(10));
        } else {
            expandableListView.setIndicatorBoundsRelative(width - GetPixelFromDips(90), width - GetPixelFromDips(30));
        }

//        SetAdapterAsyncTask setAdapterAsyncTask = new SetAdapterAsyncTask();
//        setAdapterAsyncTask.execute();

        setAdapterForeground();

    }


    private void setAdapterForeground(){
        // Check SharedPreferences for the language
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.PREFS_FILE, Activity.MODE_PRIVATE);
        // get the language
        String language = sharedPreferences.getString(MainActivity.LANGUAGE, MainActivity.ENG);
        // ExpandableListAdapter
        RoutesTimesExpandableListAdapter listAdapter;
        // Routes Header
        ArrayList<String> listDataHeader;
        // Routes Info - Times & Stations
        HashMap<String, ArrayList<String>> listDataChildStation, listDataChildTime;
        // Database Helper
        BusTrackerDBHelper db = new BusTrackerDBHelper(getActivity());

            /*
             * Preparing the list data
             */
        if (language.equals(MainActivity.GR)) {
            listDataHeader = db.getAllRouteNamesGR();
        } else {
            listDataHeader = db.getAllRouteNamesENG();
        }
        listDataChildStation=new HashMap<>();
        listDataChildTime=new HashMap<>();


        for(int i = 0; i < listDataHeader.size(); i++){
            if (language.equals(MainActivity.GR)) {
                listDataChildStation.put(listDataHeader.get(i), db.getAllRouteStopNamesGR(i + 1));
            } else if (language.equals(MainActivity.ENG)){
                listDataChildStation.put(listDataHeader.get(i), db.getAllRouteStopNamesENG(i + 1));
            }
            listDataChildTime.put(listDataHeader.get(i), db.getAllRouteStopTimes(i + 1));
        }

        // Create the custom expandable list adapter
        listAdapter = new RoutesTimesExpandableListAdapter(getActivity(),
                listDataHeader,
                listDataChildStation,
                listDataChildTime);

        expandableListViewAdapterSize = listAdapter.getGroupCount();

        expandableListView.setAdapter(listAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                mCallback.onExpandableListItemSelected(groupPosition, childPosition);

                return false;
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated()");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.e(TAG, "onViewStateRestored()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart()  ============");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach()");
    }


    // This method helps to setIndicatorBounds
    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //do when hidden
            Log.e(TAG, "do when hidden");

            try {
                // Collapse all the expanded groups when the fragment is hidden
                for (int i = 0; i <= expandableListViewAdapterSize; i++) {
                    expandableListView.collapseGroup(i);
                }
            } catch (NullPointerException e){
                Log.e(TAG, "NullPointerException");
            }
        } else {
            //do when shown
            Log.e(TAG, "do when shown");
        }
    }


    private class SetAdapterAsyncTask extends
            AsyncTask<Void, Void, RoutesTimesExpandableListAdapter> {

        // LOG TAG
        private static final String TAG = "SetAdapterAsyncTask";

        @Override
        protected RoutesTimesExpandableListAdapter doInBackground(Void... params) {
            // Check SharedPreferences for the language
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.PREFS_FILE, Activity.MODE_PRIVATE);
            // get the language
            String language = sharedPreferences.getString(MainActivity.LANGUAGE, MainActivity.ENG);
            // ExpandableListAdapter
            RoutesTimesExpandableListAdapter listAdapter;
            // Routes Header
            ArrayList<String> listDataHeader;
            // Routes Info - Times & Stations
            HashMap<String, ArrayList<String>> listDataChildStation, listDataChildTime;
            // Database Helper
            BusTrackerDBHelper db = new BusTrackerDBHelper(getActivity());

            /*
             * Preparing the list data
             */
            if (language.equals(MainActivity.GR)) {
                listDataHeader = db.getAllRouteNamesGR();
            } else {
                listDataHeader = db.getAllRouteNamesENG();
            }
            listDataChildStation=new HashMap<>();
            listDataChildTime=new HashMap<>();


            for(int i = 0; i < listDataHeader.size(); i++){
                if (language.equals(MainActivity.GR)) {
                    listDataChildStation.put(listDataHeader.get(i), db.getAllRouteStopNamesGR(i + 1));
                } else if (language.equals(MainActivity.ENG)){
                    listDataChildStation.put(listDataHeader.get(i), db.getAllRouteStopNamesENG(i + 1));
                }
                listDataChildTime.put(listDataHeader.get(i), db.getAllRouteStopTimes(i + 1));
            }

            // Create the custom expandable list adapter
            listAdapter = new RoutesTimesExpandableListAdapter(getActivity(),
                listDataHeader,
                listDataChildStation,
                listDataChildTime);

            return listAdapter;
        }


        @Override
        protected void onPostExecute(RoutesTimesExpandableListAdapter listAdapter) {
            super.onPostExecute(listAdapter);

            expandableListViewAdapterSize = listAdapter.getGroupCount();

            expandableListView.setAdapter(listAdapter);

            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    mCallback.onExpandableListItemSelected(groupPosition, childPosition);

                    return false;
                }
            });


        }
    }

}
