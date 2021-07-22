package ir.guardianapp.guardian_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import com.onesignal.OneSignal;

import ir.guardianapp.guardian_v2.extras.GPSAndInternetChecker;


public class MainActivity extends AppCompatActivity {

    private static final int TIME_OUT = 3000;
    private static final String ONESIGNAL_APP_ID = "52708f3f-d26f-4739-9b0e-97093714a222";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // MAKE IT FULL SCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.appThemeColor));
        }

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        // RETRY BUTTON
        Button retryButton = findViewById(R.id.retryButton);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
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
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }

        }.start();
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
}