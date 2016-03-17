package com.example.android.bustracker_acg;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


public class WhereIsTheBusFragment extends Fragment implements OnMapReadyCallback {

    // LOG_TAG
    protected static final String TAG = "WITB Fragment";
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
}
