package ir.guardianapp.guardian_v2.DrivingStatus.vibration;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import ir.guardianapp.guardian_v2.DrivingStatus.ShakeSituation;

public class Shake {

    private SensorManager sensorManager;
    private Sensor accelometerSensor;
    private boolean isAccelometerSensorAvailible, several = false;
    public float currentX, currentY, currentZ, lastX, lastY, lastZ, xDifference, yDifference, zDifference;
    public ShakeSituation situation = ShakeSituation.noShake;

    public float getxDifference() {
        return xDifference;
    }

    public void setxDifference(float xDifference) {
        this.xDifference = xDifference;
    }

    public float getyDifference() {
        return yDifference;
    }

    public void setyDifference(float yDifference) {
        this.yDifference = yDifference;
    }

    public float getzDifference() {
        return zDifference;
    }

    public void setzDifference(float zDifference) {
        this.zDifference = zDifference;
    }

    public Shake(Context applicationContext) {
        sensorManager = (SensorManager) applicationContext.getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelometerSensorAvailible = true;
        }
        else {
            Log.d("xAccelometer", "Accelometer is not availible");
            isAccelometerSensorAvailible = false;
        }

        Log.d("shake thread", Thread.currentThread().getName());
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Log.d("xAccelometer", sensorEvent.values[0]+ "m/s2");
            Log.d("yAccelometer", sensorEvent.values[1]+ "m/s2");
            Log.d("zAccelometer", sensorEvent.values[2]+ "m/s2");
            Log.d("sensor thread", Thread.currentThread().getName());

            currentX = sensorEvent.values[0];
            currentY = sensorEvent.values[1];
            currentZ = sensorEvent.values[2];

            if(several) {

                xDifference = Math.abs(lastX - currentX);
                yDifference = Math.abs(lastY - currentY);
                zDifference = Math.abs(lastZ - currentZ);

//                if((xDifference > 6f && yDifference > 6f)
//                        || (xDifference > 6f && zDifference > 6f)
//                        || (yDifference > 6f && zDifference > 6f)) {
//                    Log.d("shake situation", Main.ShakeSituation.veryHighShake.toString());
//                    situation = Shake.ShakeSituation.veryHighShake;
//                }
//                else if ((xDifference > 5f && yDifference > 5f)
//                        || (xDifference > 5f && zDifference > 5f)
//                        || (yDifference > 5f && zDifference > 5f)) {
//                    Log.d("shake situation", Main.ShakeSituation.highShake.toString());
//                    situation = Shake.ShakeSituation.highShake;
//                }
//                else if ((xDifference > 3.9f && yDifference > 3.9f)
//                        || (xDifference > 3.9f && zDifference > 3.9f)
//                        || (yDifference > 3.9f && zDifference > 3.9f)) {
//                    Log.d("shake situation", Main.ShakeSituation.mediumShake.toString());
//                    situation = Shake.ShakeSituation.mediumShake;
//                }
//                else if ((xDifference > 2.8f && yDifference > 2.8f)
//                        || (xDifference > 2.8f && zDifference > 2.8f)
//                        || (yDifference > 2.8f && zDifference > 2.8f)) {
//                    Log.d("shake situation", Main.ShakeSituation.lowShake.toString());
//                    situation = Shake.ShakeSituation.lowShake;
//                }
//                else {
//                    Log.d("shake situation", Main.ShakeSituation.noShake.toString());
//                    situation = Shake.ShakeSituation.noShake;
//                }
//                if((xDifference > 7f && yDifference > 7f)
//                        || (xDifference > 7f && zDifference > 7f)
//                        || (yDifference > 7f && zDifference > 7f)) {
//                    //Log.d("shake situation", ShakeSituation.veryHighShake.toString());
//                    situation = ShakeSituation.veryHighShake;
//                }
//                else if ((xDifference > 6f && yDifference > 6f)
//                        || (xDifference > 6f && zDifference > 6f)
//                        || (yDifference > 6f && zDifference > 6f)) {
//                    Log.d("shake situation", ShakeSituation.highShake.toString());
//                    situation = ShakeSituation.highShake;
//                }
//                else if ((xDifference > 5f && yDifference > 5f)
//                        || (xDifference > 5f && zDifference > 5f)
//                        || (yDifference > 5f && zDifference > 5f)) {
//                    Log.d("shake situation", ShakeSituation.mediumShake.toString());
//                    situation = ShakeSituation.mediumShake;
//                }
//                else if ((xDifference > 4f && yDifference > 4f)
//                        || (xDifference > 4f && zDifference > 4f)
//                        || (yDifference > 4f && zDifference > 4f)) {
//                    Log.d("shake situation", ShakeSituation.lowShake.toString());
//                    situation = ShakeSituation.lowShake;
//                }
//                else {
//                    Log.d("shake situation", ShakeSituation.noShake.toString());
//                    situation = ShakeSituation.noShake;
//                }
            }

            lastX = currentX;
            lastY = currentY;
            lastZ = currentZ;
            several = true;

            setxDifference(xDifference);
            setyDifference(yDifference);
            setzDifference(zDifference);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    public void registerUnregister(boolean register) {

        if(register) {
            sensorManager.registerListener(sensorEventListener, accelometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    public float getX() {
        return xDifference;
    }

    public float getY() {
        return yDifference;
    }

    public float getZ() {
        return zDifference;
    }
}
