package ir.guardianapp.guardian_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ir.guardianapp.guardian_v2.SleepSpeedManager.SleepData;

public class SleepManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_manager);
    }

    public void submit(View view) {
    }

    public void recordAuto(View view) {
    }

    public static SleepData readDataFromFile(Context context){
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput("SleepData");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (IOException e) {
            return null;
        }
        Gson gson = new Gson();
        return   gson.fromJson(ret,SleepData.class);
    }
}