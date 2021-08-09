package ir.guardianapp.guardian_v2.OSRM;

import java.util.ArrayList;

public class Step {
    String drivingSide = "";
    ArrayList<Line> lines = new ArrayList<>();
    String name = "";
    double distance = 0;
    double duration = 0;
    int bearing = 0;
    String type;
    int exit = 1;
    String rotary_name = "";

    public ArrayList<Line> getLines() {
        return lines;
    }

    public String getDrivingSide() {
        return drivingSide;
    }

    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }

    public double getDuration() {
        return duration;
    }

    public int getBearing() {
        return bearing;
    }

    public String getType() {
        return type;
    }

    public int getExit() {
        return exit;
    }

    public String getRotary_name() {
        return rotary_name;
    }
}