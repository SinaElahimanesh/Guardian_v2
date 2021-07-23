package ir.guardianapp.guardian_v2.DrivingStatus.time;

import java.math.BigDecimal;

/**
 * Defines the solar declination used in computing the sunrise/sunset.
 */
public class SunsetSunrise {
    /** Astronomical sunrise/set is when the sun is 18 degrees below the horizon. */
    public static final SunsetSunrise ASTRONOMICAL = new SunsetSunrise(108);

    /** Nautical sunrise/set is when the sun is 12 degrees below the horizon. */
    public static final SunsetSunrise NAUTICAL = new SunsetSunrise(102);

    /** Civil sunrise/set (dawn/dusk) is when the sun is 6 degrees below the horizon. */
    public static final SunsetSunrise CIVIL = new SunsetSunrise(96);

    /** Official sunrise/set is when the sun is 50' below the horizon. */
    public static final SunsetSunrise OFFICIAL = new SunsetSunrise(90.8333);

    private final BigDecimal degrees;

    public SunsetSunrise(double degrees) {
        this.degrees = BigDecimal.valueOf(degrees);
    }

    public BigDecimal degrees() {
        return degrees;
    }
}
