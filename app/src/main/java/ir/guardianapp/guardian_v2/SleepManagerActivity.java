package ir.guardianapp.guardian_v2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bikcrum.circularrangeslider.CircularRangeSlider;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import ir.guardianapp.guardian_v2.SleepSpeedManager.SleepData;
import ir.guardianapp.guardian_v2.SleepSpeedManager.SleepSpeedDetectorService;

public class SleepManagerActivity extends AppCompatActivity {

    TextView sleepTime;
    TextView wakeUpTime;
    TextView totalSleep;
    CircularRangeSlider clock;

    Date sleepTimeDate;
    Date wakeUpTimeDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_manager);

        // MAKE IT FULL SCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.appThemeColor));
        }

        if(!isThereSleepDataFile(this))
            makeSleepDataFile(this);
        initiateViews();
        initiateClock();
        recordAuto(null);
    }

    public String getTotalSleepTime(Date wakeUpDate, Date sleepDate) {
        long diff = wakeUpDate.getTime() - sleepDate.getTime();
        if(diff<0) diff*=-1;
        long diffMinutes = (diff / (60 * 1000))%60;
        long diffHours = diff / (60 * 60 * 1000);

//        int minutes = wakeUpDate.getHours() * 60 + wakeUpDate.getMinutes() - sleepDate.getHours() * 60 - sleepDate.getMinutes();
//        if(minutes<0) minutes=minutes*-1;
        return diffHours + " ساعت و " + diffMinutes + " دقیقه ";
    }

    public ArrayList<Date> generateToDate(int timeStart, int timeEnd) {
        ArrayList<Date> dates = new ArrayList<>();
        if (timeStart < timeEnd) {
            Date date = Calendar.getInstance().getTime();
            int minuteStart = timeStart % 60;
            int hourStart = timeStart / 60;
            int minuteFinish = timeEnd % 60;
            int hourFinish = timeEnd / 60;
            dates.add(new Date(date.getYear(), date.getMonth(), date.getDate(), hourStart, minuteStart, 0));
            dates.add(new Date(date.getYear(), date.getMonth(), date.getDate(), hourFinish, minuteFinish, 0));
        } else {
            Date date = Calendar.getInstance().getTime();
            int minuteStart = timeStart % 60;
            int hourStart = (timeStart / 60) + 12;
            int minuteFinish = timeEnd % 60;
            int hourFinish = timeEnd / 60;
            dates.add(new Date(date.getYear(), date.getMonth(), date.getDate() - 1, hourStart, minuteStart, 0));
            dates.add(new Date(date.getYear(), date.getMonth(), date.getDate(), hourFinish, minuteFinish, 0));
        }
        return dates;
    }

    public void initiateViews() {
        wakeUpTime = findViewById(R.id.wakeUpTime);
        sleepTime = findViewById(R.id.sleepTime);
        totalSleep = findViewById(R.id.totalSleep);
        clock = findViewById(R.id.clock);
    }

    public void initiateClock() {
        clock.setMax(720);
        clock.setProgress(2);
        String[] labels = new String[720];
        Arrays.fill(labels, "");
        labels[0] = "12";
        labels[60] = "1";
        labels[120] = "2";
        labels[180] = "3";
        labels[240] = "4";
        labels[300] = "5";
        labels[360] = "6";
        labels[420] = "7";
        labels[480] = "8";
        labels[540] = "9";
        labels[600] = "10";
        labels[660] = "11";
        clock.setlabels(labels);
        clock.setOnRangeChangeListener(new CircularRangeSlider.OnRangeChangeListener() {
            @Override
            public void onRangePress(int i, int i1) {

            }

            @Override
            public void onRangeChange(int i, int i1) {
                ArrayList<Date> dates = generateToDate(clock.getStartIndex(), clock.getEndIndex());
                wakeUpTimeDate = dates.get(1);
                sleepTimeDate = dates.get(0);
                changeTexts();
            }

            @Override
            public void onRangeRelease(int i, int i1) {

            }
        });
    }


    public void submit(View view) {
        if ((sleepTimeDate == null) || (wakeUpTimeDate == null)) {
            Toast.makeText(this, "لطفا میزان خواب را وارد کنید!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!SleepSpeedDetectorService.isSleepValid(sleepTimeDate, wakeUpTimeDate)) {
            Toast.makeText(this, "این میزان خواب برای شما معتبر نیست!", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_sleep_sureness, null);

        AlertDialog alertDialog = builder.create();
        alertDialog.setView(dialogView, 0, 0, 0, 0);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        lp.width = 700;
//        lp.height = 800;
        lp.x= (int)0;
        lp.y=(int)0;
        alertDialog.getWindow().setAttributes(lp);



        Button yesButton = dialogView.findViewById(R.id.yesButton);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomSleep = null;
                randomWake = null;
                writeInfoToFile(SleepManagerActivity.this,sleepTimeDate,wakeUpTimeDate);
                alertDialog.dismiss();
                Intent intent = new Intent(SleepManagerActivity.this, MainMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button noButton = dialogView.findViewById(R.id.noButton);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
//       builder.setNeutralButton("خروج", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        builder.setView(dialogView);
//        builder.show();
    }

    public void changeTexts() {
        System.out.println(sleepTimeDate.toString());
        DateFormat readFormat = new SimpleDateFormat( "EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        DateFormat writeFormat = new SimpleDateFormat( "HH:mm", Locale.ENGLISH);
        Date date = null;
        try {
            date = readFormat.parse(sleepTimeDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            sleepTime.setText(("ساعت خواب:  " + writeFormat.format(date)));
        }
        //
        try {
            date = readFormat.parse(wakeUpTimeDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            wakeUpTime.setText(("ساعت بیدار شدن:  " + writeFormat.format(date)));
        }
//        sleepTime.setText("ساعت خواب:  " + sleepTimeDate.getHours() + "H:" + sleepTimeDate.getMinutes() + "M");
//        wakeUpTime.setText("ساعت بیدارشدن:  " + wakeUpTimeDate.getHours() + "H:" + wakeUpTimeDate.getMinutes() + "M");
        totalSleep.setText(getTotalSleepTime(sleepTimeDate, wakeUpTimeDate));
    }


    Date randomSleep,randomWake;
    public void recordAuto_random(){
        if(randomWake==null && randomSleep==null) {
            randomWake=new Date();
            randomSleep=new Date();
            Random rand = new Random();
            Date date = Calendar.getInstance().getTime();
            int wakeUpHour = date.getHours();
            int sleepHour = rand.nextInt(4) + 22;
            int bound = date.getHours();
            if(bound>=10)
                bound = 9;
            if (date.getHours() > 6 && date.getHours() < 12)
                wakeUpHour = rand.nextInt(bound - 6) + 6;
            else
                wakeUpHour = rand.nextInt(3) + 6;
            randomWake.setHours(wakeUpHour);
            randomWake.setMinutes(rand.nextInt(date.getMinutes()));
            randomSleep.setHours(sleepHour);
            randomSleep.setMinutes(Math.abs(rand.nextInt(59)));

            Log.d("sleep",randomSleep.toString());
            Log.d("wake",randomWake.toString());
            if(randomWake.getDate()!=randomSleep.getDate()){
                if(randomSleep.getHours()<=randomWake.getHours())
                    randomSleep.setDate(randomWake.getDate());
                else
                    randomSleep.setDate(randomWake.getDate()-1);
            }
        }
        setSleepTimeDate(randomSleep);
        setWakeUpTimeDate(randomWake);
        Log.d("sleep",randomSleep.toString());
        Log.d("wake",randomWake.toString());
        changeTexts();
        // 10 to 1
        // 6 to 8
    }

    public void recordAuto(View view) {
        ArrayList<Date> dates = SleepSpeedDetectorService.getSleepTime();
        if ((dates==null)||(dates.get(0).equals(dates.get(1)))) {
            // Toast.makeText(this, "اطلاعات کافی موجود نیست!", Toast.LENGTH_SHORT).show();
            recordAuto_random();
            return;
        }
        if(dates.get(0).getDay()==dates.get(1).getDay()){
            int minute1= dates.get(0).getHours()*60 + dates.get(0).getMinutes();
            int minute2= dates.get(1).getHours()*60 + dates.get(1).getMinutes();
            if(Math.abs(minute1-minute2)<=15){
                // Toast.makeText(this, "اطلاعات کافی موجود نیست!", Toast.LENGTH_SHORT).show();
                recordAuto_random();
                return;
            }
        }
        setSleepTimeDate(dates.get(0));
        setWakeUpTimeDate(dates.get(1));
        changeTexts();
    }

    public void setSleepTimeDate(Date date) {
        int minutes = 0;
        if (date.getHours() >= 12)
            minutes = (date.getHours() - 12) * 60 + date.getMinutes();
        else
            minutes = date.getHours() * 60 + date.getMinutes();
        clock.setStartIndex(minutes);
        sleepTimeDate = date;
    }

    public void setWakeUpTimeDate(Date date) {
        int minutes = 0;
        if (date.getHours() >= 12)
            minutes = (date.getHours() - 12) * 60 + date.getMinutes();
        else
            minutes = date.getHours() * 60 + date.getMinutes();
        clock.setEndIndex(minutes);
        wakeUpTimeDate = date;
    }

    public static boolean isThereSleepDataFile(Context context){
        for (String s : context.fileList()) {
            if(s.equals("SleepData")) return true;
        }
        return false;
    }

    public static void makeSleepDataFile(Context context){
        File file = new File(context.getFilesDir(), "SleepData");
    }

    public static boolean writeInfoToFile(Context context, Date sleepTimeDate, Date wakeUpTimeDate){
        try {
            makeSleepDataFile(context);
            SleepData sleepData = new SleepData(sleepTimeDate,wakeUpTimeDate,Calendar.getInstance().getTime());
            Gson gson = new Gson();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("SleepData", Context.MODE_PRIVATE));
            outputStreamWriter.write(gson.toJson(sleepData));
            outputStreamWriter.close();
            System.out.println("done");
            isSleepDataRecordedToday(context);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
        return   gson.fromJson(ret ,SleepData.class);
    }

    public static boolean isSleepDataRecordedToday(Context context){
        if(!isThereSleepDataFile(context)) makeSleepDataFile(context);
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
        catch (FileNotFoundException e) {
            System.out.println("login activity File not found: " + e.toString());
            return false;
        } catch (IOException e) {
            System.out. println("login activity Can not read file: " + e.toString());
            return false;
        }
        System.out.println(ret);
        Gson gson = new Gson();
        SleepData sleepData =  gson.fromJson(ret,SleepData.class);
        Date now = Calendar.getInstance().getTime();
        System.out.println(sleepData.getCurrentTime().getHours());
        System.out.println(now.getHours());
        if(sleepData.getCurrentTime().getDate()-now.getDate()>1) return false;
        return !((sleepData.getCurrentTime().getDate()!=now.getDate()) && (now.getHours()>10));
    }

    public static boolean isItTimeToRecord(){
        Date date = Calendar.getInstance().getTime();
        return date.getHours() >= 6 && date.getHours() <= 16;
    }

    public static void deleteSleepData(Context context){
        makeSleepDataFile(context);
    }
}