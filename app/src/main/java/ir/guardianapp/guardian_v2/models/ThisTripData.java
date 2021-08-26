package ir.guardianapp.guardian_v2.models;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ThisTripData {

    private static ThisTripData single_instance = null;

    private ThisTripData () {}

    public static ThisTripData getInstance() {
        if (single_instance == null)  single_instance = new ThisTripData();

        return single_instance;
    }

    private String username;
    private String token;
    private String sourceName;
    private double sourceLongitude;
    private double sourceLatitude;
    private String destName;
    private double destLongitude;
    private double destLatitude;
    private double duration;
    private String startDate;
    private String endDate;
    private double average;
    private int distance;
    private double realDistance = 0;
    private double realLatitude = -1;
    private double realLongitude = -1;
    private String realEndTime;
    private boolean enable = false;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceLongitude(double sourceLongitude) {
        this.sourceLongitude = sourceLongitude;
    }

    public double getSourceLongitude() {
        return sourceLongitude;
    }

    public void setSourceLatitude(double sourceLatitude) {
        this.sourceLatitude = sourceLatitude;
    }

    public double getSourceLatitude() {
        return sourceLatitude;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public String getDestName() {
        return destName;
    }

    public void setDestLongitude(double destLongitude) {
        this.destLongitude = destLongitude;
    }

    public double getDestLongitude() {
        return destLongitude;
    }

    public void setDestLatitude(double destLatitude) {
        this.destLatitude = destLatitude;
    }

    public double getDestLatitude() {
        return destLatitude;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }

    public void setStartDate(Date startDate) {
        String inputPattern = "EEE MMM d HH:mm:ss zzz yyyy";
        String outputPattern = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);
        try {
            this.startDate = outputFormat.format(inputFormat.parse(startDate.toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        String inputPattern = "EEE MMM d HH:mm:ss zzz yyyy";
        String outputPattern = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);
        try {
            this.endDate = outputFormat.format(inputFormat.parse(endDate.toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getEndDate() {
        return endDate;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getAverage() {
        return average;
    }

    public void setDistance(double distance) {
        this.distance = (int)(distance/1000);
    }

    public int getDistance() {
        return distance;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean getEnable() {
        return enable;
    }

    public void setRealDistance(double realDistance) {
        this.realDistance = realDistance;
    }

    public double getRealDistance() {
        return realDistance;
    }

    public void setRealLongitude(double realLongitude) {
        this.realLongitude = realLongitude;
    }

    public double getRealLongitude() {
        return realLongitude;
    }

    public void setRealLatitude(double realLatitude) {
        this.realLatitude = realLatitude;
    }

    public double getRealLatitude() {
        return realLatitude;
    }

    public void addDist(double dist) {
        if(realDistance<0) realDistance = 0;
        this.realDistance += dist;
    }

    public void setRealEndTime(Date realEndTime) {
        String inputPattern = "EEE MMM d HH:mm:ss zzz yyyy";
        String outputPattern = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);
        try {
            this.realEndTime = outputFormat.format(inputFormat.parse(realEndTime.toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getRealEndTime() {
        return realEndTime;
    }

    public static JSONObject createTripJSONObject() throws JSONException, ParseException {
        JSONObject jsonObject = new JSONObject();
        ThisTripData thisTripData = ThisTripData.getInstance();

        jsonObject.put("username", thisTripData.username);
        jsonObject.put("token", thisTripData.token);
        jsonObject.put("sourceName", thisTripData.sourceName);
        jsonObject.put("sourceLongitude", thisTripData.sourceLongitude);
        jsonObject.put("sourceLatitude", thisTripData.sourceLatitude);
        jsonObject.put("destName", thisTripData.destName);
        jsonObject.put("destLongitude", thisTripData.destLongitude);
        jsonObject.put("destLatitude", thisTripData.destLatitude);
        jsonObject.put("duration", thisTripData.duration);
        jsonObject.put("startDate", thisTripData.startDate);
        jsonObject.put("endDate", thisTripData.endDate);
        jsonObject.put("average", thisTripData.average);
        jsonObject.put("distance", thisTripData.distance);
        jsonObject.put("realDistance", thisTripData.realDistance);
        jsonObject.put("realLatitude", thisTripData.realLatitude);
        jsonObject.put("realLongitude", thisTripData.realLongitude);
        jsonObject.put("realEndTime", thisTripData.realEndTime);

        return jsonObject;
    }

    public static void writeJSONArrIntoJSONFile(Context context, JSONObject jsonObject) throws IOException {
        // Convert JsonObject to String Format
        String userString = jsonObject.toString();
        // Define the File Path and its Name
        File file = new File(context.getFilesDir(),"trip.json");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(userString);
        bufferedWriter.close();
        Log.d("wrote in the file!", "done");
    }

    public static JSONObject readJSONArrFromJSONFile(Context context) throws IOException, JSONException {
        File file = new File(context.getFilesDir(), "trip.json");
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null){
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        // This response will have Json Format String
        String response = stringBuilder.toString();
        return new JSONObject(response);
    }

    public static boolean fileDoesExist(Context context) {
        File f = new File(context.getFilesDir(), "trip.json");
        if(f.exists() && !f.isDirectory()) {
            return true;
        }
        return false;
    }

    public static boolean deleteFile(Context context) {
        File f = new File(context.getFilesDir(), "trip.json");
        return f.delete();
    }
}
