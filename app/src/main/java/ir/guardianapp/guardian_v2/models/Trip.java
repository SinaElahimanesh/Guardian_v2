package ir.guardianapp.guardian_v2.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trip {

    private static ArrayList<Trip> trips = new ArrayList<>();
    private String sourceName;
    private String destinationName;
    private Date startDate;
    private Date endDate;
    private double distanceInKM;

    public Trip(String sourceName, String destinationName, Date startDate, Date endDate, double distanceInKM) {
        this.sourceName = sourceName;
        this.destinationName = destinationName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.distanceInKM = distanceInKM;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public double getDistanceInKM() {
        return distanceInKM;
    }

    public static void deleteTrips(){
        trips.clear();
    }

    public static void addTrip(Trip trip){
        trips.add(trip);
    }

    public static void addAllTrips(List<Trip> tripsList){
        trips.clear();
        trips.addAll(tripsList);
    }

    public static ArrayList<Trip> getTrips() {
        return trips;
    }

    public static void deleteTrip(Trip trip) {
        if(trips.contains(trip)){
            trips.remove(trip);
        } else{
            //
        }
    }

    public static void setTrips(ArrayList<Trip> trips) {
        Trip.trips = trips;
    }
}
