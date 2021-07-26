package ir.guardianapp.guardian_v2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ir.guardianapp.guardian_v2.extras.TipHandler;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button button = view.findViewById(R.id.startButton);
        button.setOnClickListener(v -> {
            if(showPermissionRequest()) {
                Intent i = new Intent(getActivity(), SeatBeltActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });


        TextView tipText = view.findViewById(R.id.tipText);
        TipHandler tipHandler = new TipHandler();
        tipText.setText(tipHandler.getTip());
        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                tipText.setText(tipHandler.getTip());
                ha.postDelayed(this, 13000);
            }
        }, 13000);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission
                    (getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission
                            (getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission
                            (getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 1);
                //return;
            }
        }
        return view;
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