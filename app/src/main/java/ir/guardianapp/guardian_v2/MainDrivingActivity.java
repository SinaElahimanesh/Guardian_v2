package ir.guardianapp.guardian_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ir.guardianapp.guardian_v2.DrivingPercentage.StatusCalculator;
import ir.guardianapp.guardian_v2.DrivingStatus.location.LocationService;

public class MainDrivingActivity extends AppCompatActivity {

    LocationService myService;
    static boolean status;
    LocationManager locationManager;
    public static long startTime, endTime;
    public static ProgressDialog locate;
    public static int p = 0;

    private static boolean firstTime = true;
    private static boolean firstSound = true;
    private static int volume_media = 10000;
    private static boolean secondSound = true;
    private static int soundRepetition = 0;
    AudioManager audio;
    private static int SOUND_REPETITION = 0;
    private static int dangerFlag = 1;

    private final int REQ_CODE = 100;
    TextView textView;
    public static boolean dangerModeOn = true;
    private static boolean firstDanger = true;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_driving);
    }
}