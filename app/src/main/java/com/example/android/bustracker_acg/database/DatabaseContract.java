package com.example.android.bustracker_acg.database;

import android.provider.BaseColumns;
import android.text.format.Time;

public class DatabaseContract {

    // To make it easy to query for the exact date,
    // we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
    // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /**
        Inner class that defines the table contents of the Routes table
     */
    public static final class RoutesEntry implements BaseColumns {

        public static final String TABLE_NAME = "Routes";

        // Column with the ID of the Routes
        public static final String COLUMN_ID = "ID";

        // The name of the route in Greek
        public static final String COLUMN_NAME_GR = "nameGR";

        // The name of the route in English
        public static final String COLUMN_NAME_ENG = "nameENG";

        // The name of the school
        public static final String COLUMN_SCHOOL = "school";

    }

    /**
        Inner class that defines the table contents of the RouteStops table
     */
    public static final class RouteStopsEntry implements BaseColumns {

        public static final String TABLE_NAME = "RouteStops";

        // Column with the ID of the RouteStops
        public static final String COLUMN_ID = "ID";

        // The routeID of the route this stop belongs to
        public static final String COLUMN_ROUTE_ID = "routeID";

        // The time a stop occurs
        public static final String COLUMN_STOP_TIME = "stopTime";

        // The name of the stop in Greek
        public static final String COLUMN_NAME_OF_STOP_GR = "nameOfStopGR";

        // The name of the stop in English
        public static final String COLUMN_NAME_OF_STOP_ENG = "nameOfStopENG";

        // A short description for the stop
        public static final String COLUMN_DESCRIPTION = "description";

        // The stop 's latitude
        public static final String COLUMN_LAT = "lat";

        // The stop 's longitude
        public static final String COLUMN_LNG = "lng";

    }

    /**
        Inner class that defines the table contents of the SnappedPoints table
     */
    public static final class SnappedPointsEntry implements BaseColumns {

        public static final String TABLE_NAME = "SnappedPoints";

        // Column with the auto_increment ID of the SnappedPoint
        public static final String COLUMN_ID = "ID";

        // The routeID of the route this stop belongs to
        public static final String COLUMN_ROUTE_ID = "routeID";

        // The snapped point 's latitude
        public static final String COLUMN_LAT = "lat";

        // The snapped point 's longitude
        public static final String COLUMN_LNG = "lng";

        // The original index
        public static final String COLUMN_ORIGINAL_INDEX = "originalIndex";

        // The placeID
        public static final String COLUMN_PLACE_ID = "placeID";

    }

    /**
        Inner class that defines the table contents of the Routes table
     */
    public static final class FaqEntry implements BaseColumns {

        public static final String TABLE_NAME = "FAQ";

        // Column with the ID of the FAQ
        public static final String COLUMN_ID = "ID";

        // The question in Greek
        public static final String COLUMN_QUESTION_GR = "questionGR";

        // The question in English
        public static final String COLUMN_QUESTION_ENG = "questionENG";

        // The answer in Greek
        public static final String COLUMN_ANSWER_GR = "answerGR";

        // The answer in English
        public static final String COLUMN_ANSWER_ENG = "answerENG";

    }


    /**
        Inner class that defines the table contents of the Alarms table
     */
    public static final class AlarmsEntry implements BaseColumns {

        public static final String TABLE_NAME = "Alarms";

        // Column with the ID of the Alarms
        public static final String COLUMN_ID = "ID";

        // The alarm 's time
        public static final String COLUMN_TIME = "time";

        // The alarm 's state
        public static final String COLUMN_STATE = "state";

        // Auto Alarm Default Time
        public static final String AUTO_DEFAULT = "NONE";

    }

}
