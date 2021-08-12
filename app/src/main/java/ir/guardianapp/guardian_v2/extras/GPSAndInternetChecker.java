package ir.guardianapp.guardian_v2.extras;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import ir.guardianapp.guardian_v2.MainNavigationActivity;
import ir.guardianapp.guardian_v2.R;
import ir.guardianapp.guardian_v2.database.SharedPreferencesManager;


public class GPSAndInternetChecker {

    public static boolean check(Context context, double height, double width){
        if (! isGPSOn(context)){
            showGPSAlert(context, height, width);
            return false;
        }
        if(!isInternetConnected(context)) {
            showInternetAlert(context, height, width);
            return false;
        }
        return true;
    }

    public static void showParkingAlert(final Context context, GoogleMap mMap, Location location){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_parking, null);

        AlertDialog alertDialog = builder.create();
        alertDialog.setView(view, 0, 0, 0, 0);
        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.x= 0;
        lp.y= 0;
        alertDialog.getWindow().setAttributes(lp);

        Button lastParkingBtn = view.findViewById(R.id.lastParkingButton);
        lastParkingBtn.setOnClickListener(v -> {
            MainNavigationActivity.showParkingLocation(context, mMap);
            alertDialog.dismiss();
        });

        Button newParkingBtn = view.findViewById(R.id.newParkingButton);
        newParkingBtn.setOnClickListener(v -> {
            SharedPreferencesManager.writeToSharedPreferences("parkingLocation",  String.valueOf(location.getLatitude())+','+String.valueOf(location.getLongitude()));
            MainNavigationActivity.showParkingLocation(context, mMap);
            alertDialog.dismiss();
        });

    }

    public static void showUpdateAlert(final Context context, String updateLink, double height, double width){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_update, null);

        Button updateBtn = view.findViewById(R.id.updateButton);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(updateLink);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setView(view, 0, 0, 0, 0);
        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.x= 0;
        lp.y= 0;
        alertDialog.getWindow().setAttributes(lp);

    }

    public static void showInternetAlert(final Context context, Double height, Double width){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_internet, null);

        Button wifiBtn = view.findViewById(R.id.wifiButton);
        wifiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        Button internetBtn = view.findViewById(R.id.internetButton);
        internetBtn.setOnClickListener(v -> context.startActivity(new Intent(Settings.ACTION_DATA_USAGE_SETTINGS)));

        AlertDialog alertDialog = builder.create();
        alertDialog.setView(view, 0, 0, 0, 0);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.x= 0;
        lp.y= 0;
        alertDialog.getWindow().setAttributes(lp);
    }

    public static void showGPSAlert(final Context context, Double height, Double width){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_gps, null);

        Button gpsBtn = view.findViewById(R.id.gpsButton);
        gpsBtn.setOnClickListener(v -> context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)));

        AlertDialog alertDialog = builder.create();
        alertDialog.setView(view, 0, 0, 0, 0);
        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.x= 0;
        lp.y= 0;
        alertDialog.getWindow().setAttributes(lp);
    }

    public static StringBuilder readFile(String fileName, Context context) {
        StringBuilder stringBuffer = new StringBuilder("");
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            stringBuffer = new StringBuilder();

            String lines;
            while ((lines = bufferedReader.readLine()) != null) {
                stringBuffer.append(lines);
            }
        } catch (IOException exp) {
            exp.printStackTrace();
        }
        return stringBuffer;
    }

    public static boolean isGPSOn(Context context){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ignored) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ignored) {}
        return !(!gps_enabled && !network_enabled) ;
    }

    public static boolean isInternetConnected(Context context){

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        }
        return connected ;
    }
}
