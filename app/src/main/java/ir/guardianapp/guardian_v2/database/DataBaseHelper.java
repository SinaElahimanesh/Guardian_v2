package ir.guardianapp.guardian_v2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

import ir.guardianapp.guardian_v2.models.DriveStatusPercentage;
import ir.guardianapp.guardian_v2.models.Trip;

public class DataBaseHelper extends SQLiteOpenHelper {

    public SQLiteDatabase db;
    private final ExecutorService executorService;

    public DataBaseHelper(@Nullable Context context, ExecutorService executorService) {
        // Create DB
        super(context, DataBaseContract.DATABASE_NAME, null, DataBaseContract.DATABASE_VERSION);
        this.executorService = executorService;
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create Tables
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                sqLiteDatabase.execSQL(DataBaseContract.TRIPS_SQL_CREATE_TABLE);
            }
        });
//        executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                sqLiteDatabase.execSQL(DataBaseContract.DRIVING_SQL_CREATE_TABLE);
//            }
//        });
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DataBaseContract.TRIPS_SQL_DELETE_ENTRIES);
//        sqLiteDatabase.execSQL(DataBaseContract.DRIVING_SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean addTrip(Trip trip) {
        ContentValues cv = new ContentValues();

        cv.put(DataBaseContract.TripsEntry.SOURCE_NAME, trip.getSourceName());
        cv.put(DataBaseContract.TripsEntry.DEST_NAME, trip.getDestinationName());
        cv.put(DataBaseContract.TripsEntry.START_DATE, trip.getStartDate().toString());
        cv.put(DataBaseContract.TripsEntry.END_DATE, trip.getEndDate().toString());
        cv.put(DataBaseContract.TripsEntry.DISTANCE, trip.getDistanceInKM());
        final long[] insert = new long[1];

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                insert[0] = db.insert(DataBaseContract.TripsEntry.TRIPS_TABLE_NAME, null, cv);
                db.close();
            }
        });
        return insert[0] != -1;
    }

//    public boolean addDriving(DriveStatusPercentage driveStatusPercentage) {
//        ContentValues cv = new ContentValues();
//
//        cv.put(DataBaseContract.DrivingEntry.DRIVING_AVERAGE, driveStatusPercentage.getDRIVING_AVERAGE());
//        cv.put(DataBaseContract.DrivingEntry.REGISTER_DATE, driveStatusPercentage.getREGISTER_DATE());
//        cv.put(DataBaseContract.DrivingEntry.LATITUDE, driveStatusPercentage.getLATITUDE());
//        cv.put(DataBaseContract.DrivingEntry.LONGITUDE, driveStatusPercentage.getLONGITUDE());
//        cv.put(DataBaseContract.DrivingEntry.SLEEP, driveStatusPercentage.getSLEEP());
//        cv.put(DataBaseContract.DrivingEntry.TIME, driveStatusPercentage.getTIME());
//        cv.put(DataBaseContract.DrivingEntry.SPEED, driveStatusPercentage.getSPEED());
//        cv.put(DataBaseContract.DrivingEntry.WITHOUT_STOP, driveStatusPercentage.getWITHOUT_STOP());
//        cv.put(DataBaseContract.DrivingEntry.ROAD_TYPE, driveStatusPercentage.getROAD_TYPE());
//        cv.put(DataBaseContract.DrivingEntry.TRAFFIC, driveStatusPercentage.getTRAFFIC());
//        cv.put(DataBaseContract.DrivingEntry.WEATHER, driveStatusPercentage.getWEATHER());
//        cv.put(DataBaseContract.DrivingEntry.NEAR_CITIES, driveStatusPercentage.getNEAR_CITIES());
//        cv.put(DataBaseContract.DrivingEntry.VIBRATION, driveStatusPercentage.getVIBRATION());
//        cv.put(DataBaseContract.DrivingEntry.ACCELERATION, driveStatusPercentage.getACCELERATION());
//        cv.put(DataBaseContract.DrivingEntry.MONTH, driveStatusPercentage.getMONTH());
//
//        final long[] insert = new long[1];
//
//        executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                insert[0] = db.insert(DataBaseContract.DrivingEntry.DRIVING_TABLE_NAME, null, cv);
//                db.close();
//            }
//        });
//        return insert[0] != -1;
//    }

    // It is not needed!
//    public void deleteBookmark(Trip trip) {
//        CharSequence name = bookmark.getName();
//        String query = "DELETE FROM " + BOOKMARK_TABLE + " WHERE " + COLUMN_BOOKMARK_NAME + " = '" + String.valueOf(name) + "'";
//        Log.i(TAG, query);
//        executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                db.delete(BOOKMARK_TABLE, COLUMN_BOOKMARK_NAME + " = '" + String.valueOf(name) + "'", null);
////                db.close();
//            }
//        });
//    }

    public void getAllTrips() {
        String query = "SELECT * FROM " + DataBaseContract.TripsEntry.TRIPS_TABLE_NAME;
        executorService.submit(new GettingBookmarksRunnable(db, query));
    }

    // It is not needed!
//    public void deleteAllBookmarks() {
//        executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                db.delete(BOOKMARK_TABLE, null, null);
//                Bookmark.deleteBookmarks();
////                db.close();
//            }
//        });
//    }

    private double DRIVING_AVERAGE = 0;
    private double REGISTER_DATE = 0;
    private double LATITUDE = 0;
    private double LONGITUDE = 0;

    private double SLEEP = 0;
    private double TIME = 0;
    private double SPEED = 0;
    private double WITHOUT_STOP = 0;
    private double ROAD_TYPE = 0;
    private double TRAFFIC = 0;
    private double WEATHER = 0;
    private double NEAR_CITIES = 0;
    private double VIBRATION = 0;
    private double ACCELERATION = 0;
    private double MONTH = 0;

    static private class GettingBookmarksRunnable implements Runnable {
        SQLiteDatabase db;
        String query;

        private GettingBookmarksRunnable(SQLiteDatabase db, String query) {
            this.db = db;
            this.query = query;
        }

        @Override
        public void run() {
            List<Trip> returnedList = new ArrayList<>();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    String source_name = cursor.getString(1);
                    String dest_name = cursor.getString(2);
                    Date start_date = new Date(Date.parse(cursor.getString(3)));
                    Date end_date = new Date(Date.parse(cursor.getString(4)));
                    int distanceInKM = cursor.getInt(5);
                    Trip trip = new Trip(source_name, dest_name, start_date, end_date, distanceInKM);
                    returnedList.add(trip);
                } while (cursor.moveToNext());
                Trip.addAllTrips(returnedList);
            }
            cursor.close();
            db.close();
        }

    }
}
