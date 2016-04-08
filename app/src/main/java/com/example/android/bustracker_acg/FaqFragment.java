package com.example.android.bustracker_acg;


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

import com.example.android.bustracker_acg.database.BusTrackerDBHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class FaqFragment extends Fragment {

    // LOG_TAG
    protected static final String TAG = "FAQ Fragment";
    // ExpandableListView
    private ExpandableListView expandableListView;
    // Number of groups in the expandable list view adapter
    int expandableListViewAdapterSize;


    // Every fragment must have a default empty constructor.
    public FaqFragment(){}


    /*
    Overriding the lifecycle methods
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(TAG, "onAttach()");
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
        return inflater.inflate(R.layout.fragment_faq, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated()");

        // Get the list view
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandable_list_view_faq);

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

        SetAdapterAsyncTask setAdapterAsyncTask = new SetAdapterAsyncTask();
        setAdapterAsyncTask.execute();

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
            }catch (NullPointerException e){
                Log.e(TAG, "NullPointerException");
            }

        } else {
            //do when shown
            Log.e(TAG, "do when shown");
        }
    }


    // This method helps to setIndicatorBounds
    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }


    private class SetAdapterAsyncTask extends
            AsyncTask<Void, Void, FaqExpandableListAdapter> {

        // LOG TAG
        private static final String TAG = "SetAdapterAsyncTask";

        @Override
        protected FaqExpandableListAdapter doInBackground(Void... params) {
            // Check SharedPreferences for the language
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.PREFS_FILE, Activity.MODE_PRIVATE);
            // get the language
            String language = sharedPreferences.getString(MainActivity.LANGUAGE, MainActivity.ENG);
            // ExpandableListAdapter
            FaqExpandableListAdapter listAdapter;
            // Questions - headers
            ArrayList<String> listDataHeader;
            // Answers - expanded
            ArrayList<String> answers = new ArrayList<>();
            HashMap<String, String> listDataChild;
            // Database Helper
            BusTrackerDBHelper db = new BusTrackerDBHelper(getActivity());

            /*
             * Preparing the list data
             */
            listDataHeader = db.getAllFaqQuestions(language);
            answers = db.getAllFaqAnswers(language);
            listDataChild = new HashMap<>();

            for (int i = 0; i < listDataHeader.size(); i++){
                listDataChild.put(listDataHeader.get(i), answers.get(i)); // Header, Child data
            }

            // Create the custom expandable list adapter
            listAdapter = new FaqExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

            return listAdapter;
        }

        @Override
        protected void onPostExecute(FaqExpandableListAdapter listAdapter) {
            super.onPostExecute(listAdapter);
            // Get the number of adapter items
            expandableListViewAdapterSize = listAdapter.getGroupCount();
            // Set the adapter to the list view
            expandableListView.setAdapter(listAdapter);
        }


        }


}
