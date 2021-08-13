package ir.guardianapp.guardian_v2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ir.guardianapp.guardian_v2.DrivingStatus.location.GPSTracker;
import ir.guardianapp.guardian_v2.OSRM.OSRMParser;
import ir.guardianapp.guardian_v2.extras.AnimationHandler;
import ir.guardianapp.guardian_v2.extras.BitmapHelper;
import ir.guardianapp.guardian_v2.extras.Network;
import ir.guardianapp.guardian_v2.extras.TipHandler;
import ir.guardianapp.guardian_v2.map.SourceDest;
import ir.guardianapp.guardian_v2.map.search.SearchPlaces;
import ir.guardianapp.guardian_v2.models.ThisTripData;
import ir.guardianapp.guardian_v2.models.User;
import ir.guardianapp.guardian_v2.network.MapThreadGenerator;
import ir.guardianapp.guardian_v2.network.MessageResult;
import ir.guardianapp.guardian_v2.network.ThreadGenerator;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private static SourceDest sourceDest = SourceDest.SOURCE;
    public static LatLng source_location = null;
    public static Marker source_marker = null;
    public static LatLng dest_location = null;
    public static Marker dest_marker = null;

    private static double cameraLatitude;
    private static double cameraLongitude;

    private EditText searchText;
    private Button locationConfirmButton;
    private TextView locationTag;
    private ImageView customMarker;
    private AlertDialog alertDialog;
    private ImageButton cancelButton;
    private com.addisonelliott.segmentedbutton.SegmentedButtonGroup buttonGroup;
    private int switchPosition = 1;

    public static double searchLatitude = -1;
    public static double searchLongitude = -1;
    public static String searchTitle;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission
                    (getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission
                            (getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 1);
                //return;
            }
        }
        //
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.selectMap);
        //getActivity().getSupportFragmentManager().findFragmentById(R.id.selectMap);
        mapFragment.getMapAsync(this);
        setLocale();

        locationConfirmButton = view.findViewById(R.id.locationConfirmButton);
        locationConfirmButton.setOnClickListener(v -> {
            if(switchPosition==0) {
                SeatBeltActivity.navigationMode = false;
                OSRMParser.clearData();
                startActivity(new Intent(getActivity(), SeatBeltActivity.class));
            } else {
                addLocation(cameraLatitude, cameraLongitude);
            }
        });

        searchText = view.findViewById(R.id.searchText);
        locationTag = view.findViewById(R.id.locationTag);
        customMarker = view.findViewById(R.id.confirm_address_map_custom_marker);
        searchText = view.findViewById(R.id.searchText);
        cancelButton = view.findViewById(R.id.cancelButton);
        buttonGroup = view.findViewById(R.id.buttonGroup);
        buttonGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(final int position) {
                switchPosition  = position;
                if(position==1) { // navigation
                    searchText.clearFocus();
                    searchText.setText("");
                    searchText.setHint("لطفا مبدأ سفر را انتخاب کنید.");
                    locationTag.setText("مبدأ");
                    locationConfirmButton.setText("تایید مبدأ");
                    locationTag.setVisibility(View.VISIBLE);
                    customMarker.setVisibility(View.VISIBLE);
                    customMarker.setBackgroundResource(R.drawable.ic_location_source);
                    locationTag.setBackgroundColor(getResources().getColor(R.color.appTheme5Color));
                    sourceDest = SourceDest.SOURCE;
                    buttonGroup.setVisibility(View.VISIBLE);
                } else { // map
                    if(source_marker != null)
                        source_marker.remove();
                    searchText.clearFocus();
                    searchText.setText("");
                    searchText.setHint("جستجو در نقشه");
                    locationTag.setVisibility(View.INVISIBLE);
                    customMarker.setVisibility(View.INVISIBLE);
                    locationConfirmButton.setText("شروع سفر");
                    sourceDest = SourceDest.SOURCE;
                    buttonGroup.setVisibility(View.VISIBLE);
                }
            }
        });

        cancelButton.setOnClickListener(v -> {
            removeLocation();
        });

//        searchText.clearFocus();

//        if(searchText.requestFocus()) {
//            Intent intent = new Intent(getActivity(), SearchPlacesActivity.class);
//            startActivity(intent);
//        }
        searchText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

            Intent intent = new Intent(getActivity(), SearchPlacesActivity.class);
            startActivity(intent);

                return true; // return is important...
            }
        });
//        searchText.setOnFocusChangeListener((view1, hasFocus) -> {
//            System.out.println("hasfocus" + hasFocus);
//            if (hasFocus) {
//                Intent intent = new Intent(getActivity(), SearchPlacesActivity.class);
//                startActivity(intent);
//            } else {
////                    Toast.makeText(getApplicationContext(), "Lost the focus", Toast.LENGTH_LONG).show();
//            }
//        });

        return view;
    }

    // MAP
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.8f));
            updateCameraBearing(mMap, location);
        }
    };

    private void updateCameraBearing(GoogleMap googleMap, Location location) {
        if (googleMap == null) return;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)             // Sets the center of the map to current location
                .zoom(15.8f)                   // Sets the zoom
                .bearing(location.getBearing()) // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                .build();                   // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onResume() {
        super.onResume();
        voiceSearch();
        if(searchText!=null)
            searchText.setText("");

        try {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(searchLatitude != -1 && searchLongitude != -1) {
//            searchText.clearFocus();
            CameraUpdate point = CameraUpdateFactory.newLatLngZoom(new LatLng(searchLatitude, searchLongitude), 15.5f);
            mMap.moveCamera(point);
            mMap.animateCamera(point);
//            addLocation(searchLatitude, searchLongitude);
        }

        if(mMap != null)
            MainNavigationActivity.showParkingLocation(getContext(), mMap);

    }

    private final int REQ_CODE = 100;

    private void voiceSearch() {
        searchText = getView().findViewById(R.id.searchText);
        ImageView speak = getView().findViewById(R.id.speakButton);
        System.out.println(speak);
        speak.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa");

            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "لطفا صحبت کنید.");
            try {
                startActivityForResult(intent, REQ_CODE);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(getContext(), "دستگاه شما از قابلیت صوتی پشتیبانی نمی کند!", Toast.LENGTH_SHORT).show();
            }
        });
        SearchPlacesActivity.setSearchText(searchText.getText().toString());
                if(!searchText.getText().toString().equalsIgnoreCase("")) {
        Intent intent2 = new Intent(getActivity(), SearchPlacesActivity.class);
        startActivity(intent2);
                }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100: {
                if (resultCode == getActivity().RESULT_OK && null != data) {
                    ArrayList result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchText.setText((String) result.get(0));

                }
                break;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission
                    (getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission
                            (getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 1);
                //return;
            } else if (ActivityCompat.checkSelfPermission
                    (getContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.INTERNET
                }, 1);
            } else {

                googleMap.setMyLocationEnabled(true);
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

                if (location != null) {
                    LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16.5f).build(); ///15.4f
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }


                //
                mMap.setOnCameraIdleListener(() -> {
                    cameraLatitude = mMap.getCameraPosition().target.latitude;
                    cameraLongitude = mMap.getCameraPosition().target.longitude;
                });

                View mapView = getView().findViewById(R.id.selectMap);
                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                rlp.setMargins(0, 0, 30, 140);

//                MainNavigationActivity.showParkingLocation(getContext(), mMap);
            }
        }

        // Setting a click event handler for the map
//        mMap.setOnMapClickListener(latLng -> {
//                if(sourceDest != SourceDest.DONE) {
//                    addLocation(cameraLatitude, cameraLongitude);
//                }});
//        mMap.setOnMapClickListener(latLng -> addMarker(latLng));

        //
//        MainNavigationActivity.showParkingLocation(getContext(), mMap);

//        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
    }

    private void removeLocation() {
        if (sourceDest == SourceDest.DESTINATION) {
            source_marker.remove();
            searchText.clearFocus();
            searchText.setText("");
            searchText.setHint("لطفا مبدأ سفر را انتخاب کنید.");
            locationTag.setText("مبدأ");
            locationConfirmButton.setText("تایید مبدأ");
            customMarker.setBackgroundResource(R.drawable.ic_location_source);
            locationTag.setBackgroundColor(getResources().getColor(R.color.appTheme5Color));
            sourceDest = SourceDest.SOURCE;
            buttonGroup.setVisibility(View.VISIBLE);
        }  else if(sourceDest == SourceDest.DONE) {
            dest_marker.remove();
            source_marker.setTitle("مبدأ");
            source_marker.showInfoWindow();
            searchText.clearFocus();
            searchText.setText("");
            searchText.setHint("لطفا مقصد سفر را انتخاب کنید.");
            locationTag.setText("مقصد");
            locationConfirmButton.setText("تایید مقصد");
            customMarker.setBackgroundResource(R.drawable.ic_location_dest);
            locationTag.setBackgroundColor(getResources().getColor(R.color.appTheme2Color));
            customMarker.setVisibility(View.VISIBLE);
            locationTag.setVisibility(View.VISIBLE);
            FrameLayout routingInformationBox = getView().findViewById(R.id.routingInfoLayout);
            AnimationHandler.slideDown(routingInformationBox);
            MapThreadGenerator.backgroundPolyline.remove();
            MapThreadGenerator.pathPolyline.remove();
            sourceDest = SourceDest.DESTINATION;
            buttonGroup.setVisibility(View.INVISIBLE);
            mMap.setPadding(0, 0, 0, 0);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    private boolean h2 = false;
    private boolean h3 = false;
    private void addLocation(double latitude, double longitude) {
        if (sourceDest == SourceDest.SOURCE) {
            source_location = new LatLng(latitude, longitude);
            searchText.clearFocus();
            searchText.setText("");
            searchText.setHint("لطفا مقصد سفر را انتخاب کنید.");
            locationTag.setText("مقصد");
            customMarker.setBackgroundResource(R.drawable.ic_location_dest);
            locationTag.setBackgroundColor(getResources().getColor(R.color.appTheme2Color));
//            locationTag.setTextColor(getResources().getColor(R.color.white));
            locationConfirmButton.setText("تایید مقصد");
            MarkerOptions markerOptions = new MarkerOptions();
            // Setting the position for the marker
            markerOptions.position(new LatLng(latitude, longitude));
            markerOptions.title("مبدأ");
            Drawable d = getResources().getDrawable(R.drawable.ic_location_source_withoutground, getActivity().getTheme());
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapHelper.drawableToBitmap(d)));
            source_marker = mMap.addMarker(markerOptions);
            source_marker.showInfoWindow();
            sourceDest = SourceDest.DESTINATION;
            buttonGroup.setVisibility(View.INVISIBLE);
        } else if(sourceDest == SourceDest.DESTINATION) {
            dest_location = new LatLng(latitude, longitude);
            searchText.clearFocus();
            searchText.setText("");
            searchText.setHint("لطفا چند لحظه منتظر بمانید!");
            locationConfirmButton.setText("در حال یافتن بهترین مسیر...");
            locationTag.setVisibility(View.INVISIBLE);
            customMarker.setVisibility(View.INVISIBLE);
            MarkerOptions markerOptions = new MarkerOptions();
            // Setting the position for the marker
            markerOptions.position(new LatLng(latitude, longitude));
            markerOptions.title("مقصد");
            Drawable d = getResources().getDrawable(R.drawable.ic_location_dest_withoutground, getActivity().getTheme());
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapHelper.drawableToBitmap(d)));
            dest_marker = mMap.addMarker(markerOptions);
            dest_marker.showInfoWindow();
            sourceDest = SourceDest.DONE;
            buttonGroup.setVisibility(View.INVISIBLE);

            //
            showProgressDialog();

            boolean h2 = false;
            boolean h3 = false;
            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == MessageResult.SUCCESSFUL) {
                        dismissProgressDialog();
                        searchText.setHint("سفر خود را آغاز کنید!");
                        locationConfirmButton.setText("شروع سفر");

                    } else {
                        Toast.makeText(getContext(), "لطفا بعدا تلاش کنید!", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            Handler handler2 = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == MessageResult.SUCCESSFUL) {
                        if(msg.obj != null) {
                            ThisTripData.getInstance().setSourceName(msg.obj.toString());
                            HomeFragment.this.h2 = true;
                            if(HomeFragment.this.h3) {
                                HomeFragment.this.h2 = false;
                                postTrip();
                            }
                        }
                    } else {
                        ThisTripData.getInstance().setSourceName("مبدأ");
                    }
                }
            };
            Handler handler3 = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == MessageResult.SUCCESSFUL) {
                        if(msg.obj != null) {
                            ThisTripData.getInstance().setDestName(msg.obj.toString());
                            HomeFragment.this.h3 = true;
                            if(HomeFragment.this.h2) {
                                HomeFragment.this.h3 = false;
                                postTrip();
                            }
                        }
                    } else {
                        ThisTripData.getInstance().setSourceName("مقصد");
                    }
                }
            };
            if (Network.isNetworkAvailable(getActivity())) {   // connected to internet
                MainActivity.executorService.submit(MapThreadGenerator.getBestRoute(getActivity(), mMap,
                        source_location.latitude, source_location.longitude,
                        dest_location.latitude, dest_location.longitude, handler));
                Handler handler4 = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                       //
                    }
                };

                MainActivity.executorService.submit(MapThreadGenerator.getBestRoute2(
                        source_location.latitude, source_location.longitude,
                        dest_location.latitude, dest_location.longitude, handler4));

                MainActivity.executorService.submit(MapThreadGenerator.getPlaceFromCoordinates(source_location.latitude, source_location.longitude, handler2));

                MainActivity.executorService.submit(MapThreadGenerator.getPlaceFromCoordinates(dest_location.latitude, dest_location.longitude, handler3));
            } else {
                Toast.makeText(getContext(), "اتصال شما به اینترنت برقرار نمی باشد.", Toast.LENGTH_SHORT).show();
            }
        } else if(sourceDest == SourceDest.DONE) {
            SeatBeltActivity.navigationMode = true;
            OSRMParser.clearData();
            startActivity(new Intent(getActivity(), SeatBeltActivity.class));
        }
    }

    private void postTrip() {
        Handler handler2 = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //
            }
        };
        DateFormat readFormat = new SimpleDateFormat( "EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        DateFormat writeFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = null;
        try {
            date = readFormat.parse((new Date()).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String startDate = "";
        String endDate = "";
        if (date != null) {
            startDate = writeFormat.format(date);
        }
        //
        try {
            date = readFormat.parse((new Date()).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            endDate = writeFormat.format(date);
        }
        ThisTripData thisTripData = ThisTripData.getInstance();
        MainActivity.executorService.submit(ThreadGenerator.postATripInformation(User.getInstance().getUsername(), User.getInstance().getToken(),
                thisTripData.getSourceName(), thisTripData.getSourceLongitude(), thisTripData.getSourceLatitude(),
                thisTripData.getDestName(), thisTripData.getDestLongitude(), thisTripData.getDestLatitude(),
                thisTripData.getDuration(), startDate, endDate,
                thisTripData.getAverage(), thisTripData.getDistance(), handler2));
    }

    public void showProgressDialog() {
        ProgressDialog.Builder builder = new ProgressDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_routing_progress, null);

        alertDialog = builder.create();
        alertDialog.setView(view, 0, 0, 0, 0);
        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.x= 0;
        lp.y= 0;
        alertDialog.getWindow().setAttributes(lp);

        TextView tipText = view.findViewById(R.id.tipText);
        TipHandler tipHandler = new TipHandler();
        tipText.setText(tipHandler.getTip());
        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                tipText.setText(tipHandler.getTip());
                ha.postDelayed(this, 10000);
            }
        }, 10000);
    }

    public void dismissProgressDialog() {
        alertDialog.dismiss();
    }


    private void setLocale() {
        // make language persian
        String languageToLoad = "fa_";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getContext().getResources().updateConfiguration(config,
                getContext().getResources().getDisplayMetrics());
    }


    private boolean firstTime = true;
    private boolean showPermissionRequest() {

        if(firstTime) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission
                        (getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        &&
                        ActivityCompat.checkSelfPermission
                                (getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        &&
                        ActivityCompat.checkSelfPermission
                                (getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 1);
                    //return;
                }
            }
            firstTime = false;
            return !(ContextCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED);
        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){


            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_permission, null);

                Button updateBtn = view.findViewById(R.id.permissionButton);
                updateBtn.setOnClickListener(v -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                });

                //        builder.setView(view);
                AlertDialog alertDialog = builder.create();
                alertDialog.setView(view, 0, 0, 0, 0);
                alertDialog.show();
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                lp.copyFrom(alertDialog.getWindow().getAttributes());
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;

                lp.x = 0;
                lp.y = 0;
                alertDialog.getWindow().setAttributes(lp);

                Toast.makeText(getContext(), "گاردین برای یافتن موقعیت شما روی نقشه به موقعیت مکانی شما نیاز دارد. لطفا این دسترسی را به برنامه بدهید!", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }
}