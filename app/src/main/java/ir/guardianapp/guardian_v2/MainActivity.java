package ir.guardianapp.guardian_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.onesignal.OneSignal;

import org.json.JSONException;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ir.guardianapp.guardian_v2.database.JSONManager;
import ir.guardianapp.guardian_v2.database.SharedPreferencesManager;
import ir.guardianapp.guardian_v2.extras.GPSAndInternetChecker;
import ir.guardianapp.guardian_v2.extras.Network;
import ir.guardianapp.guardian_v2.models.User;
import ir.guardianapp.guardian_v2.network.MessageResult;
import ir.guardianapp.guardian_v2.network.ThreadGenerator;


public class MainActivity extends AppCompatActivity {

    private static final int TIME_OUT = 3000;
    private static final String ONESIGNAL_APP_ID = "52708f3f-d26f-4739-9b0e-97093714a222";

    private String version = "1.0.0";
    private boolean isLoggedIn = false;

    public static final int DEFAULT_THREAD_POOL_SIZE = 5;
    public static ExecutorService executorService;
    private Handler handler;
    public static String updateLink;
    private boolean intentEnable = false;
    private boolean checkDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // MAKE IT FULL SCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.appThemeColor));
        }

        // height / width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        //
        if(checkIfVPNConnected()) {
            Toast.makeText(this,"روشن بودن VPN سرعت برنامه را کند می کند. لطفا آن را خاموش کنید!", Toast.LENGTH_LONG).show();
        }

        // sharedPreferences
        SharedPreferencesManager.initializeSharedPreferences(this);
        String token  = "";
        if(SharedPreferencesManager.hasToken(this)) {
            token = SharedPreferencesManager.getToken(this);
        }

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
        if(token.length() > 0) {
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    checkDone = true;
                    if (msg.what == MessageResult.VERSION_IS_LESS_THAN_MINIMUM) {
                        GPSAndInternetChecker.showUpdateAlert(MainActivity.this, updateLink, height, width);
                    } else if (msg.what == MessageResult.LOGGED_OUT) {
                        //
                    } else if (msg.what == MessageResult.LOGGED_IN) {
                        isLoggedIn = true;
                        MainActivity.setShowGuide(false);
                        User.getInstance().setToken(SharedPreferencesManager.getToken(MainActivity.this));
                        //
                        Handler handler2 = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                //
                            }
                        };
                        MainActivity.executorService.submit(ThreadGenerator.getDrivingDetail(User.getInstance().getUsername(), User.getInstance().getToken(), handler2));
                    } else if (msg.what == MessageResult.FAILED) {
                        //
                    }
                    if(intentEnable) {
                        startNextActivity();
                    }
                }
            };
            executorService.submit(ThreadGenerator.userCredentials(token, version, handler));
        } else {
            isLoggedIn = false;
            checkDone = true;
        }

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        // RETRY BUTTON
        Button retryButton = findViewById(R.id.retryButton);
        retryButton.setOnClickListener(v -> {
            if(!GPSAndInternetChecker.check(MainActivity.this, height, width)) {
                retryButton.setVisibility(View.VISIBLE);
            } else {
                retryButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void startApp() {
        new CountDownTimer(TIME_OUT, TIME_OUT) {

            @Override
            public void onTick(long millisUntilFinished) {
                ImageView animateTextView = findViewById(R.id.Logo);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;

                ObjectAnimator textViewAnimator = ObjectAnimator.ofFloat(animateTextView, "translationY",0f,-(height/3.72f));
                textViewAnimator.setDuration(3000);
                textViewAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                textViewAnimator.start();
            }

            public void onFinish() {
                intentEnable = true;
                if(checkDone) {
                    startNextActivity();
                }
            }

        }.start();
    }

    private void startNextActivity() {
        if (isLoggedIn) {
            if(!SleepManagerActivity.isSleepDataRecordedToday(MainActivity.this) && SleepManagerActivity.isItTimeToRecord()){
                startActivity(new Intent(MainActivity.this, SleepManagerActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
            }
        } else {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Button retryButton = findViewById(R.id.retryButton);
        retryButton.setOnClickListener(v -> {
            if(!GPSAndInternetChecker.check(MainActivity.this, height, width)) {
                retryButton.setVisibility(View.VISIBLE);
            } else {

                retryButton.setVisibility(View.INVISIBLE);
                startApp();
            }
        });

        if(!GPSAndInternetChecker.check(MainActivity.this, height, width)) {
            retryButton.setVisibility(View.VISIBLE);
        }
        if(retryButton.getVisibility()==View.INVISIBLE) {
            startApp();
        }
    }

    private static boolean showGuide = true;
    public static void setShowGuide(boolean sg) {
        showGuide = sg;
    }
    public static boolean getShowGuide() {
        return showGuide;
    }

    public boolean checkIfVPNConnected() {
        List<String> networkList = new ArrayList<>();
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    networkList.add(networkInterface.getName());
            }
        } catch (Exception ex) {
            Log.d("VPN", "isVpnUsing Network List didn't received");
        }

        return networkList.contains("tun0");
    }
}