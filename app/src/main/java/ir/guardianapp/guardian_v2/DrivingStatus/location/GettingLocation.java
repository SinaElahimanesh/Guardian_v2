package ir.guardianapp.guardian_v2.DrivingStatus.location;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class GettingLocation extends AppCompatActivity {

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    int counter;
    double[] result = new double[31];
    double min;
    int cityIndex;
    double gettingLatitude;
    double gettingLongtitude;

    String[] cities = {"Tehran", "Mashhad", "Isfahan", "Karaj", "Shiraz", "Tabriz", "Qom", "Ahvaz", "Kermanshah", "Orumiyeh", "Rasht", "Zahedan", "Kerman",
            "Yazd", "Ardabil", "BandarAbbas", "Arak", "Zanjan", "Sanandaj", "Qazvin", "Khorramabad", "Gorgan", "Sari", "Bojnourd", "Bushehr", "Birjand",
            "Ilam", "Shahrekord", "Semnan", "Yasuj", "Hamedan"};
    double[] longtitude = {51.42151, 59.56796, 51.67462, 50.99155, 52.53113, 46.2919, 50.8764, 48.6842, 47.065, 45.07605, 49.58319, 60.8629, 57.07879,
            54.3675, 48.2933, 56.2808, 49.68916, 48.4787, 46.99883, 50.0041, 48.35583, 54.44361, 53.06009, 57.333332, 50.8166634, 59.22114, 46.4227,
            50.8556632, 53.9191629, 51.58796, 48.5166646};
    double[] latitude = {35.69439, 36.31559, 32.65246, 35.83226, 29.61031, 38.08, 34.6401, 31.31901, 34.31417, 37.55274, 37.28077, 29.4963, 30.28321,
            31.89722, 38.2498, 27.1865, 34.09174, 36.6736, 35.31495, 36.26877, 33.48778, 36.84165, 36.56332, 37.47166478, 28.9833294, 32.86628, 33.6374,
            32.32333204, 35.23416573, 30.66824, 34.7999968};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    Log.d("latitude TextView", gettingLatitude + "");
                                    Log.d("Longtitude TextView", gettingLongtitude + "");
                                    gettingLatitude = location.getLatitude();
                                    gettingLongtitude = location.getLongitude();
                                    Location destLocation = new Location("hellllo");


                                    for (counter = 0; counter < 31; counter++) {

                                        destLocation.setLatitude(latitude[counter]);
                                        destLocation.setLongitude(longtitude[counter]);
                                        location.setLongitude(location.getLongitude());
                                        location.setLatitude(location.getLatitude());
                                        result[counter] = location.distanceTo(destLocation);
                                    }

                                    min = result[0];
                                    cityIndex = 0;
                                    for (counter = 0; counter < 31; counter++) {
                                        if (result[counter] < min) {
                                            min = result[counter];
                                            cityIndex = counter;
                                        }
                                    }


                                    Log.d("cityTextView", "nearest city is " + cities[cityIndex]);
                                    Log.d("distanceTextView", "your distance from this city is " + result[cityIndex] + "");
                                    if (result[cityIndex] < 31000) {
                                        Log.d("areaTextView", "you are in area of " + cities[cityIndex]);
                                    }

                                    requestNewLocationData();

                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        @SuppressLint("RestrictedApi") LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //in this lines we update location
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        //because we want to update location we comment this line
        //mLocationRequest.setNumUpdates(50);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.d("latitudeTextView", mLastLocation.getLatitude() + "");
            Log.d("longtitudeTextView", mLastLocation.getLongitude() + "");
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }

    public double GetLastLatitude() {
        return gettingLatitude;
    }

    public double GetLastLongtitude() {
        return gettingLongtitude;
    }

    public String NearestCity () {
        return cities[cityIndex];
    }
}