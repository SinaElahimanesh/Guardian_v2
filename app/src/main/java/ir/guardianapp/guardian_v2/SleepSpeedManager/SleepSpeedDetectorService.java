package ir.guardianapp.guardian_v2.SleepSpeedManager;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ir.guardianapp.guardian_v2.MainActivity;
import ir.guardianapp.guardian_v2.R;

public class SleepSpeedDetectorService extends Service {

    private static final int NOTIFICATION_ID = 1231234;
    private final String CHANNEL_ID = "guardian";
    private Intent intent;
    private CountDownTimer countDownTimer;
    private ActivityRecognitionClient mActivityRecognitionClient;
    private boolean started = false;
    private Date lastNotification;
    private static HashMap<Date,DetectedActivity> sleepData= new HashMap<>();
    private  static ArrayList<Date> allDates = new ArrayList<>();
    private static DetectedActivity lastActivity;


    @Nullable @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println(serviceIsRunningInForeground(this));
        Intent notificationIntent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setPriority(Notification.PRIORITY_MIN)
                .setContentTitle("Guardian")
                .setContentText("گاردین به عنوان دستیاری صوتی تا پایان سفر همراه شما خواهد بود.")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notification_blue)
                .setColor(Color.parseColor("#00ff00"))
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        builder.setOngoing(true);
        if(countDownTimer==null)
            startForeground(NOTIFICATION_ID, builder.build());
        this.intent=intent;
        countDownTimer = new CountDownTimer(999999999, 1000 * 60 * 5) {
            @Override
            public void onTick(long l) {
                recognizeActivity();
                System.out.println("tick");
            }

            @Override
            public void onFinish() {
                countDownTimer.start();
            }
        }.start();
        return START_REDELIVER_INTENT;
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "guardian";
            String description = "alerting user";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void recognizeActivity() {
        if((mActivityRecognitionClient==null)&&(!started)) {
            mActivityRecognitionClient = new ActivityRecognitionClient(this);
            mActivityRecognitionClient.requestActivityUpdates(0, PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_ONE_SHOT));
            started = true;
        }

        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
            detectedActivitiesToJson(detectedActivities);
        }
    }

    private static void deleteOldData(){
        if(allDates.isEmpty()) return;
        while((allDates.get(0).getDay()!=allDates.get(allDates.size()-1).getDay()) && (allDates.get(allDates.size()-1).getHours()>allDates.get(0).getHours())){
            sleepData.remove(allDates.get(0));
            allDates.remove(0);
        }
    }

    String detectedActivitiesToJson(ArrayList<DetectedActivity> detectedActivitiesList) {
        Type type = new TypeToken<ArrayList<DetectedActivity>>() {}.getType();
        System.out.println(detectedActivitiesList.toString()+Calendar.getInstance().getTime()+" sleep"+Calendar.getInstance().getTime().getDay());
        if(detectedActivitiesList!=null){  ///sleep
            lastActivity=detectedActivitiesList.get(0);
            deleteOldData();
            Date date = getDate(Calendar.getInstance().getTime());
            sleepData.put(date,detectedActivitiesList.get(0));
            allDates.add(date);
            System.out.println(sleepData+" sleep");
        }
        if ((detectedActivitiesList.size()>=1)&&(detectedActivitiesList.get(0).getType() == DetectedActivity.IN_VEHICLE) && (detectedActivitiesList.get(0).getConfidence()) >= 60){ //speed
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

            List<ActivityManager.RunningTaskInfo> allTasks = am.getRunningTasks(1);
            for (ActivityManager.RunningTaskInfo aTask : allTasks) {
                if(aTask.baseActivity.getClassName().contains("guardian"))
                    return null;
            }
            if(lastNotification!=null){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(lastNotification);
                calendar.add(Calendar.MINUTE,20);
                Date newDate = calendar.getTime();
                calendar.clear();
                if(newDate.before(lastNotification) == false)
                    return null;
            }
            makeNotification();
            lastNotification = Calendar.getInstance().getTime();
        }
        return new Gson().toJson(detectedActivitiesList, type);
    }

    public void makeNotification(){
        createNotificationChannel();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("رانندگی شناسایی شد")
                .setContentText("ایا مایل به باز کردن گاردین هستید؟")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notification_white)
                .setColor(Color.parseColor("#00ff00"))
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(70, builder.build());
    }

    static void startService(Context context, String message) {
        Intent startIntent = new Intent(context, SleepSpeedDetectorService.class);
        startIntent.putExtra("inputExtra", message);
        ContextCompat.startForegroundService(context, startIntent);
    }

    static void stopService(Context context) {
        Intent stopIntent = new Intent(context, SleepSpeedDetectorService.class);
        context.stopService(stopIntent);
    }


    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())){
                if (service.foreground){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("Service", "onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }


    static void clear(){
        sleepData.clear();
        allDates.clear();
    }



    public static boolean isSleepValid(Date sleepTime, Date awakeTime){
        Date date = getDate(sleepTime);
        int error = 0;
        while(date.after(awakeTime)  == false){
            if((sleepData.containsKey(date))&&(sleepData.get(date).getType()!=DetectedActivity.STILL)&&(sleepData.get(date).getConfidence()==100)){
                error++;
                if(date.getHours()>7)
                    error++;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE,5);
            date = calendar.getTime();
            calendar.clear();
        }
        return error <= 3;
    }



    private static Date getDate(Date date){
        return new Date(date.getYear(),date.getMonth(),date.getDate(),date.getHours(),(date.getMinutes()/5)*5,0);
    }

}