package ir.guardianapp.guardian_v2;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.MapStyleOptions;
import com.suke.widget.SwitchButton;

import ir.guardianapp.guardian_v2.database.SharedPreferencesManager;


public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static boolean defaultColor = true;
    public static boolean dayNight = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        setupPreferences();

        com.suke.widget.SwitchButton defaultColorSwitch = view.findViewById(R.id.defaultColorSwitch);
        defaultColorSwitch.setChecked(defaultColor);
        com.suke.widget.SwitchButton dayNightSwitch = view.findViewById(R.id.dayNightSwitch);
        dayNightSwitch.setChecked(dayNight);
        defaultColorSwitch.setOnCheckedChangeListener((view1, isChecked) -> {
            if(isChecked) {
                defaultColor = true;
                dayNightSwitch.setEnabled(false);
            } else {
                defaultColor = false;
                dayNightSwitch.setEnabled(true);
            }
            SharedPreferencesManager.writeToSharedPreferences("defaultColor", String.valueOf(defaultColor));
        });

        dayNightSwitch.setOnCheckedChangeListener((view1, isChecked) -> {
            if(isChecked) {
                dayNight = true;
            } else {
                dayNight = false;
            }
            SharedPreferencesManager.writeToSharedPreferences("dayNight", String.valueOf(dayNight));
        });

        if(defaultColor) {
            dayNightSwitch.setEnabled(false);
        }

        Button doneButton = view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> Toast.makeText(getContext(), "تغییرات برای صفحه مسیریابی و نقشه با موفقیت ذخیره شد.", Toast.LENGTH_SHORT).show());

        return view;
    }

    private static void setupPreferences() {
        if(!SharedPreferencesManager.readFromSharedPreferences("defaultColor").equalsIgnoreCase("")) {
            defaultColor = Boolean.parseBoolean(SharedPreferencesManager.readFromSharedPreferences("defaultColor"));
        }
        else {
            defaultColor = true;
        }
        if(!SharedPreferencesManager.readFromSharedPreferences("dayNight").equalsIgnoreCase(""))
            dayNight = Boolean.parseBoolean(SharedPreferencesManager.readFromSharedPreferences("dayNight"));
        else
            dayNight = true;
    }

    public static MapStyleOptions getMapStyle(Context context) {
        setupPreferences();
        if(defaultColor) {
            return null;
        } else {
            if(dayNight) {
                return MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle_light);
            } else {
                return MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle_night);
            }
        }
    }
}