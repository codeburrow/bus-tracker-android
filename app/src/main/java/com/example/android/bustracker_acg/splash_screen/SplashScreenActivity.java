package com.example.android.bustracker_acg.splash_screen;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.android.bustracker_acg.JSONParser;
import com.example.android.bustracker_acg.MainActivity;
import com.example.android.bustracker_acg.R;
import com.example.android.bustracker_acg.database.BusTrackerDBHelper;
import com.example.android.bustracker_acg.database.DatabaseContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by giorgos on 3/11/2016.
 */
public class SplashScreenActivity extends AppCompatActivity {

    // LOG TAG
    private static final String TAG = "SplashScreenActivity";
    // Custom Secret View
    private SecretTextView secretTextView;
    // JSON Parser
    JSONParser jsonParser = new JSONParser();
    // ProgressBar
    ProgressBar progressBar;
    int progressBarMax;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // secret text view
        secretTextView = (SecretTextView)findViewById(R.id.secret_text_view);
        secretTextView.setDuration(1000);
        secretTextView.setIsVisible(false);
        secretTextView.toggle();

        checkDataBase();

    }

    /**
     * Check if the database exist and can be read.
     *
     */
    private void checkDataBase() {

        SQLiteDatabase checkDB = null;
        try { // the database exists
            checkDB = SQLiteDatabase.openDatabase(getDatabasePath("busTracker.db").toString(), null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
            SplashAsyncTask splashAsyncTask = new SplashAsyncTask();
            splashAsyncTask.execute();
        } catch (SQLiteException e) { // the database must be created
            GetSizeAndDownloadAsyncTask getSizeAndDownloadAsyncTask = new GetSizeAndDownloadAsyncTask();
            getSizeAndDownloadAsyncTask.execute();
        }

    }

    public void startMain(){
        // Create new intent for the Main Activity
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        // Finish this SplashActivity
        finish();
        // Start the intent
        startActivity(intent);
    }

    /**
     * Async Task to make database
     */
    private class DbUpdateAsyncTask extends AsyncTask<Void, Integer, String> {

        // TAG
        private static final String TAG = "DatabaseConnect";
        // Database Helper
        BusTrackerDBHelper db;
        // url
        private static final String GET_COORDINATES_URL = "http://ashoka.students.acg.edu/BusTrackerAndroid/webServices/downloadJsonFile.php";
        // success & routes tags (in response)
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";
        private static final String TAG_ROUTES = "routes";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Get the progressBar
            progressBar = (ProgressBar)findViewById(R.id.progressBar);
            progressBar.setMax(progressBarMax);
        }

        @Override
        protected String doInBackground(Void... params) {
            // Check for success tag
            int success;
            // Progress
            int progress = 0;

            // Initialize the BusTrackerDBHelper
            db = new BusTrackerDBHelper(getApplicationContext());

            // Add the auto alarm at ID = 1
            db.addAlarm(DatabaseContract.AlarmsEntry.AUTO_DEFAULT, 1);

            try {
                JSONObject json = jsonParser.getJSONFromUrl(GET_COORDINATES_URL);

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d(TAG, "JSON response: " + json.toString());

                        JSONArray routesArray = new JSONArray(json.getString(TAG_ROUTES));
                        JSONObject routeObject;
                        JSONObject routeStopObject, snappedPointObject;

                        // Fill the Routes Table
                        for (int i = 0; i < routesArray.length(); i++) {
                            // Get the JSON object representing the route
                            routeObject = routesArray.getJSONObject(i);
                            db.addRoute(
                                    Integer.parseInt(routeObject.getString("ID")),
                                    routeObject.getString("nameENG"),
                                    routeObject.getString("nameGR"),
                                    routeObject.getString("school")
                            );
                            publishProgress(++progress);


                            // Fill the RouteStops Table
                            for (int j = 0; j < routeObject.getJSONArray("routeStops").length(); j++) {
                                // Get the JSON object representing the stationPoint
                                routeStopObject = routeObject.getJSONArray("routeStops").getJSONObject(j);
                                db.addRouteStop(
                                        Integer.parseInt(routeStopObject.getString("ID")),
                                        Integer.parseInt(routeStopObject.getString("routeID")),
                                        routeStopObject.getString("stopTime"),
                                        routeStopObject.getString("nameOfStopGR"),
                                        routeStopObject.getString("nameOfStopENG"),
                                        routeStopObject.getString("description"),
                                        Double.parseDouble(routeStopObject.getString("lat")),
                                        Double.parseDouble(routeStopObject.getString("lng"))
                                );
                                publishProgress(++progress);
                            }

                            // Fill the SnappedPoints Table
                            for (int z = 0; z < routeObject.getJSONArray("snappedPoints").length(); z++) {
                                // Get the JSON object representing the snappedPoint
                                snappedPointObject = routeObject.getJSONArray("snappedPoints").getJSONObject(z);
                                db.addSnappedPoint(
                                        Integer.parseInt(snappedPointObject.getString("routeID")),
                                        Double.parseDouble(snappedPointObject.getJSONObject("location").getString("latitude")),
                                        Double.parseDouble(snappedPointObject.getJSONObject("location").getString("longitude")),
                                        snappedPointObject.getString("originalIndex"),
                                        snappedPointObject.getString("placeID")
                                );
                                publishProgress(++progress);
                            }

                        }


                    // testing
//                    asyncTaskTesting();

                    db.close();

                    return json.getString(TAG_ROUTES);
                } else {
                    Log.d(TAG,"Retrieving Failure! " + json.getString(TAG_MESSAGE));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        // Called from the publish progress
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
//            Log.e(TAG, "onProgressUpdate(): " + String.valueOf(values[0]));

            // Update the progress bar's progress
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String json_response) {
            if (json_response != null){
//                Log.e(TAG, "DONE!!");

                // Put Language SharedPreferences to English
                getSharedPreferences(MainActivity.PREFS_FILE, Activity.MODE_PRIVATE)
                        .edit()
                        .putString(MainActivity.LANGUAGE, MainActivity.ENG);
                // Start the main activity
                startMain();
            } else {

            }
        }

//        // Testing Method
//        public void asyncTaskTesting(){
//
//            // Show the routes
//            Log.e("Routes_Table", "Reading All Routes");
//            ArrayList<RouteDAO> routes = db.getAllRoutesDAO();
//            for (RouteDAO routeDAO : routes){
//                String log = "ID: " + routeDAO.getID() + ", NameGR: " + routeDAO.getNameGR() +
//                        ", NameENG: " + routeDAO.getNameENG() + ", School: " + routeDAO.getSchool();
//                Log.e("Route", log);
//            }
//
//            // Show the routeStops
//            Log.e("RouteStops_Table", "Reading All RouteStops");
//            ArrayList<RouteStopDAO> routeStops = db.getAllRouteStopsDAO();
//            for (RouteStopDAO routeStop : routeStops){
//                String log = "ID: " + routeStop.getID() +
//                        ", RouteID: " + routeStop.getRouteID() +
//                        ", StopTime: " + routeStop.getStopTime() +
//                        ", NameOfStopGR: " + routeStop.getNameOfStopGR() +
//                        ", NameOfStopENG: " + routeStop.getNameOfStopENG() +
//                        ", Description: " + routeStop.getDescription() +
//                        ", Latitude: " + routeStop.getLat() +
//                        ", Longitude: " + routeStop.getLng();
//                Log.e("RouteStop", log);
//            }
//
//            // Show the snappedPoints
//            Log.e("SnappedPoints_Table", "Reading All SnappedPoints");
//            ArrayList<SnappedPointDAO> snappedPoints = db.getAllSnappedPointsDAO();
//            for (SnappedPointDAO snappedPoint : snappedPoints) {
//                String log = "ID: " + snappedPoint.getID() +
//                        ", RouteID: " + snappedPoint.getRouteID() +
//                        ", Latitude: " + snappedPoint.getLat() +
//                        ", Longitude: " + snappedPoint.getLng() +
//                        ", OriginalIndex: " + snappedPoint.getOriginalIndex() +
//                        ", PlaceID: " + snappedPoint.getPlaceID();
//                Log.e("RouteStop", log);
//            }
//
//        }

    }


    /*
     * AsyncTask to check the size in rows
     */
    private class GetSizeAndDownloadAsyncTask extends AsyncTask<Void, Void, Void> {

        // TAG
        private static final String TAG = "GetSizeAsyncTask";
        // url
        private static final String GET_DB_SIZE_IN_ROWS_URL = "http://ashoka.students.acg.edu/BusTrackerAndroid/webServices/getDbSizeInRows.php";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                JSONObject json = jsonParser.getJSONFromUrl(GET_DB_SIZE_IN_ROWS_URL);
                progressBarMax = json.getInt("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            DbUpdateAsyncTask dbUpdateAsyncTask = new DbUpdateAsyncTask();
            dbUpdateAsyncTask.execute();
        }
    }


    /*
     * AsyncTask to update the progressBar if database is created
     */
    private class SplashAsyncTask extends AsyncTask<Void, Integer, Void> {

        // TAG
        private static final String TAG = "SplashAsyncTask";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Get the progressBar
            progressBar = (ProgressBar)findViewById(R.id.progressBar);
            progressBar.setMax(100);
        }

        @Override
        protected Void doInBackground(Void... params) {

            int i = 0;
            while (i <= 50) {
                try {
                    Thread.sleep(20);
                    publishProgress(i);
                    i++;
                }
                catch (Exception e) {
                    Log.i(TAG, e.getMessage());
                }
            }

            return null;
        }

        // Called from the publish progress
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
//            Log.e(TAG, "onProgressUpdate(): " + String.valueOf(values[0]));

            // Update the progress bar's progress
            progressBar.setProgress(values[0] * 2);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Start the main activity
            startMain();
        }
    }

}
