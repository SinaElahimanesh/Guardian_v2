package ir.guardianapp.guardian_v2.SleepSpeedManager;

import java.util.Date;

public class SleepData {
    private Date sleep;
    private Date wakeUp;
    private Date currentTime;

    public SleepData(Date sleep, Date wakeUp, Date currentTime) {
        this.sleep = sleep;
        this.wakeUp = wakeUp;
        this.currentTime = currentTime;
    }

    public Date getCurrentTime() {
        return currentTime;
    }

    public Date getSleep() {
        return sleep;
    }

    public Date getWakeUp() {
        return wakeUp;
    }
}
