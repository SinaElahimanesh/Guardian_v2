package ir.guardianapp.guardian_v2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ir.guardianapp.guardian_v2.DrivingPercentage.EncodeDecode;
import ir.guardianapp.guardian_v2.database.ImageSavingManager;
import ir.guardianapp.guardian_v2.extras.Network;
import ir.guardianapp.guardian_v2.models.Driving;
import ir.guardianapp.guardian_v2.models.User;
import ir.guardianapp.guardian_v2.network.MessageResult;
import ir.guardianapp.guardian_v2.network.ThreadGenerator;

public class DrivingFragment extends Fragment {

    private Handler handler;
    private static boolean canUpdate = true;
    private static int requestLimit = 10;
    private static boolean isFirstTime = true;

    private ProgressBar progressBar;
    private TextView withoutStopDriving;
    private TextView speed;
    private TextView sleep;
    private TextView vibration;
    private TextView time;
    private TextView acceleration;
    private TextView weather;
    private TextView dangerZone;
    private TextView month;
    private TextView roadType;
    private  TextView average;
    private TextView username;
    private TextView phoneNum;
    private TextView averageDescription;

    public DrivingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setDriving();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driving, container, false);
        withoutStopDriving = view.findViewById(R.id.withoutStopDriving);
        speed = view.findViewById(R.id.speed);
        sleep = view.findViewById(R.id.sleep);
        vibration = view.findViewById(R.id.vibration);
        time = view.findViewById(R.id.time);
        acceleration = view.findViewById(R.id.acceleration);
        weather = view.findViewById(R.id.weather);
        dangerZone = view.findViewById(R.id.danger_zone);
        month = view.findViewById(R.id.month);
        roadType = view.findViewById(R.id.road_type);

        average = view.findViewById(R.id.average);
        username = view.findViewById(R.id.usernameTextView);
        phoneNum = view.findViewById(R.id.phoneNumberTextView);
        averageDescription = view.findViewById(R.id.safetyStatus);
        progressBar = view.findViewById(R.id.progressBar);

        if(isFirstTime) {
            requestUpdateData();
            isFirstTime = false;
        }

        Button updateDataButton = view.findViewById(R.id.updateDataButton);
        updateDataButton.setOnClickListener(v -> {
            requestUpdateData();
        });

        ImageSavingManager.loadImageFromStorage(view.findViewById(R.id.imageView), getContext());
        return view;
    }

    private void requestUpdateData() {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MessageResult.SUCCESSFUL) {
                    progressBar.setVisibility(View.INVISIBLE);
                    canUpdate = true;
                    progressBar.setProgress(progressBar.getProgress());
                    setDriving();

                } else if (msg.what == MessageResult.FAILED) {
                    progressBar.setVisibility(View.INVISIBLE);
                    canUpdate = true;
                    Toast.makeText(getContext(), "لطفا دوباره تلاش کنید.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    canUpdate = true;
                    Toast.makeText(getContext(), "لطفا دوباره تلاش کنید.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        if (Network.isNetworkAvailable(getActivity())) {   // connected to internet
            if(canUpdate && requestLimit!=0) {
                canUpdate = false;
                requestLimit--;
                progressBar.setVisibility(View.VISIBLE);
                MainActivity.executorService.submit(ThreadGenerator.getDrivingDetail(User.getInstance().getUsername(), User.getInstance().getToken(), handler));
            } else if(requestLimit == 0) {
                Toast.makeText(getContext(), "لطفا بعدا تلاش کنید!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "اتصال شما به اینترنت برقرار نمی باشد.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setDriving() {
        Driving driving = Driving.getInstance();
        if(driving.getTripDuration() != -1) {
            withoutStopDriving.setText(EncodeDecode.withoutStopDecode(driving.getTripDuration()));
        } else {
            withoutStopDriving.setText("--");
        }

        if(driving.getSpeed() != -1) {
            speed.setText(EncodeDecode.speedDecode(driving.getSpeed()));
        } else {
            speed.setText("--");
        }

        if(driving.getSleep() != -1) {
            sleep.setText(EncodeDecode.sleepDecode(driving.getSleep()));
        } else {
            sleep.setText("--");
        }

        if(driving.getVibration() != -1) {
            vibration.setText(EncodeDecode.vibrationDecode(driving.getVibration()));
        } else {
            vibration.setText("--");
        }

        if(driving.getTime() != -1) {
            time.setText(EncodeDecode.timeDecode(driving.getTime()));
        } else {
            time.setText("--");
        }

        if(driving.getAcceleration() != -1) {
            acceleration.setText(EncodeDecode.accelerationDecode(driving.getAcceleration()));
        } else {
            acceleration.setText("--");
        }

        if(driving.getWeather() != -1) {
            weather.setText(EncodeDecode.weatherDecode(driving.getWeather()));
        } else {
            weather.setText("--");
        }

        if(driving.getRadius30KM() != -1) {
            dangerZone.setText(EncodeDecode.nearCitiesDecode(driving.getRadius30KM()));
        } else {
            dangerZone.setText("--");
        }

        if(driving.getMonth() != -1) {
            month.setText(EncodeDecode.monthDecode(driving.getMonth()));
        } else {
            month.setText("--");
        }

        if(driving.getRoadType() != -1) {
            roadType.setText(EncodeDecode.roadTypeDecode(driving.getRoadType()));
        } else {
            roadType.setText("--");
        }

        if(driving.getAverage() != -1) {
            average.setText(((Math.round(driving.getAverage() * 100.0) / 100.0) + "%").toString());
            averageDescription.setText(EncodeDecode.calculateStatusAlgorithm(driving.getAverage()));
        } else {
            average.setText("100");
            averageDescription.setText("اطلاعات ناموجود");
        }

        User user = User.getInstance();
        username.setText(user.getUsername());
        phoneNum.setText(user.getPhoneNum());
    }
}