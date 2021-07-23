package ir.guardianapp.guardian_v2.DrivingStatus.time;

import java.util.Calendar;
import java.util.TimeZone;

public class Time {
    public double getTimeHOUR(){
        TimeZone tz = TimeZone.getTimeZone("GMT+4:30");
        Calendar c = Calendar.getInstance(tz);

        return c.getTime().getHours(); //c.HOUR_OF_DAY;
    }

    public double getTimeMINUTE(){
        TimeZone tz = TimeZone.getTimeZone("GMT+4:30");
        Calendar c = Calendar.getInstance(tz);


        return c.getTime().getMinutes(); //c.MINUTE;
    }

    public int getCurrentMonth() {
        return Integer.parseInt(PersianCalender.getCurrentShamsidate());
    }

    public String getSunrise(Location location){
//        Location location = new Location("35.6960", "51.3997"); // Tehran

        // Pass the time zone display here in the second parameter.
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, "GMT+4:30");
        return calculator.getOfficialSunriseForDate(Calendar.getInstance());
    }

    public String getSunset(Location location){
//        Location location = new Location("35.6960", "51.3997"); // Tehran

        // Pass the time zone display here in the second parameter.
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, "GMT+4:30");
        return calculator.getOfficialSunsetForDate(Calendar.getInstance());
    }

    //    private static final String seasons[] = {
//            "Winter3", "Winter4", "Spring1", "Spring2", "Summer1", "Summer2",
//            "Summer3", "Summer4", "Fall1", "Fall2", "Winter1", "Winter2"
//    };
//    public String getSeason( Date date ) {
//        return seasons[ date.getMonth() ];
//    }
}
