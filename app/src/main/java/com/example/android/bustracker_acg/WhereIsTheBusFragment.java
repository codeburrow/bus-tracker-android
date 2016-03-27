package com.example.android.bustracker_acg;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class WhereIsTheBusFragment extends Fragment implements OnMapReadyCallback {

    // LOG_TAG
    protected static final String TAG = "WITB Fragment";
    // Route Names for the popup menu
    private ArrayList<String> routeNames;
    // Selected RouteName
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
    // Timer & TimerTask
    Timer timer;
    TimerTask timerTask;
    // Handler to be used in TimerTask
    final Handler handler = new Handler();
    // ArrayLists
    private String routeName;
    private ArrayList<String> stationPointNames;
    private ArrayList<String> stationPointTimes;
    private ArrayList<LatLng> stationPointLatLngs;
    private ArrayList<LatLng> snappedPointLatLngs;
    // markers list
    private ArrayList<Marker> markers = new ArrayList<>();
    // Route Polyline
    PolylineOptions routePolyline;
    // Language selected
    String language;

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


    /*
        When overriding these lifecycle methods
        —  with the exception of onCreateView()
        — you MUST call through to the super class’s implementation of the method.
        Otherwise, an exception occurs at run-time.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()");

        // Check SharedPreferences for the language
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.PREFS_FILE, Activity.MODE_PRIVATE);
        // get the language
        language = sharedPreferences.getString(MainActivity.LANGUAGE, MainActivity.ENG);
        // Database Helper
        BusTrackerDBHelper db = new BusTrackerDBHelper(getActivity());

        if (language.equals(MainActivity.GR)) {
            routeNames = db.getAllRouteNamesGR();
        } else {
            routeNames = db.getAllRouteNamesENG();
        }

        // Selected Route - by default the first in the routeNames
        routeShown = routeNames.get(0);

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
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Update the routeShown
                        routeShown = item.toString();
                        // AsyncTask to get the appropriate routeStops and Polyline
                        PrepareDataAsyncTask prepareDataAsyncTask = new PrepareDataAsyncTask();
                        prepareDataAsyncTask.execute();
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

        // Start the Timer on resume
        startTimer();
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

        // Stop the Timer
        stopTimer();
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

            // Stop the Timer
            stopTimer();
        } else {
            //do when shown
            Log.e(TAG, "do when shown");

            // Start the Timer
            startTimer();
        }
    }


    /*
        Google Maps
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMapReady = true;
        gMap = googleMap;

        // Default Polyline and Markers - routeStops
        PrepareDataAsyncTask prepareDataAsyncTask = new PrepareDataAsyncTask();
        prepareDataAsyncTask.execute();

        moveTo(DEREE);
    }

    private void moveTo(CameraPosition target){
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
    }

    private void flyTo(CameraPosition target){
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 2000, null);
    }

    private void setMarker(LatLng latLng) {
        marker = gMap.addMarker(new MarkerOptions().position(latLng));
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        moveTo(CameraPosition.builder()
                .target(latLng)
                .zoom(17)
                .bearing(0)
                .tilt(0)
                .build());
    }

    /**
     * @param latlng - the (latitude, longitude) of the station - marker
     * @param title - the name of the station (RouteStop)
     * @param snippet - the time of the RouteStop and the routeName
     */
    public void addMarkerToMap(LatLng latlng, String title, String snippet){
        Marker marker = gMap.addMarker(new MarkerOptions()
                        .position(latlng)
                        .title(title)
                        .snippet(snippet)
        );
        markers.add(marker);
    }


    /*
        TimerTask
     */
    public void startTimer() {
        // Set a new Timer
        timer = new Timer();

        // Initialize the TimerTask's job
        initializeTimerTask();

        // Schedule the timer -- After the first 0ms the TimerTask will run every 2000ms
        timer.schedule(timerTask, 0, 2000); //

        Log.e(TAG, "Timer started.. ");
    }

    public void stopTimer() {
        // Stop the timer
        // if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        Log.e(TAG, "Timer stopped.. ");
    }


    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {
                // Use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        /*
                            Debug
                         */
                        //get the current timeStamp
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                        final String strDate = simpleDateFormat.format(calendar.getTime());
                        // LOG
                        Log.e(TAG, strDate);

                        // Execute the GetCoordinatesAsyncTask
                        GetCoordinatesAsyncTask getCoordinatesAsyncTask = new GetCoordinatesAsyncTask();
                        getCoordinatesAsyncTask.execute();
                    }
                });
            }
        };

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


    private class PrepareDataAsyncTask extends
            AsyncTask<Void, Void, Void> {

        // LOG TAG
        private static final String TAG = "PrepareDataAsyncTask";

        @Override
        protected Void doInBackground(Void... params) {
            // Database Helper
            BusTrackerDBHelper db = new BusTrackerDBHelper(getActivity());
            // roueID
            int routeID;

            if (language.equals(MainActivity.GR)) {
                routeID = db.getRouteID_byNameGR(routeShown);
                routeName = db.getRouteNameGR_byID(routeID);
                stationPointNames = db.getAllRouteStopNamesGR(routeID);
            } else {
                routeID = db.getRouteID_byNameENG(routeShown);
                routeName = db.getRouteNameENG_byID(routeID);
                stationPointNames = db.getAllRouteStopNamesENG(routeID);
            }

            stationPointTimes = db.getAllRouteStopTimes(routeID);
            stationPointLatLngs = db.getAllRouteStopLatLngs(routeID);
            snappedPointLatLngs = db.getAllSnappedPointLatLngs(routeID);

            // Test
            Log.e(TAG, routeName);
            Log.e(TAG, stationPointNames.size() + stationPointNames.toString());
            Log.e(TAG, stationPointTimes.size() + stationPointTimes.toString());
            Log.e(TAG, stationPointLatLngs.size() + stationPointLatLngs.toString());
            Log.e(TAG, snappedPointLatLngs.size() + snappedPointLatLngs.toString());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Clear the map from all markers and polylines
            gMap.clear();
            // Clear the markers array list
            markers.clear();

            // Fill gMap with the stations of the route
            int size = stationPointNames.size();
            for (int i = 0; i < size; i++){
                addMarkerToMap(stationPointLatLngs.get(i), stationPointNames.get(i), stationPointTimes.get(i) + " - " + routeName);
            }

            // Draw the polyline on gMap
            size = snappedPointLatLngs.size();
            routePolyline = new PolylineOptions();
            for (int j = 0; j < size; j++){
                routePolyline.add(snappedPointLatLngs.get(j));
            }
            routePolyline.width(10).color(Color.RED);
            gMap.addPolyline(routePolyline);

        }
    }


}
