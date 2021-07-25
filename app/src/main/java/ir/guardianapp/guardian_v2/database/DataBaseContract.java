package ir.guardianapp.guardian_v2.database;

import android.provider.BaseColumns;

public final class DataBaseContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DataBaseContract() {
    }

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LocalDatabase.db";

    // ------------------   TRIPS   ----------------------------
    public static class TripsEntry implements BaseColumns {
        public static final String TRIPS_TABLE_NAME = "TRIPS_TABLE";
        public static final String SOURCE_NAME = "SOURCE";
        public static final String DEST_NAME = "DEST";
        public static final String START_DATE = "START";
        public static final String END_DATE = "END";
        public static final String DISTANCE = "DISTANCE";
    }

    final static String TRIPS_SQL_CREATE_TABLE = "CREATE TABLE " + TripsEntry.TRIPS_TABLE_NAME + " (" + TripsEntry._ID + " INTEGER PRIMARY KEY, "
            + TripsEntry.SOURCE_NAME + " TEXT, "
            + TripsEntry.DEST_NAME + " TEXT, "
            + TripsEntry.START_DATE + " TEXT, "
            + TripsEntry.END_DATE + " TEXT, "
            + TripsEntry.DISTANCE + " INTEGER)";

    public static final String TRIPS_SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TripsEntry.TRIPS_TABLE_NAME;


    // ------------------   DRIVING   ----------------------------
//    public static class DrivingEntry implements BaseColumns {
//        public static final String DRIVING_TABLE_NAME = "DRIVING_TABLE";
//        public static final String DRIVING_AVERAGE = "AVERAGE";
//        public static final String REGISTER_DATE = "DATE";
//        public static final String LATITUDE = "LATITUDE";
//        public static final String LONGITUDE = "LONGITUDE";
//
//        public static final String SLEEP = "SLEEP";
//        public static final String TIME = "TIME";
//        public static final String SPEED = "SPEED";
//        public static final String WITHOUT_STOP = "WITHOUT_STOP";
//        public static final String ROAD_TYPE = "ROAD_TYPE";
//        public static final String TRAFFIC = "TRAFFIC";
//        public static final String WEATHER = "WEATHER";
//        public static final String NEAR_CITIES = "NEAR_CITIES";
//        public static final String VIBRATION = "VIBRATION";
//        public static final String ACCELERATION = "ACCELERATION";
//        public static final String MONTH = "MONTH";
//    }
//
//    final static String DRIVING_SQL_CREATE_TABLE = "CREATE TABLE " + DrivingEntry.DRIVING_TABLE_NAME + " (" + DrivingEntry._ID + " INTEGER PRIMARY KEY, "
//            + DrivingEntry.DRIVING_AVERAGE + " REAL, "
//            + DrivingEntry.REGISTER_DATE + " TEXT, "
//            + DrivingEntry.LATITUDE + " REAL, "
//            + DrivingEntry.LONGITUDE + " REAL, "
//            + DrivingEntry.SLEEP + " REAL, "
//            + DrivingEntry.SPEED + " REAL, "
//            + DrivingEntry.WITHOUT_STOP + " REAL, "
//            + DrivingEntry.ROAD_TYPE + " REAL, "
//            + DrivingEntry.TRAFFIC + " REAL, "
//            + DrivingEntry.WEATHER + " REAL, "
//            + DrivingEntry.NEAR_CITIES + " REAL, "
//            + DrivingEntry.VIBRATION + " REAL, "
//            + DrivingEntry.ACCELERATION + " REAL, "
//            + DrivingEntry.MONTH + " REAL)";
//
//    public static final String DRIVING_SQL_DELETE_ENTRIES =
//            "DROP TABLE IF EXISTS " + DrivingEntry.DRIVING_TABLE_NAME;

}
