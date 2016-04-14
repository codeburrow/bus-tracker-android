package com.example.android.bustracker_acg.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.android.bustracker_acg.MainActivity;
import com.example.android.bustracker_acg.R;
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

import java.util.ArrayList;


public class RoutesTimesMapFragment extends Fragment implements OnMapReadyCallback {

    // rootView
    private static View rootView;
    // LOG_TAG
    protected static final String TAG = "RoutesTimesMap Fragment";
    // Pointer for flyTo next/prev feature
    private int pointer;
    // array lists
    private String routeName;
    private ArrayList<String> stationPointNames;
    private ArrayList<String> stationPointTimes;
    private ArrayList<LatLng> stationPointLatLngs;
    private ArrayList<LatLng> snappedPointLatLngs;
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
    // markers list
    private ArrayList<Marker> markers = new ArrayList<>();

    /*
    Every fragment must have a default empty constructor.
    - The system invokes the default constructor to re-instantiate the fragment
      when restoring its activity’s state.
    - Fragments generally should not implement additional constructors
      or override the default constructor.
     */
    public RoutesTimesMapFragment(){}

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

        // check if there are arguments passed to the fragment
        Bundle args = getArguments();

        if (args != null) {
            // set markers based on argument passed in
            int routeID = args.getInt("groupPosition") + 1;
            pointer = args.getInt("childPosition");

            PrepareDataAsyncTask prepareDataAsyncTask = new PrepareDataAsyncTask();
            prepareDataAsyncTask.execute(routeID);

        } else {
            // set a case if needed
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView()");

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            super.onCreate(savedInstanceState);
            rootView = inflater.inflate(R.layout.fragment_routes_times_map, container, false);


            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.routes_times_map);
            mapFragment.getMapAsync(this);

        } catch (InflateException e) {
            /*
                map is already there, just return rootView as it is
            */
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated()");

        ImageButton nextButton = (ImageButton) rootView.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gMapReady) {
                    if (pointer < stationPointLatLngs.size() - 1) {
                        // Move the pointer to the next position
                        pointer += 1;
                        // Get the LatLng of the routeStop at this position
                        LatLng next_station_point = stationPointLatLngs.get(pointer);
                        float bearing;
                        if (pointer == stationPointLatLngs.size() - 1) {
                            bearing =
                                    bearingBetweenLatLngs(
                                            stationPointLatLngs.get(pointer),
                                            stationPointLatLngs.get(pointer - 1));
                        } else {
                            bearing =
                                    bearingBetweenLatLngs(
                                            stationPointLatLngs.get(pointer),
                                            stationPointLatLngs.get(pointer + 1));
                        }
                        // Animate flyTo this routeStop
                        flyTo(CameraPosition.builder()
                                .target(next_station_point)
                                .zoom(15)
                                .bearing(bearing)
                                .tilt(45)
                                .build());
                        // Make the previous one red
                        markers.get(pointer - 1).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop_red_marker));
                        // Change the color and open the info window of the marker
                        markers.get(pointer).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop_blue_marker));
                        markers.get(pointer).showInfoWindow();
                    }
                }
            }
        });

        ImageButton prevButton = (ImageButton) rootView.findViewById(R.id.previous_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gMapReady) {
                    if (pointer > 0) {
                        // Move the pointer to the next position
                        pointer -= 1;
                        // Get the LatLng of the routeStop at this position
                        LatLng next_station_point = stationPointLatLngs.get(pointer);
                        float bearing;
                        if (pointer == 0) {
                            bearing =
                                    bearingBetweenLatLngs(
                                            stationPointLatLngs.get(pointer),
                                            stationPointLatLngs.get(pointer + 1));
                        } else {
                            bearing =
                                    bearingBetweenLatLngs(
                                            stationPointLatLngs.get(pointer),
                                            stationPointLatLngs.get(pointer - 1));
                        }
                        // Animate flyTo this routeStop
                        flyTo(CameraPosition.builder()
                                .target(next_station_point)
                                .zoom(15)
                                .bearing(bearing)
                                .tilt(45)
                                .build());
                        // Make the previous one red
                        markers.get(pointer + 1).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop_red_marker));
                        // Change the color and open the info window of the marker
                        markers.get(pointer).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop_blue_marker));
                        markers.get(pointer).showInfoWindow();

                    }
                }
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


    // OnMapReadyCallback
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "onMapReady()  <===========");

        gMapReady = true;
        gMap = googleMap;

        gMap.getUiSettings().setZoomControlsEnabled(true);
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
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop_red_marker))
        );
        markers.add(marker);
    }


    /*
        A nice bearing effect
    */
    private Location convertLatLngToLocation(LatLng latLng) {
        Location location = new Location("someLoc");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

    private float bearingBetweenLatLngs(LatLng beginLatLng,LatLng endLatLng) {
        Location beginLocation = convertLatLngToLocation(beginLatLng);
        Location endLocation = convertLatLngToLocation(endLatLng);
        return beginLocation.bearingTo(endLocation);
    }

    // move the camera to..
    private void moveTo(CameraPosition target) {
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
    }

    // fly the camera to..
    private void flyTo(CameraPosition target){
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(target), 1000, null);
    }


    private class PrepareDataAsyncTask extends
            AsyncTask<Integer, Void, Void> {

        // LOG TAG
        private static final String TAG = "PrepareDataAsyncTask";

        @Override
        protected Void doInBackground(Integer... params) {
            int routeID = params[0];
            // Check SharedPreferences for the language
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.PREFS_FILE, Activity.MODE_PRIVATE);
            // get the language
            String language = sharedPreferences.getString(MainActivity.LANGUAGE, MainActivity.ENG);
            // Database Helper
            BusTrackerDBHelper db = new BusTrackerDBHelper(getActivity());

            if (language.equals(MainActivity.GR)) {
                routeName = db.getRouteNameGR_byID(routeID);
                stationPointNames = db.getAllRouteStopNamesGR(routeID);
            } else {
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

            try {
                CameraPosition cameraPosition =
                        new CameraPosition.Builder()
                                .target(stationPointLatLngs.get(pointer))
                                .bearing(
                                        bearingBetweenLatLngs(
                                                stationPointLatLngs.get(pointer),
                                                stationPointLatLngs.get(pointer + 1)
                                        )
                                )
                                .tilt(90)
                                .zoom(15)
                                .build();

                flyTo(cameraPosition);
            } catch (Exception e){  // The last routeStop
                CameraPosition cameraPosition =
                        new CameraPosition.Builder()
                                .target(stationPointLatLngs.get(pointer))
                                .bearing(
                                        bearingBetweenLatLngs(
                                                stationPointLatLngs.get(pointer),
                                                stationPointLatLngs.get(pointer - 1)
                                        )
                                )
                                .tilt(90)
                                .zoom(15)
                                .build();

                flyTo(cameraPosition);
            }

            // Fill gMap with the stations of the route
            int size = stationPointNames.size();
            for (int i = 0; i < size; i++){
                addMarkerToMap(stationPointLatLngs.get(i), stationPointNames.get(i), stationPointTimes.get(i) + " - " + routeName);
                // Make the first one blue
                if (pointer == i){
                    markers.get(pointer).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop_blue_marker));
                    markers.get(pointer).showInfoWindow();
                }
            }

            // Draw the polyline on gMap
            size = snappedPointLatLngs.size();
            PolylineOptions polylineOptions = new PolylineOptions();
            for (int j = 0; j < size; j++){
                polylineOptions.add(snappedPointLatLngs.get(j));
            }
            polylineOptions.width(10).color(Color.RED);
            gMap.addPolyline(polylineOptions);


            // marker's listener
            gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    // Change the previous pointer color
                    markers.get(pointer).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop_red_marker));
                    // Move the pointer to the right marker
                    String markerID = marker.getId().toString();
                    pointer = Integer.parseInt(markerID.substring(1, markerID.length()));
                    // Change the current pointer color
                    markers.get(pointer).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop_blue_marker));
                    // Show the Info Window of the marker
                    marker.showInfoWindow();
                    // Animate - flyTo this marker
                    flyTo(CameraPosition.builder()
                            .target(marker.getPosition())
                            .zoom(15)
                            .bearing(0)
                            .tilt(0)
                            .build());
                    return true;
                }
            });

        }
    }


}
