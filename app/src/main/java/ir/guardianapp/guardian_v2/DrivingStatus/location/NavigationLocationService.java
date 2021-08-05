package ir.guardianapp.guardian_v2.DrivingStatus.location;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import ir.guardianapp.guardian_v2.DrivingPercentage.StatusCalculator;
import ir.guardianapp.guardian_v2.MainNavigationActivity;

public class NavigationLocationService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    ArrayList<Double> speedArray = new ArrayList<>();
    ArrayList<Long> timeArray = new ArrayList<>();
    ArrayList<Long> timeSaving = new ArrayList<>();
    ArrayList<Long> nonStopDriving = new ArrayList<>();
    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation, lStart, lEnd;
    static double distance = 0;
    double speed;
    int counter;
    private boolean first = true;
    private long firstTime;
    private long stopTime = 0;
    private double minspeed , maxspeed;
    private boolean firstArray = true;
    private final IBinder mBinder = new LocalBinder();
    long endingTime;
    boolean firstSpeedTime = true;
    long saveSpeedTime;
    long nonStopDrivingTime;
    Long firstSpeedStart = Long.valueOf(0);
    boolean firstForSpeed = false;
    long nonStopDrivingShow;
    long totalRest = 0;
    StatusCalculator statusCalculator;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        return mBinder;
    }

    public void setStatusCalculator(StatusCalculator statusCalculator) {
        this.statusCalculator = statusCalculator;
    }

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onConnected(Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
        }
    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        distance = 0;
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {
        MainNavigationActivity.locate.dismiss();
        mCurrentLocation = location;
        if (lStart == null) {
            lStart = mCurrentLocation;
            lEnd = mCurrentLocation;
        } else
            lEnd = mCurrentLocation;

        //Calling the method below updates the  live values of distance and speed to the TextViews.
        updateUI();
        //calculating the speed with getSpeed method it returns speed in m/s so we are converting it into kmph
        speed = location.getSpeed() * 18 / 5;

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class LocalBinder extends Binder {

        public NavigationLocationService getService() {
            return NavigationLocationService.this;
        }


    }

    //The live feed of Distance and Speed are being set in the method below .
    private double updateUI() {
        if (MainNavigationActivity.p == 0) {
            MainNavigationActivity.locate.dismiss();
            distance = distance + (lStart.distanceTo(lEnd) / 1000.00);
            MainNavigationActivity.endTime = System.currentTimeMillis();
            endingTime = TimeUnit.MILLISECONDS.toMinutes(MainNavigationActivity.endTime);
            long diff = MainNavigationActivity.endTime - MainNavigationActivity.startTime;
            diff = TimeUnit.MILLISECONDS.toMinutes(diff);
            //Log.d("time", "Total Time: " + diff + " minutes");
            StatusCalculator.totalDrive = diff - totalRest;
            //non stop
            if(firstSpeedStart == Long.valueOf(0)) {
                firstSpeedStart = endingTime;
            }
            nonStopDrivingShow = endingTime - firstSpeedStart;
            Log.d("non stop driving", String.valueOf(nonStopDrivingShow));
            MainNavigationActivity.driveText.setText(String.valueOf(nonStopDrivingShow));
            StatusCalculator.nonStop = nonStopDrivingShow;
            if (speed >= 0.0) {
                Log.d("speed", "Current speed: " + new DecimalFormat("#.##").format(speed) + " km/hr");
//                StatusCalculator.staticUserSpeed = speed;
                statusCalculator.singleSpeedCall(speed);
                if(speed >= 4.5) {
                    MainNavigationActivity.speedText.setText(new DecimalFormat("#.#").format(speed));
                } else {
                    MainNavigationActivity.speedText.setText("0");
                }


                if (speed <= 9.0 ) {

                    if (first) {
                        firstTime = System.currentTimeMillis();
                        firstTime = TimeUnit.MILLISECONDS.toMinutes(firstTime);
                        Log.d("start stop time", String.valueOf(firstTime));
                        first = false;
                    }

                }
                else {
                    first = true;
                    //non stop
                    if(firstForSpeed) {
                        firstSpeedStart = endingTime;
                        firstForSpeed = false;
                    }

                    if(firstSpeedTime) {
                        saveSpeedTime = endingTime;
                        stopTime = endingTime - firstTime;
                        timeArray.add(stopTime);
                        firstSpeedTime = false;
                        //non stop
                        nonStopDrivingTime = endingTime - firstSpeedStart;
                        nonStopDriving.add(nonStopDrivingTime);
                        firstForSpeed = true;
                        nonStopDrivingTime = 0;
                        nonStopDrivingShow = 0;
                    }
                }

                if (speed <= 9.0 && (endingTime - firstTime) >= 5) {
                    Log.d("stop text", "you have been stopped");
                    firstSpeedTime = true;
                    // non stop
                    nonStopDrivingTime = endingTime - firstSpeedStart;
                    nonStopDriving.add(nonStopDrivingTime);
                    firstForSpeed = true;
                    nonStopDrivingTime = 0;
                    nonStopDrivingShow = 0;
                    firstSpeedStart = Long.valueOf(0);
                }
                else if(speed > 9.0) {
                    Log.d("stop text", "You are driving now");
                    firstTime = endingTime;
                }

            } else {
                Log.d("speed", ".......");
                MainNavigationActivity.speedText.setText("...");
            }
            Log.d("distination", new DecimalFormat("#.###").format(distance) + " Km's.");

            lStart = lEnd;

            speedArray.add(counter , speed);
            timeSaving.add(counter, MainNavigationActivity.endTime);
            MeasureAcceleration(speedArray, timeSaving);

            if (firstArray) {
                minspeed = 0;
                maxspeed = 0;
                first = false;
            }
            if (minspeed > speedArray.get(counter)) {
                minspeed = speedArray.get(counter);
            }
            if(maxspeed < speedArray.get(counter)) {
                maxspeed = speedArray.get(counter);
            }
            if(speedArray.size() == 16) {
                speedArray.remove(0);
            }

            if(timeArray != null) {
                for(counter = 0; counter < timeArray.size(); counter++) {
                    totalRest = totalRest + timeArray.get(counter);
                }
            }
            else {
                totalRest = 0;
            }

            StatusCalculator.totalTime = totalRest;

        }
        return speed;
    }

    public double MeasureAcceleration(ArrayList<Double> speedArray, ArrayList<Long> timeSaving) {
        double deltav;
        long deltat;
        double acceleration;
        if (speedArray.size() < 10 || timeSaving.size() < 10) {
//            StatusCalculator.acceleration = 0;
            statusCalculator.singleAccelerationCall(0);
            return 0;
        }
        else {
            deltav = speedArray.get(4) - speedArray.get(0);
            deltat = timeSaving.get(4) - timeSaving.get(0);
            acceleration = deltav / deltat;
//            StatusCalculator.acceleration = acceleration;
            statusCalculator.singleAccelerationCall(acceleration);
            return acceleration;
        }


    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopLocationUpdates();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        lStart = null;
        lEnd = null;
        distance = 0;
        return super.onUnbind(intent);
    }

    public double returnSeedTolarance() {
        double speedTolarance = maxspeed - minspeed;
        return speedTolarance;
    }

    public long returnStopTime() {
        return stopTime;
    }

    public double GetSpeed() {
        return speed;
    }
}