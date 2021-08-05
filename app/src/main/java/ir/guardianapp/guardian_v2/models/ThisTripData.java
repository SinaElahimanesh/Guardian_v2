package ir.guardianapp.guardian_v2.models;

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
    private double distance;
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
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean getEnable() {
        return enable;
    }
}
