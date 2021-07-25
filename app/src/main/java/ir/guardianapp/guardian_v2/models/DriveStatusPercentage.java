package ir.guardianapp.guardian_v2.models;

public class DriveStatusPercentage {
    // Singleton
    private static DriveStatusPercentage single_instance = null;

    // private constructor restricted to this class itself
    private DriveStatusPercentage () {}

    // static method to create instance of Singleton class
    public static DriveStatusPercentage getInstance() {
        if (single_instance == null)
            single_instance = new DriveStatusPercentage();

        return single_instance;
    }

    private double DRIVING_AVERAGE = 0;
    private double REGISTER_DATE = 0;
    private double LATITUDE = 0;
    private double LONGITUDE = 0;

    private double SLEEP = 0;
    private double TIME = 0;
    private double SPEED = 0;
    private double WITHOUT_STOP = 0;
    private double ROAD_TYPE = 0;
    private double TRAFFIC = 0;
    private double WEATHER = 0;
    private double NEAR_CITIES = 0;
    private double VIBRATION = 0;
    private double ACCELERATION = 0;
    private double MONTH = 0;

    public void setDRIVING_AVERAGE(double DRIVING_AVERAGE) {
        this.DRIVING_AVERAGE = DRIVING_AVERAGE;
    }

    public void setREGISTER_DATE(double REGISTER_DATE) {
        this.REGISTER_DATE = REGISTER_DATE;
    }

    public void setLATITUDE(double LATITUDE) {
        this.LATITUDE = LATITUDE;
    }

    public void setLONGITUDE(double LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }

    public void setSLEEP(double SLEEP) {
        this.SLEEP = SLEEP;
    }

    public void setTIME(double TIME) {
        this.TIME = TIME;
    }

    public void setSPEED(double SPEED) {
        this.SPEED = SPEED;
    }

    public void setWITHOUT_STOP(double WITHOUT_STOP) {
        this.WITHOUT_STOP = WITHOUT_STOP;
    }

    public void setROAD_TYPE(double ROAD_TYPE) {
        this.ROAD_TYPE = ROAD_TYPE;
    }

    public void setTRAFFIC(double TRAFFIC) {
        this.TRAFFIC = TRAFFIC;
    }

    public void setWEATHER(double WEATHER) {
        this.WEATHER = WEATHER;
    }

    public void setNEAR_CITIES(double NEAR_CITIES) {
        this.NEAR_CITIES = NEAR_CITIES;
    }

    public void setVIBRATION(double VIBRATION) {
        this.VIBRATION = VIBRATION;
    }

    public void setACCELERATION(double ACCELERATION) {
        this.ACCELERATION = ACCELERATION;
    }

    public void setMONTH(double MONTH) {
        this.MONTH = MONTH;
    }

    public double getDRIVING_AVERAGE() {
        return DRIVING_AVERAGE;
    }

    public double getREGISTER_DATE() {
        return REGISTER_DATE;
    }

    public double getLATITUDE() {
        return LATITUDE;
    }

    public double getLONGITUDE() {
        return LONGITUDE;
    }

    public double getSLEEP() {
        return SLEEP;
    }

    public double getTIME() {
        return TIME;
    }

    public double getSPEED() {
        return SPEED;
    }

    public double getWITHOUT_STOP() {
        return WITHOUT_STOP;
    }

    public double getROAD_TYPE() {
        return ROAD_TYPE;
    }

    public double getTRAFFIC() {
        return TRAFFIC;
    }

    public double getWEATHER() {
        return WEATHER;
    }

    public double getNEAR_CITIES() {
        return NEAR_CITIES;
    }

    public double getVIBRATION() {
        return VIBRATION;
    }

    public double getACCELERATION() {
        return ACCELERATION;
    }

    public double getMONTH() {
        return MONTH;
    }
}
