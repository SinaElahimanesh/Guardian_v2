package ir.guardianapp.guardian_v2.SleepSpeedManager;

import android.app.AppComponentFactory;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.Provider;
import java.util.Calendar;
import java.util.Date;

public class UseMeNotification extends Service{

    @Override
    public void onCreate() {
        super.onCreate();
        Date date=readDataFromFile(this);
        Date dateNow = Calendar.getInstance().getTime();
        if(dateNow.getMonth()!=date.getMonth() || dateNow.getDay()-date.getDay()>15)
            makeNotification();
    }

    public static void makeUsageDataFile(Context context){
        File file = new File(context.getFilesDir(), "UsageData");
    }

    public static boolean writeInfoToFile(Context context, Date time){
        try {
            makeUsageDataFile(context);
            Gson gson = new Gson();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("UsageData", Context.MODE_PRIVATE));
            outputStreamWriter.write(gson.toJson(time));
            outputStreamWriter.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static final int NOTIFICATION_ID = 1231234;
    private final String CHANNEL_ID = "guardian";
    public void makeNotification(){
        createNotificationChannel();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("مدتی است از گاردین استفاده نکرده اید.")
                .setContentText("گاردین می تواند به شما برای داشتن سفری امن کمک کند!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notification_white)
                .setColor(Color.parseColor("#00ff00"))
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(70, builder.build());
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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Date readDataFromFile(Context context){
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput("UsageData");

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
        return   gson.fromJson(ret,Date.class);
    }

}
