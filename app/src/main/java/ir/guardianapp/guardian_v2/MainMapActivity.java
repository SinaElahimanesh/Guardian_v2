package ir.guardianapp.guardian_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import ir.guardianapp.guardian_v2.DrivingPercentage.DriveAlertHandler;
import ir.guardianapp.guardian_v2.DrivingPercentage.StatusCalculator;
import ir.guardianapp.guardian_v2.DrivingStatus.ShakeSituation;
import ir.guardianapp.guardian_v2.DrivingStatus.location.GPSTracker;
import ir.guardianapp.guardian_v2.DrivingStatus.location.MapLocationService;
import ir.guardianapp.guardian_v2.DrivingStatus.weather.Weather;
import ir.guardianapp.guardian_v2.database.JSONManager;
import ir.guardianapp.guardian_v2.database.SharedPreferencesManager;
import ir.guardianapp.guardian_v2.extras.BitmapHelper;
import ir.guardianapp.guardian_v2.extras.BoxAnimationState;
import ir.guardianapp.guardian_v2.extras.GPSAndInternetChecker;
import ir.guardianapp.guardian_v2.extras.GuideManager;
import ir.guardianapp.guardian_v2.extras.Network;
import ir.guardianapp.guardian_v2.extras.NumberHandler;
import ir.guardianapp.guardian_v2.models.User;
import ir.guardianapp.guardian_v2.network.MessageResult;
import ir.guardianapp.guardian_v2.network.ThreadGenerator;

public class MainMapActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback, LocationListener {

    // Sensors
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private boolean isAccelerometerSensorAvailable, several = false;
    public float currentX, currentY, currentZ, lastX, lastY, lastZ, xDifference, yDifference, zDifference;
    private ShakeSituation situation = ShakeSituation.noShake;

    // Speedometer
    public MapLocationService myService;
    private static boolean status;
    private LocationManager locationManager;
    public static long startTime, endTime;
    public static ProgressDialog locate;
    public static int p = 0;

    // map
    private View locationButton;
    private boolean restComplex = false;
    private Marker mapMarker;
    private GoogleMap mMap;
    private Location mapLocation;

    // view
    private TextView weatherTypeTxt;
    private ImageView weatherTypeImg;

    // Menu
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;

    // Algorithm
    private TextView algorithmPercentageText;
    private TextView algorithmStatusText;
    private ImageView algorithmBackground;
    private TextView alertMessageText;
    private FrameLayout alertMessageBox;
    private ImageView alertMessageImage;
    private StatusCalculator statusCalculator;
    public static TextView speedText;
    public static TextView driveText;
    private ImageButton parkingButton;
    private ImageButton moreButton;

    // Permission
    private final int REQ_CODE = 100;

    // onCreate VARS
    private Handler algorithmCallerHandler;
    public Runnable algorithmCallerRunnable;
    private boolean isPaused = false;
    private static boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        // FIND_VIEWS
        ImageButton restButton = findViewById(R.id.restButton);
        ImageButton back2mapButton = findViewById(R.id.back2mapButton);
        //
        weatherTypeImg = findViewById(R.id.WeatherTypeImage);
        weatherTypeTxt = findViewById(R.id.WeatherTypeTextView);
        speedText = findViewById(R.id.speedTextView);
        driveText = findViewById(R.id.driveTextView);
        //
        algorithmPercentageText = findViewById(R.id.driving_percentage);
        algorithmStatusText = findViewById(R.id.driving_status);
        algorithmBackground = findViewById(R.id.driving_background);
        alertMessageText = findViewById(R.id.alertMessageText);
        alertMessageBox = findViewById(R.id.alertMessageBox);
        alertMessageImage = findViewById(R.id.alertMessageImage);
        parkingButton = findViewById(R.id.parkingButton);
        moreButton = findViewById(R.id.moreButton);
        parkingButton.setOnClickListener(v -> saveParking());
        statusCalculator = new StatusCalculator(this);
        statusCalculator.setSleepData(this);

        // MAKE IT FULL SCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorBar));
        }

        // MAP LANG
        String languageToLoad = "fa_IR";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // REST
        activateRestButton(restButton, back2mapButton);

        // Vibration
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerSensorAvailable = true;
        } else {
            isAccelerometerSensorAvailable = false;
        }

        // Speedometer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }

        //The method below checks if Location is enabled on device or not. If not, then an alert dialog box appears with option to enable gps.
        checkGps();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return;
        }

        if (!status)
            //Here, the Location Service gets bound and the GPS Speedometer gets Active.
            bindService();
        if (firstTime) {
            locate = new ProgressDialog(MainMapActivity.this);
            locate.setIndeterminate(true);
            locate.setCancelable(false);
            locate.setMessage("در حال دریافت موقعیت مکانی...");
            locate.show();
            firstTime = false;
        }

        // Drawer
        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        Button button = findViewById(R.id.menuButton);
        button.setOnClickListener(v -> openDrawer());

        // first time calling algorithm
        callAlgorithmLogic();
        //final Handler
        algorithmCallerHandler = new Handler(Looper.getMainLooper());
        algorithmCallerHandler.postDelayed(algorithmCallerRunnable = new Runnable() {
            @Override
            public void run() {
                System.gc();
                //call function
                callAlgorithmLogic();
                algorithmCallerHandler.postDelayed(this, 30000);
            }
        }, 30000);

        // Guide
//        if (MainActivity.getShowGuide()) {
//            GuideManager.showGuide(this);
//        }
    }

    private void activateRestButton(ImageButton restButton, ImageButton back2mapButton) {
        // REST
        back2mapButton.setOnClickListener(v -> {
            restComplex = false;
            FrameLayout percentBox = findViewById(R.id.percentBox);
            ObjectAnimator animation = ObjectAnimator.ofFloat(percentBox, "translationY", 0);
            animation.setDuration(500);
            animation.start();

            if(mapMarker != null)
                mapMarker.remove();
            if (mapLocation != null && mMap != null)
                updateCameraBearing(mMap, mapLocation);

            back2mapButton.setVisibility(View.INVISIBLE);
            restButton.setVisibility(View.VISIBLE);
            showAlertBox();
        });
        restButton.setOnClickListener((View.OnClickListener) v -> {
            restComplex = true;
            FrameLayout percentBox = findViewById(R.id.percentBox);
            ObjectAnimator animation = ObjectAnimator.ofFloat(percentBox, "translationY", -600);
            animation.setDuration(750);
            animation.start();

            if (mapLocation != null && mMap != null)
                updateCameraBearing(mMap, mapLocation);

            back2mapButton.setVisibility(View.VISIBLE);
            restButton.setVisibility(View.INVISIBLE);

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                restComplex = false;
                ObjectAnimator animation1 = ObjectAnimator.ofFloat(percentBox, "translationY", 0);
                animation1.setDuration(500);
                animation1.start();

                mapMarker.remove();
                if (mapLocation != null && mMap != null)
                    updateCameraBearing(mMap, mapLocation);

                back2mapButton.setVisibility(View.INVISIBLE);
                restButton.setVisibility(View.VISIBLE);

                showAlertBox();
            }, 10000);
        });
    }

    private void showAlertBox() {
        if (!restComplex) {
            String toShowAlert = DriveAlertHandler.toShowAlert();
            if (toShowAlert.equalsIgnoreCase("")) {
                alertMessageText.setText("با دقت به رانندگی ادامه دهید.");
                alertMessageBox.setBackgroundResource(R.drawable.rectangle_alert_background_green);
//            alertMessageText.setTextColor(Color.BLACK);
                alertMessageImage.setImageResource(R.drawable.warning_white);
            } else if (DriveAlertHandler.getCurrentAlertType() == DriveAlertHandler.Type.REST_AREA) {
                alertMessageText.setText(toShowAlert);
                alertMessageBox.setBackgroundResource(R.drawable.rectangle_alert_background_orange);
                alertMessageImage.setImageResource(R.drawable.coffee_white);
            } else {
                alertMessageText.setText(toShowAlert);
                alertMessageBox.setBackgroundResource(R.drawable.rectangle_alert_background_red);
                alertMessageImage.setImageResource(R.drawable.alert_icon);
            }
        }
        alertMessageBox.getLayoutParams().height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertMessageBox.requestLayout();
        alertMessageBox.setMinimumHeight(39);
    }

    // MAP
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
//                mMarker = mMap.addMarker(new MarkerOptions().position(loc));
            mapLocation = location;
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.8f));
                updateCameraBearing(mMap, location);
            }
        }
    };

    private void updateCameraBearing(GoogleMap googleMap, Location location) {
        if (googleMap == null) return;

        if (restComplex & mMap != null & location != null) {

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            GPSTracker gpsTracker = new GPSTracker(this);
            LatLng latLng1 = new LatLng(gpsTracker.getNearestPlaceLatitude(), gpsTracker.getNearestPlaceLangtitude());

            // Creating a marker
            MarkerOptions markerOptions = new MarkerOptions();

            // Setting the position for the marker
            markerOptions.position(latLng1);

            // Setting the title for the marker.
            // This will be displayed on taping the marker
            markerOptions.title(latLng1.latitude + " : " + latLng1.longitude);

            int height = 110;
            int width = 110;
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.petrol);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

            markerOptions.title(gpsTracker.getPlaceName())
//                    .snippet("Population: 4,627,300")
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            // Clears the previously touched position
            googleMap.clear();

            // Animating to the touched position
//        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            // Placing a marker on the touched position
            mapMarker = googleMap.addMarker(markerOptions);
            mapMarker.showInfoWindow();

            ArrayList<Marker> markers = new ArrayList<>();
            markers.add(mapMarker);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            builder.include(latLng);

            LatLngBounds bounds = builder.build();

            int padding = 250; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            googleMap.animateCamera(cu);

            double nearestRestComplexDistance = gpsTracker.getMinPlaceDistance() / 1000;
            String nearestRestComplexName = gpsTracker.getPlaceName();
            String restComplexDist = "";
            if (nearestRestComplexDistance <= 5) {
                restComplexDist = "کمتر از ۵ کیلومتر با شما فاصله دارد.";
            } else if (nearestRestComplexDistance <= 10) {
                restComplexDist = "کمتر از ۱۰ کیلومتر با شما فاصله دارد.";
            } else if (nearestRestComplexDistance <= 20) {
                restComplexDist = "کمتر از ۲۰ کیلومتر با شما فاصله دارد.";
            } else if (nearestRestComplexDistance <= 30) {
                restComplexDist = "کمتر از ۳۰ کیلومتر با شما فاصله دارد.";
            } else if (nearestRestComplexDistance <= 50) {
                restComplexDist = "کمتر از ۵۰ کیلومتر با شما فاصله دارد.";
            } else if (nearestRestComplexDistance <= 80) {
                restComplexDist = "کمتر از ۸۰ کیلومتر با شما فاصله دارد.";
            } else {
                restComplexDist = "نزدیک ترین مجتمع رفاهی به شماست!";
            }

            alertMessageText.setText("«" + nearestRestComplexName + "» " + restComplexDist);
            alertMessageBox.setBackgroundResource(R.drawable.rectangle_alert_background_orange);
//            alertMessageText.setTextColor(Color.BLACK);
            alertMessageImage.setImageResource(R.drawable.coffee_white);
            alertMessageBox.getLayoutParams().height = WindowManager.LayoutParams.WRAP_CONTENT;
            alertMessageBox.requestLayout();
            alertMessageBox.setMinimumHeight(39);

        } else {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to current location
                    .zoom(15.8f)                   // Sets the zoom
                    .bearing(location.getBearing()) // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location arg0) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                    }
                });
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        // theme of map
        if(SettingsFragment.getMapStyle(this) != null)
            mMap.setMapStyle(SettingsFragment.getMapStyle(this));
        // settings of map
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission
                            (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 1);
                //return;
            } else {
                googleMap.setMyLocationEnabled(true);
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

                if (location != null) {
                    LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(15.8f).build(); ///15.4f
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        }
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);

        //
        locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_BOTTOM, 0);
        rlp.addRule(RelativeLayout.ALIGN_BOTTOM, RelativeLayout.TRUE);
        locationButton.setLeft(100);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        rlp.setMargins(0, (height - 250), 110, 0);

        locationButton.post(() -> locationButton.performClick());
        setUpMapIfNeeded();

        showParkingLocation(this, mMap);

        View mapView = findViewById(R.id.map);
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp2 = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp2.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp2.setMargins(0, 0, 90, 30);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // permission not granted
        } else {
            // permission granted
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(15.8f).build(); //15.2
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    // Speedometer
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MapLocationService.LocalBinder binder = (MapLocationService.LocalBinder) service;
            myService = binder.getService();
            status = true;
            myService.setStatusCalculator(statusCalculator);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status = false;
        }
    };

    void bindService() {
        if (status == true)
            return;
        Intent i = new Intent(getApplicationContext(), MapLocationService.class);
        bindService(i, sc, BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();
    }

    void unbindService() {
        if (status == false)
            return;
        Intent i = new Intent(getApplicationContext(), MapLocationService.class);
        unbindService(sc);
        status = false;
    }

    //This method leads you to the alert dialog box.
    void checkGps() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser();
        }
    }

    //This method configures the Alert Dialog box.
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        (dialog, id) -> {
                            Intent callGPSSettingIntent = new Intent(
                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(callGPSSettingIntent);
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                (dialog, id) -> dialog.cancel());
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    //

    // Vibration
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        currentX = sensorEvent.values[0];
        currentY = sensorEvent.values[1];
        currentZ = sensorEvent.values[2];
        if (several) {

            xDifference = Math.abs(lastX - currentX);
            yDifference = Math.abs(lastY - currentY);
            zDifference = Math.abs(lastZ - currentZ);

            if ((xDifference > 6f && yDifference > 6f)
                    || (xDifference > 6f && zDifference > 6f)
                    || (yDifference > 6f && zDifference > 6f)) {
//                Log.d("shake situation", ShakeSituation.veryHighShake.toString());
                situation = ShakeSituation.veryHighShake;
            } else if ((xDifference > 5f && yDifference > 5f)
                    || (xDifference > 5f && zDifference > 5f)
                    || (yDifference > 5f && zDifference > 5f)) {
//                Log.d("shake situation", ShakeSituation.highShake.toString());
                situation = ShakeSituation.highShake;
            } else if ((xDifference > 3.9f && yDifference > 3.9f)
                    || (xDifference > 3.9f && zDifference > 3.9f)
                    || (yDifference > 3.9f && zDifference > 3.9f)) {
//                Log.d("shake situation", ShakeSituation.mediumShake.toString());
                situation = ShakeSituation.mediumShake;
            } else if ((xDifference > 2.8f && yDifference > 2.8f)
                    || (xDifference > 2.8f && zDifference > 2.8f)
                    || (yDifference > 2.8f && zDifference > 2.8f)) {
//                Log.d("shake situation", ShakeSituation.lowShake.toString());
                situation = ShakeSituation.lowShake;
            } else {
//                Log.d("shake situation", ShakeSituation.noShake.toString());
                situation = ShakeSituation.noShake;
            }
        }

        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;
        several = true;

        statusCalculator.singleVibrateCall(situation);
    }


    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
        if (MainActivity.getShowGuide()) {
            GuideManager.showGuide(this);
        }
        if (isAccelerometerSensorAvailable) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

//        if (mMap != null)
//            showParkingLocation(this, mMap);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Network.isNetworkAvailable(this)) {   // connected to internet
            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == MessageResult.SUCCESSFUL) {
                       //
                    } else {
                        try {
                            JSONManager.writeJSONArrIntoJSONFile(MainMapActivity.this);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            MainActivity.executorService.submit(ThreadGenerator.postDrivingDetails(User.getInstance().getUsername(),
                    User.getInstance().getToken(), JSONManager.getDrivingJSONArray(), handler));
        } else {
            Toast.makeText(this, "اتصال شما به اینترنت برقرار نمی باشد.", Toast.LENGTH_SHORT).show();
        }
    }

    // Drawer
    public void openDrawer() {
        mDrawer.openDrawer(GravityCompat.START);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (item.getItemId() == android.R.id.home) {
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        switch (menuItem.getItemId()) {
            case R.id.back2home:
                Intent i1 = new Intent(MainMapActivity.this, MainMenuActivity.class);
                startActivity(i1);
                break;
            case R.id.support:
                Intent i2 = new Intent(MainMapActivity.this, SupportActivity.class);
                startActivity(i2);
                break;
            case R.id.info:
                Intent i3 = new Intent(MainMapActivity.this, InfoActivity.class);
                startActivity(i3);
                break;
        }
        mDrawer.closeDrawers();
    }

    private void setWeatherImage(String url) {
        runOnUiThread(() -> Picasso.get().load(url).into(weatherTypeImg));
    }

    private void callAlgorithmLogic() {
        double percentage = statusCalculator.calculatePercentageAlgorithm();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false));
            statusCalculator.staticAverageLatitude = location.getLatitude();
            statusCalculator.staticAverageLongitude = location.getLongitude();
        }
        int lastPercentage = 0;
        if(NumberHandler.isNumeric(algorithmPercentageText.getText().toString().substring(0, 2))) {
            lastPercentage = Integer.parseInt(algorithmPercentageText.getText().toString().substring(0, 2));
        } else if(NumberHandler.isNumeric(algorithmPercentageText.getText().toString().substring(0, 1))) {
            lastPercentage = Integer.parseInt(algorithmPercentageText.getText().toString().substring(0, 1));
        }

        int counter = (int) Math.floor(percentage);
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(lastPercentage, counter);// here you set the range, from 0 to "count" value
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
//                algorithmPercentageText.setText(((counter) + "%").toString());
                algorithmPercentageText.setText(String.valueOf(animation.getAnimatedValue()) + "%");
            }
        });
        animator.setDuration(500); // here you set the duration of the anim
        animator.start();

        algorithmStatusText.setText(statusCalculator.calculateStatusAlgorithm(percentage));

        ///setting weather type
        Thread weatherThread = new Thread() {
            @Override
            public void run() {
                try {
                    Weather weather = Weather.getCurrentLocationWeather(getApplicationContext());
                    weatherTypeTxt.setText(weather.getWeatherTypePersian());
                    System.out.println(weather.getImageUrl());
                    setWeatherImage(weather.getImageUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        weatherThread.start();
        showAlertBox();

        int backgroundNumber = statusCalculator.calculateBackgroundAlgorithm(percentage);
        if (backgroundNumber == 1) {
            algorithmBackground.setImageResource(R.drawable.circle_gradient_green);
        } else if (backgroundNumber == 2) {
            algorithmBackground.setImageResource(R.drawable.circle_gradient_lightgreen);
        } else if (backgroundNumber == 3) {
            algorithmBackground.setImageResource(R.drawable.circle_gardient_yellow);
        } else if (backgroundNumber == 4) {
            algorithmBackground.setImageResource(R.drawable.circle_gradient_orange);
        } else if (backgroundNumber == 5) {
            algorithmBackground.setImageResource(R.drawable.circle_gradient_darkorange);
        } else if (backgroundNumber == 6) {
            algorithmBackground.setImageResource(R.drawable.circle_gradient_lightred);
        } else if (backgroundNumber == 7) {
            algorithmBackground.setImageResource(R.drawable.circle_gradient_red);
        }

        // pass cycle
        DriveAlertHandler.passCycle();

        // parking
        if(Integer.parseInt(speedText.getText().toString()) >= 20) {
            deleteParkingLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            }
        }
    }

    // Life Cycle
    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
        if (isAccelerometerSensorAvailable) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        //
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainMapActivity.this.finish();
    }

    public void saveParking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false));
        SharedPreferencesManager.writeToSharedPreferences("parkingLocation",  String.valueOf(location.getLatitude())+','+String.valueOf(location.getLongitude()));
//        Toast.makeText(this,"محل پارک خودروی شما به موفقیت ذخیره شد.", Toast.LENGTH_SHORT).show();
        GPSAndInternetChecker.showParkingAlert(this);
        MainMapActivity.showParkingLocation(this, mMap);
    }

    public static Location getParkingLocation(){
        String latLang = SharedPreferencesManager.readFromSharedPreferences("parkingLocation");
        if (latLang==null || latLang.isEmpty()){
            return null;
        }
        String[] tmp = latLang.split(",");
        Location targetLocation = new Location("");
        targetLocation.setLatitude(Float.valueOf(tmp[0]));
        targetLocation.setLongitude(Float.valueOf(tmp[1]));
        return targetLocation;
    }

    public static void showParkingLocation(Context context, GoogleMap mMap) {
        Location location = getParkingLocation();
        if(location != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            // Setting the position for the marker
            markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
            markerOptions.title("آخرین محل پارک خودروی شما");
            Drawable d = context.getResources() .getDrawable(R.drawable.ic_car_parking, context.getTheme());
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapHelper.drawableToBitmap(d)));
            markerOptions.zIndex(10000);
            Marker source_marker = mMap.addMarker(markerOptions);
            source_marker.showInfoWindow();

            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15f).build(); ///15.4f
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public static void deleteParkingLocation() {
        SharedPreferencesManager.deleteFromSharedPreferences("parkingLocation");
    }

    private BoxAnimationState boxAnimationState = BoxAnimationState.OPEN;
    private void percentBoxAnimation() {
        FrameLayout percentBox = findViewById(R.id.percentBox);
        if(boxAnimationState == BoxAnimationState.OPEN) {
            ObjectAnimator animation = ObjectAnimator.ofFloat(percentBox, "translationY", -230);
            animation.setDuration(500);
            animation.start();
            boxAnimationState = BoxAnimationState.MID;
        } else if(boxAnimationState == BoxAnimationState.MID) {
            ObjectAnimator animation = ObjectAnimator.ofFloat(percentBox, "translationY", -650);
            animation.setDuration(500);
            animation.start();
            boxAnimationState = BoxAnimationState.CLOSE;
            moreButton.setBackgroundResource(R.drawable.ic_arrow_down);
        } else if(boxAnimationState == BoxAnimationState.CLOSE) {
            ObjectAnimator animation1 = ObjectAnimator.ofFloat(percentBox, "translationY", 0);
            animation1.setDuration(500);
            animation1.start();
            boxAnimationState = BoxAnimationState.OPEN;
            moreButton.setBackgroundResource(R.drawable.ic_arrow_up);
        }
    }
}