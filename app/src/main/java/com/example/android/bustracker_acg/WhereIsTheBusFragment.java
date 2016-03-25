package com.example.android.bustracker_acg;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.android.bustracker_acg.database.BusTrackerDBHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class WhereIsTheBusFragment extends Fragment implements OnMapReadyCallback {

    // LOG_TAG
    protected static final String TAG = "WITB Fragment";
    // Route Names for the popup menu
    private ArrayList<String> routeNames;
    // Selected RouteID - by default is 1
    private String routeShown;
    // Google Map
    GoogleMap gMap;
    boolean gMapReady = false;
    // DEREE camera position
    static final CameraPosition DEREE = CameraPosition.builder()
            .target(new LatLng(38.00367, 23.830351))
            .zoom(15)
            .bearing(0)
            .tilt(0)
            .build();
    // Marker
    Marker marker;

    /*
    Every fragment must have a default empty constructor.
    - The system invokes the default constructor to re-instantiate the fragment
      when restoring its activity’s state.
    - Fragments generally should not implement additional constructors
      or override the default constructor.
     */
    public WhereIsTheBusFragment(){}


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

        // Check SharedPreferences for the language
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.PREFS_FILE, Activity.MODE_PRIVATE);
        // get the language
        String language = sharedPreferences.getString(MainActivity.LANGUAGE, MainActivity.ENG);
        // Database Helper
        BusTrackerDBHelper db = new BusTrackerDBHelper(getActivity());

        if (language.equals(MainActivity.GR)) {
            routeNames = db.getAllRouteNamesGR();
        } else {
            routeNames = db.getAllRouteNamesENG();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView()");
        return inflater.inflate(R.layout.fragment_where_is_the_bus, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated()");

        final ImageButton chooseRouteButton = (ImageButton) view.findViewById(R.id.choose_route_button);
        chooseRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getActivity(), view);
                Menu popupMenu = popup.getMenu();
                for (int i = 0; i < routeNames.size(); i++) {
                    popupMenu.add(routeNames.get(i));
                }
                popupMenu.getItem(0).setChecked(true);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Update the routeShown
                        routeShown = item.toString();
                        // AsyncTask to get the appropriate route's coordinates
                        GetCoordinatesAsyncTask getCoordinatesAsyncTask = new GetCoordinatesAsyncTask();
                        getCoordinatesAsyncTask.execute();

                        return true;
                    }
                });
                popup.show();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.where_is_the_bus_map);
        mapFragment.getMapAsync(this);
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

    /*
    When overriding these lifecycle methods
     —  with the exception of onCreateView()
     — you MUST call through to the super class’s implementation of the method.
    Otherwise, an exception occurs at run-time.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMapReady = true;
        gMap = googleMap;

        moveTo(DEREE);
    }

    private void moveTo(CameraPosition target){
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
    }

    private void flyTo(CameraPosition target){
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 2000, null);
    }

    private void setMarker(LatLng latLng){
        marker = gMap.addMarker(new MarkerOptions().position(latLng));
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        moveTo(CameraPosition.builder()
                .target(latLng)
                .zoom(17)
                .bearing(0)
                .tilt(0)
                .build());
    }

    // Async Task to get the coordinates of the appropriate route
    private class GetCoordinatesAsyncTask extends AsyncTask<Void, Void, LatLng> {

        // LOG_TAG
        private static final String GET_COORDINATES_URL = "http://ashoka.students.acg.edu/BusTrackerAndroid/webServices/getCoordinates.php";
        // Response tags
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";


        @Override
        protected LatLng doInBackground(Void... args) {
            // Check for success tag
            int success;
            // Database Helper
            BusTrackerDBHelper db = new BusTrackerDBHelper(getActivity());
            // Check SharedPreferences for the language
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.PREFS_FILE, Activity.MODE_PRIVATE);
            // Get the language
            String language = sharedPreferences.getString(MainActivity.LANGUAGE, MainActivity.ENG);
            // JSON Parser
            JSONParser jsonParser = new JSONParser();


            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                // Check the language
                if (language.equals(MainActivity.GR)) {
                    params.add(new BasicNameValuePair("routeID", db.getRouteID_byNameGR(routeShown) + ""));
                } else {
                    params.add(new BasicNameValuePair("routeID", db.getRouteID_byNameENG(routeShown) + ""));
                }

                //Posting routeID to script
                JSONObject json = jsonParser.makeHttpRequest(
                        GET_COORDINATES_URL, "POST", params);

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    JSONArray messageArray = new JSONArray(json.getString(TAG_MESSAGE));
                    JSONObject result = messageArray.getJSONObject(0);

                    return
                            new LatLng(
                                    Double.parseDouble(result.getString("lat")),
                                    Double.parseDouble(result.getString("lng"))
                            );
                } else {
                    Log.e(TAG,"Retrieving Failure! " + json.getString(TAG_MESSAGE));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            if (latLng != null){
                if (gMapReady) {
                    // Remove the latest marker
                    if (marker != null) {
                        marker.remove();
                    }
                    // And set the new one
                    setMarker(latLng);
                }
                Log.e(TAG, latLng.toString());
            } else {
                Log.e(TAG, "null");
            }
        }
    }




}
