package ir.guardianapp.guardian_v2.models;

public class Driving {

    // Singleton
    private static Driving single_instance = null;

    // private constructor restricted to this class itself
    private Driving () {}

    // static method to create instance of Singleton class
    public static Driving getInstance() {
        if (single_instance == null)
            single_instance = new Driving();

        return single_instance;
    }

    private double sleep;
    private double time;
    private double speed;
    private double tripDuration;
    private double roadType;
    private double traffic;
    private double weather;
    private double radius30KM;
    private double vibration;
    private double acceleration;
    private double month;
    private double average;

    public void setSleep(double sleep) {
        this.sleep = sleep;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setTripDuration(double tripDuration) {
        this.tripDuration = tripDuration;
    }

    public void setRoadType(double roadType) {
        this.roadType = roadType;
    }

    public void setTraffic(double traffic) {
        this.traffic = traffic;
    }

    public void setWeather(double weather) {
        this.weather = weather;
    }

    public void setRadius30KM(double radius30KM) {
        this.radius30KM = radius30KM;
    }

    public void setVibration(double vibration) {
        this.vibration = vibration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public void setMonth(double month) {
        this.month = month;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getSleep() {
        return sleep;
    }

    public double getTime() {
        return time;
    }

    public double getSpeed() {
        return speed;
    }

    public double getTripDuration() {
        return tripDuration;
    }

    public double getRoadType() {
        return roadType;
    }

    public double getTraffic() {
        return traffic;
    }

    public double getWeather() {
        return weather;
    }

    public double getRadius30KM() {
        return radius30KM;
    }

    public double getVibration() {
        return vibration;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getMonth() {
        return month;
    }

    public double getAverage() {
        return average;
    }
}
