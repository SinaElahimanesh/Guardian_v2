package ir.guardianapp.guardian_v2.DrivingPercentage;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;

public class DriveAlertHandler {

    private String alertMessage;
    private int repetition;
    private boolean isTooImportant;
    private Type type;
    private String soundURL;
    public static boolean changed = true;

    private static DriveAlertHandler currentAlert = new DriveAlertHandler("", 0, false, Type.NONE, "");

    // number of cycles
    private static final int sleep_timeGap = 24;
    private static final int speed_timeGap = 1;
    private static final int acceleration_timeGap = 1;
    private static final int vibration_timeGap = 1;
    private static final int time_timeGap = 30;
    private static final int nearCities_timeGap = 8;
    private static final int month_timeGap = 240;
    private static final int weather_timeGap = 22;
    private static final int withoutStop_timeGap = 14;
//    private static final int traffic_timeGap = 5;
    private static final int roadType_timeGap = 24;
    private static final int restArea_timeGap = 16;

    //
    private static int sleep_restTime = 0;
    private static int speed_restTime = 0;
    private static int acceleration_restTime = 0;
    private static int vibration_restTime = 0;
    private static int time_restTime = 0;
    private static int nearCities_restTime = 0;
    private static int month_restTime = 0;
    private static int weather_restTime = 0;
    private static int withoutStop_restTime = 0;
//    private static int traffic_restTime = 0;
    private static int roadType_restTime = 0;
    private static int restArea_restTime = 0;

    //
    public static DriveAlertHandler sleep_alert = new DriveAlertHandler("", 1, false, Type.NONE, "");
    public static DriveAlertHandler speed_alert = new DriveAlertHandler("", 1, false, Type.NONE, "");
    public static DriveAlertHandler acceleration_alert = new DriveAlertHandler("", 1, false, Type.NONE, "");
    public static DriveAlertHandler vibration_alert = new DriveAlertHandler("", 1, false, Type.NONE, "");
    public static DriveAlertHandler time_alert = new DriveAlertHandler("", 1, false, Type.NONE, "");
    public static DriveAlertHandler nearCities_alert = new DriveAlertHandler("", 1, false, Type.NONE, "");
    public static DriveAlertHandler month_alert = new DriveAlertHandler("", 1, false, Type.NONE, "");
    public static DriveAlertHandler weather_alert = new DriveAlertHandler("", 1, false, Type.NONE, "");
    public static DriveAlertHandler withoutStop_alert = new DriveAlertHandler("", 1, false, Type.NONE, "");
//    public static DriveAlertHandler traffic_alert = new DriveAlertHandler("", 1, false, Type.NONE, "");
    public static DriveAlertHandler roadType_alert = new DriveAlertHandler("", 1, false, Type.NONE,"");
    //
    public static DriveAlertHandler restArea = new DriveAlertHandler("", 1, false, Type.REST_AREA, "");

    public enum Type {
        SLEEP,
        SPEED,
        ACCELERATION,
        VIBRATION,
        TIME,
        NEAR_CITIES,
        MONTH,
        WEATHER,
        WITHOUT_STOP,
//        TRAFFIC,
        ROAD_TYPE,
        NONE,

        REST_AREA
    }

    public static Type getCurrentAlertType() {
        return currentAlert.type;
    }

    public DriveAlertHandler(String alertMessage, int repetition, boolean tooImportant, Type type, String soundURL) {
        setAlertMessage(alertMessage);
        setRepetition(repetition);
        setTooImportant(tooImportant);
        setType(type);
        setSoundURL(soundURL);
    }

    public static void sleep_func(String alertMessage, int repetition, boolean tooImportant, Type type, String soundURL) {
        if(((repetition > 0) && (sleep_restTime <= 0)) || (tooImportant && (sleep_restTime <= 9))) {
            sleep_alert.setAlertMessage(alertMessage);
            sleep_alert.setRepetition(repetition);
            sleep_alert.setTooImportant(tooImportant);
            sleep_alert.setType(type);
            sleep_alert.setSoundURL(soundURL);
        } else if (repetition == 0) {
            sleep_alert.setAlertMessage("");
            sleep_alert.setRepetition(1);
            sleep_alert.setTooImportant(false);
            sleep_alert.setType(Type.SLEEP);
            sleep_alert.setSoundURL("");
        }
    }
    public static void speed_func(String alertMessage, int repetition, boolean tooImportant, Type type, String soundURL) {
        if(((repetition > 0) && (speed_restTime <= 0)) || (tooImportant && (speed_restTime <= 9))) {
            speed_alert.setAlertMessage(alertMessage);
            speed_alert.setRepetition(repetition);
            speed_alert.setTooImportant(tooImportant);
            speed_alert.setType(type);
            speed_alert.setSoundURL(soundURL);
        } else if (repetition == 0) {
            speed_alert.setAlertMessage("");
            speed_alert.setRepetition(1);
            speed_alert.setTooImportant(false);
            speed_alert.setType(Type.SPEED);
            speed_alert.setSoundURL("");
        }
    }
    public static void acceleration_func(String alertMessage, int repetition, boolean tooImportant, Type type, String soundURL) {
        if(((repetition > 0) && (acceleration_restTime <= 0)) || (tooImportant && (acceleration_restTime <= 9))) {
            acceleration_alert.setAlertMessage(alertMessage);
            acceleration_alert.setRepetition(repetition);
            acceleration_alert.setTooImportant(tooImportant);
            acceleration_alert.setType(type);
            acceleration_alert.setSoundURL(soundURL);
        } else if (repetition == 0) {
            acceleration_alert.setAlertMessage("");
            acceleration_alert.setRepetition(1);
            acceleration_alert.setTooImportant(false);
            acceleration_alert.setType(Type.ACCELERATION);
            acceleration_alert.setSoundURL("");
        }
    }
    public static void vibration_func(String alertMessage, int repetition, boolean tooImportant, Type type, String soundURL) {
        if(((repetition > 0) && (vibration_restTime <= 0)) || (tooImportant && (vibration_restTime <= 9))) {
            vibration_alert.setAlertMessage(alertMessage);
            vibration_alert.setRepetition(repetition);
            vibration_alert.setTooImportant(tooImportant);
            vibration_alert.setType(type);
            vibration_alert.setSoundURL(soundURL);
        } else if (repetition == 0) {
            vibration_alert.setAlertMessage("");
            vibration_alert.setRepetition(1);
            vibration_alert.setTooImportant(false);
            vibration_alert.setType(Type.VIBRATION);
            vibration_alert.setSoundURL("");
        }
    }
    public static void time_func(String alertMessage, int repetition, boolean tooImportant, Type type, String soundURL) {
        if(((repetition > 0) && (time_restTime <= 0)) || (tooImportant && (time_restTime <= 9))) {
            time_alert.setAlertMessage(alertMessage);
            time_alert.setRepetition(repetition);
            time_alert.setTooImportant(tooImportant);
            time_alert.setType(type);
            time_alert.setSoundURL(soundURL);
        } else if (repetition == 0) {
            time_alert.setAlertMessage("");
            time_alert.setRepetition(1);
            time_alert.setTooImportant(false);
            time_alert.setType(Type.TIME);
            time_alert.setSoundURL("");
        }
    }
    public static void nearCities_func(String alertMessage, int repetition, boolean tooImportant, Type type, String soundURL) {
        if(((repetition > 0) && (nearCities_restTime <= 0)) || (tooImportant && (nearCities_restTime <= 9))) {
            nearCities_alert.setAlertMessage(alertMessage);
            nearCities_alert.setRepetition(repetition);
            nearCities_alert.setTooImportant(tooImportant);
            nearCities_alert.setType(type);
            nearCities_alert.setSoundURL(soundURL);
        } else if (repetition == 0) {
            nearCities_alert.setAlertMessage("");
            nearCities_alert.setRepetition(1);
            nearCities_alert.setTooImportant(false);
            nearCities_alert.setType(Type.NEAR_CITIES);
            nearCities_alert.setSoundURL("");
        }
    }
    public static void month_func(String alertMessage, int repetition, boolean tooImportant, Type type, String soundURL) {
        if(((repetition > 0) && (month_restTime <= 0)) || (tooImportant && (month_restTime <= 9))) {
            month_alert.setAlertMessage(alertMessage);
            month_alert.setRepetition(repetition);
            month_alert.setTooImportant(tooImportant);
            month_alert.setType(type);
            month_alert.setSoundURL(soundURL);
        } else if (repetition == 0) {
            month_alert.setAlertMessage("");
            month_alert.setRepetition(1);
            month_alert.setTooImportant(false);
            month_alert.setType(Type.MONTH);
            month_alert.setSoundURL("");
        }
    }
    public static void weather_func(String alertMessage, int repetition, boolean tooImportant, Type type, String soundURL) {
        if(((repetition > 0) && (weather_restTime <= 0)) || (tooImportant && (weather_restTime <= 9))) {
            weather_alert.setAlertMessage(alertMessage);
            weather_alert.setRepetition(repetition);
            weather_alert.setTooImportant(tooImportant);
            weather_alert.setType(type);
            weather_alert.setSoundURL(soundURL);
        } else if (repetition == 0) {
            weather_alert.setAlertMessage("");
            weather_alert.setRepetition(1);
            weather_alert.setTooImportant(false);
            weather_alert.setType(Type.WEATHER);
            weather_alert.setSoundURL("");
        }
    }
    public static void withoutStop_func(String alertMessage, int repetition, boolean tooImportant, Type type, String soundURL) {
        if(((repetition > 0) && (withoutStop_restTime <= 0)) || (tooImportant && (withoutStop_restTime <= 9))) {
            withoutStop_alert.setAlertMessage(alertMessage);
            withoutStop_alert.setRepetition(repetition);
            withoutStop_alert.setTooImportant(tooImportant);
            withoutStop_alert.setType(type);
            withoutStop_alert.setSoundURL(soundURL);
        } else if (repetition == 0) {
            withoutStop_alert.setAlertMessage("");
            withoutStop_alert.setRepetition(1);
            withoutStop_alert.setTooImportant(false);
            withoutStop_alert.setType(Type.WITHOUT_STOP);
            withoutStop_alert.setSoundURL("");
        }
    }
//    public static void traffic_func(String alertMessage, int repetition, boolean tooImportant, Type type, String soundURL) {
//        if(((repetition > 0) && (traffic_restTime <= 0)) || (tooImportant && (traffic_restTime <= 9))) {
//            traffic_alert.setAlertMessage(alertMessage);
//            traffic_alert.setRepetition(repetition);
//            traffic_alert.setTooImportant(tooImportant);
//            traffic_alert.setType(type);
//            traffic_alert.setSoundURL(soundURL);
//        }
//    }
    public static void roadType_func(String alertMessage, int repetition, boolean tooImportant, Type type, String soundURL) {
        if(((repetition > 0) && (roadType_restTime <= 0)) || (tooImportant && (roadType_restTime <= 9))) {
            roadType_alert.setAlertMessage(alertMessage);
            roadType_alert.setRepetition(repetition);
            roadType_alert.setTooImportant(tooImportant);
            roadType_alert.setType(type);
            roadType_alert.setSoundURL(soundURL);
        } else if (repetition == 0) {
            roadType_alert.setAlertMessage("");
            roadType_alert.setRepetition(1);
            roadType_alert.setTooImportant(false);
            roadType_alert.setType(Type.ROAD_TYPE);
            roadType_alert.setSoundURL("");
        }
    }

    public static void restArea_func(String alertMessage, int repetition, boolean tooImportant, Type type, String soundURL) {
        if(((repetition > 0) && (restArea_restTime <= 0)) || (tooImportant && (restArea_restTime <= 9))) {
            restArea.setAlertMessage(alertMessage);
            restArea.setRepetition(repetition);
            restArea.setTooImportant(tooImportant);
            restArea.setType(type);
            restArea.setSoundURL(soundURL);
        } else if (repetition == 0) {
            restArea.setAlertMessage("");
            restArea.setRepetition(1);
            restArea.setTooImportant(false);
            restArea.setType(Type.REST_AREA);
            restArea.setSoundURL("");
        }
    }

    private void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    private void setRepetition(int repetition) {
        this.repetition = repetition;
    }

    public void setTooImportant(boolean tooImportant) {
        isTooImportant = tooImportant;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setSoundURL(String soundURL) {
        this.soundURL = soundURL;
    }

    public static void playSound(Context context) {
//        Sound.playSound(context, currentAlert.soundURL);
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public static void passCycle(){
        changed = false;

       if(currentAlert.repetition > 0) {
           currentAlert.repetition --;
       } else {
           if(currentAlert.type == Type.SLEEP) {
               sleep_func("", 0, false, Type.SLEEP, "");
           } else if(currentAlert.type == Type.SPEED) {
               speed_func("", 0, false, Type.SPEED, "");
           } else if(currentAlert.type == Type.ACCELERATION) {
               acceleration_func("", 0, false, Type.ACCELERATION, "");
           } else if(currentAlert.type == Type.VIBRATION) {
               vibration_func("", 0, false, Type.VIBRATION, "");
           } else if(currentAlert.type == Type.TIME) {
               time_func("", 0, false, Type.TIME, "");
           } else if(currentAlert.type == Type.NEAR_CITIES) {
               nearCities_func("", 0, false, Type.NEAR_CITIES, "");
           } else if(currentAlert.type == Type.MONTH) {
               month_func("", 0, false, Type.MONTH, "");
           } else if(currentAlert.type == Type.WEATHER) {
               weather_func("", 0, false, Type.WEATHER, "");
           } else if(currentAlert.type == Type.WITHOUT_STOP) {
               withoutStop_func("", 0, false, Type.WITHOUT_STOP, "");
           } else if(currentAlert.type == Type.ROAD_TYPE) {
               roadType_func("", 0, false, Type.ROAD_TYPE, "");
           } else if(currentAlert.type == Type.REST_AREA) {
            restArea_func("", 0, false, Type.REST_AREA, "");
           }
       }

        if(sleep_restTime > 0) {
             sleep_restTime --;
         }
        if(speed_restTime > 0) {
            speed_restTime --;
        }
        if(acceleration_restTime > 0) {
            acceleration_restTime --;
        }
        if(vibration_restTime > 0) {
            vibration_restTime --;
        }
        if(time_restTime > 0) {
            time_restTime --;
        }
        if(nearCities_restTime > 0) {
            nearCities_restTime --;
        }
        if(month_restTime > 0) {
            month_restTime --;
        }
        if(weather_restTime > 0) {
            weather_restTime --;
        }
        if(withoutStop_restTime > 0) {
            withoutStop_restTime --;
        }
        if(roadType_restTime > 0) {
            roadType_restTime --;
        }
        if(restArea_restTime > 0) {
            restArea_restTime --;
        }
//        if(traffic_restTime > 0) {
//            traffic_restTime --;
//        }
    }

    public static String toShowAlert() {

        String toShowStr = "با دقت به رانندگی ادامه دهید.";
        Log.d("Alert", "alert" + currentAlert.type + "repetition: " + currentAlert.repetition);

        if(currentAlert.repetition > 0 && currentAlert.isTooImportant) {
            toShowStr = currentAlert.getAlertMessage();
            return toShowStr;
        }


        if(restArea.isTooImportant && restArea.repetition>0) {
            toShowStr = speed_alert.getAlertMessage();
            changed = true;
            restArea_restTime = restArea_timeGap;
            currentAlert = restArea;
            return toShowStr;
        }
        // zire 30 ha
        if(speed_alert.isTooImportant && speed_alert.repetition>0) {
            toShowStr = speed_alert.getAlertMessage();
            changed = true;
            speed_restTime = speed_timeGap;
            currentAlert = speed_alert;
            return toShowStr;
        } else if(sleep_alert.isTooImportant && sleep_alert.repetition>0) {
            changed = true;
            toShowStr = sleep_alert.getAlertMessage();
            sleep_restTime = sleep_timeGap;
            currentAlert = sleep_alert;
            return toShowStr;
        } else if(withoutStop_alert.isTooImportant && withoutStop_alert.repetition>0) {
            changed = true;
            toShowStr = withoutStop_alert.getAlertMessage();
            withoutStop_restTime = withoutStop_timeGap;
            currentAlert = withoutStop_alert;
            return toShowStr;
        } else if(acceleration_alert.isTooImportant && acceleration_alert.repetition>0) {
            changed = true;
            toShowStr = acceleration_alert.getAlertMessage();
            acceleration_restTime = acceleration_timeGap;
            currentAlert = acceleration_alert;
            return toShowStr;
        } else if(vibration_alert.isTooImportant && vibration_alert.repetition>0) {
            changed = true;
            toShowStr = vibration_alert.getAlertMessage();
            vibration_restTime = vibration_timeGap;
            currentAlert = vibration_alert;
            return toShowStr;
        } else if(time_alert.isTooImportant && time_alert.repetition>0) {
            changed = true;
            toShowStr = time_alert.getAlertMessage();
            time_restTime = time_timeGap;
            currentAlert = time_alert;
            return toShowStr;
        } else if(nearCities_alert.isTooImportant && nearCities_alert.repetition>0) {
            changed = true;
            toShowStr = nearCities_alert.getAlertMessage();
            nearCities_restTime = nearCities_timeGap;
            currentAlert = nearCities_alert;
            return toShowStr;
        }  else if(weather_alert.isTooImportant && weather_alert.repetition>0) {
            changed = true;
            toShowStr = weather_alert.getAlertMessage();
            weather_restTime = weather_timeGap;
            currentAlert = weather_alert;
            return toShowStr;
        } else if(roadType_alert.isTooImportant && roadType_alert.repetition>0) {
            changed = true;
            toShowStr = roadType_alert.getAlertMessage();
            roadType_restTime = roadType_timeGap;
            currentAlert = roadType_alert;
            return toShowStr;
        } else if(month_alert.isTooImportant && month_alert.repetition>0) {
            changed = true;
            toShowStr = month_alert.getAlertMessage();
            month_restTime = month_timeGap;
            currentAlert = month_alert;
            return toShowStr;
        }
//        else if(traffic_alert.isTooImportant && traffic_alert.repetition>1) {
//            changed = true;
//            toShowStr = traffic_alert.getAlertMessage();
//            traffic_restTime = traffic_timeGap;
//            currentAlert = traffic_alert;
//            return toShowStr;
//        }


        if(currentAlert.repetition > 0) {
            toShowStr = currentAlert.getAlertMessage();
            return toShowStr;
        } else {

            HashMap<DriveAlertHandler, Integer> toShowAlertsList = new HashMap<>();
            if(!speed_alert.getAlertMessage().equalsIgnoreCase("") && speed_alert.repetition>0) {
               toShowAlertsList.put(speed_alert, speed_restTime);
            } else if(!sleep_alert.getAlertMessage().equalsIgnoreCase("") && sleep_alert.repetition>0) {
                toShowAlertsList.put(sleep_alert, sleep_restTime);
            } else if(!withoutStop_alert.getAlertMessage().equalsIgnoreCase("") && withoutStop_alert.repetition>0) {
                toShowAlertsList.put(withoutStop_alert, withoutStop_restTime);
            } else if(!acceleration_alert.getAlertMessage().equalsIgnoreCase("") && acceleration_alert.repetition>0) {
                toShowAlertsList.put(acceleration_alert, acceleration_restTime);
            } else if(!vibration_alert.getAlertMessage().equalsIgnoreCase("") && vibration_alert.repetition>0) {
                toShowAlertsList.put(vibration_alert, vibration_restTime);
            } else if(!time_alert.getAlertMessage().equalsIgnoreCase("") && time_alert.repetition>0) {
                toShowAlertsList.put(time_alert, time_restTime);
            } else if(!nearCities_alert.getAlertMessage().equalsIgnoreCase("") && nearCities_alert.repetition>0) {
                toShowAlertsList.put(nearCities_alert, nearCities_restTime);
            } else if(!weather_alert.getAlertMessage().equalsIgnoreCase("") && weather_alert.repetition>0) {
                toShowAlertsList.put(weather_alert, weather_restTime);
            } else if(!roadType_alert.getAlertMessage().equalsIgnoreCase("") && roadType_alert.repetition>0) {
                toShowAlertsList.put(roadType_alert, roadType_restTime);
            } else if(!month_alert.getAlertMessage().equalsIgnoreCase("") && month_alert.repetition>0) {
                toShowAlertsList.put(month_alert, month_restTime);
            } else if(!restArea.getAlertMessage().equalsIgnoreCase("") && restArea.repetition>0) {
                toShowAlertsList.put(restArea, restArea_restTime);
            }
//            else if(!traffic_alert.getAlertMessage().equalsIgnoreCase("")) {
//                toShowAlertsList.put(traffic_alert, traffic_restTime);
//            }

            int min = 1000;
            for (DriveAlertHandler driveAlertHandler : toShowAlertsList.keySet()) {
                if(toShowAlertsList.get(driveAlertHandler) < min) {
                    currentAlert = driveAlertHandler;
                    min = toShowAlertsList.get(driveAlertHandler);
                }
            }
            if(toShowAlertsList.size()==0) {
                toShowStr = "";
                return toShowStr;
            }

            changed = true;
            toShowStr = currentAlert.getAlertMessage();
            if(currentAlert.type == Type.SLEEP) {
                sleep_restTime = sleep_timeGap;
            } else if(currentAlert.type == Type.SPEED) {
                speed_restTime = speed_timeGap;
            } else if(currentAlert.type == Type.ACCELERATION) {
                acceleration_restTime = acceleration_timeGap;
            } else if(currentAlert.type == Type.VIBRATION) {
                vibration_restTime = vibration_timeGap;
            } else if(currentAlert.type == Type.TIME) {
                time_restTime = time_timeGap;
            } else if(currentAlert.type == Type.NEAR_CITIES) {
                nearCities_restTime = nearCities_timeGap;
            } else if(currentAlert.type == Type.MONTH) {
                month_restTime = month_timeGap;
            } else if(currentAlert.type == Type.WEATHER) {
                weather_restTime = weather_timeGap;
            } else if(currentAlert.type == Type.WITHOUT_STOP) {
                withoutStop_restTime = withoutStop_timeGap;
            } else if(currentAlert.type == Type.ROAD_TYPE) {
                roadType_restTime = roadType_timeGap;
            } else if(currentAlert.type == Type.REST_AREA) {
                restArea_restTime = restArea_timeGap;
            }
//            else if(currentAlert.type == Type.TRAFFIC) {
//                traffic_restTime = traffic_timeGap;
//            }

            return toShowStr;
        }
    }
}
