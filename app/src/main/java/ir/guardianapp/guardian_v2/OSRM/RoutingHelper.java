package ir.guardianapp.guardian_v2.OSRM;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import ir.guardianapp.guardian_v2.R;

public class RoutingHelper {
    public static double remainingDistance(Step from) {
        int index = OSRMParser.getSteps().indexOf(from);
        if (index < 0) return 0;
        double totalDistance = 0;
        for (int i = index; i < OSRMParser.getSteps().size(); i++) {
            totalDistance += OSRMParser.getSteps().get(i).getDistance();
        }
        return totalDistance;
    }

    public static double remainingTime(Step from) {
        int index = OSRMParser.getSteps().indexOf(from);
        if (index < 0) return 0;
        double totalTime = 0;
        for (int i = index; i < OSRMParser.getSteps().size(); i++) {
            totalTime += OSRMParser.getSteps().get(i).getDuration();
        }
        return totalTime;
    }

    public static double distanceBetween(LatLng latLng1, LatLng latLng2) {
        return (latLng1.latitude - latLng2.latitude) * (latLng1.latitude - latLng2.latitude) +
                (latLng1.longitude - latLng2.longitude) * (latLng1.longitude - latLng2.longitude);
    }

    @Nullable
    public static Step whichStepWeAreNearTo(Location location, int bearing) {
        LatLng locationLatLng = new LatLng(location.getLat(), location.getLon());
        double minDistance = 10_000_000_00;
        Step minStep = null;
        for (Step step : OSRMParser.getSteps()) {
            if (minStep == null) minStep = step;
            double minDistanceOfStep = 10_000_000_00;
            Line minLine = null;
            for (Line line : step.getLines()) {
                double distance = distanceBetween(line.getEnd(), locationLatLng);
                if (distance < minDistanceOfStep) {
                    minDistanceOfStep = distance;
                    minLine = line;
                }
            }
            if (minLine != null && minDistanceOfStep < minDistance && Math.abs(step.bearing-bearing)<90) {
                minDistance = minDistanceOfStep;
                minStep = step;
            }
        }
        return minStep;
    }

    public static int getStepImage(Step minStep) {
        if (minStep.getType().equals("roundabout") || minStep.getType().equals("rotary")) {
            return R.drawable.ic_roundabout;
        } else if (minStep.getType().equals("arrive")) {
            return R.drawable.ic_arrive;
        } else if (minStep.getDrivingSide().equals("left") | minStep.getDrivingSide().equals("sharp left") || minStep.getDrivingSide().equals("slight left")) {
            return R.drawable.ic_turn_left;
        } else if (minStep.getDrivingSide().equals("right") | minStep.getDrivingSide().equals("sharp right") || minStep.getDrivingSide().equals("slight right")){
            return R.drawable.ic_turn_right;
        } else if (minStep.getDrivingSide().equals("uturn")) {
            return R.drawable.ic_u_turn;
        }
        return R.drawable.ic_straight;
    }

    public static String getStepText(Step minStep, double dist) {
        System.out.println(minStep.getType());

        long remainingDist = Math.round((minStep.getDistance()-dist)/10.0) * 10;
        if(remainingDist<0) {
            remainingDist = 0;
        }

        if(minStep.getType().equalsIgnoreCase("turn")) {
            if(minStep.getDrivingSide().equalsIgnoreCase("right")) {
                return "پس از " + calculateDistance(remainingDist) + " به راست بپیچید";
            } else  if(minStep.getDrivingSide().equalsIgnoreCase("left")) {
                return "پس از " + calculateDistance(remainingDist) + " به چپ بپیچید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("uturn")) {
                return "پس از " + calculateDistance(remainingDist) + " دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp right")) {
                return "پس از " + calculateDistance(remainingDist) + " به راست دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight right")) {
                return "پس از " + calculateDistance(remainingDist) + " به آرامی به راست بپیچید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp left")) {
                return "پس از " + calculateDistance(remainingDist) + " به چپ دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight left")) {
                return "پس از " + calculateDistance(remainingDist) + " به آرامی به چپ بپیچید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("straight")) {
                return "پس از " + calculateDistance(remainingDist) + " مستقیم به مسیر خود ادامه دهید";
            }
        } else if(minStep.getType().equalsIgnoreCase("new name")) {
            if(minStep.getDrivingSide().equalsIgnoreCase("right")) {
                return "پس از " + calculateDistance(remainingDist) + " به راست بپیچید";
            } else  if(minStep.getDrivingSide().equalsIgnoreCase("left")) {
                return "پس از " + calculateDistance(remainingDist) + " به چپ بپیچید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("uturn")) {
                return "پس از " + calculateDistance(remainingDist) + " دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp right")) {
                return "پس از " + calculateDistance(remainingDist) + " به راست دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight right")) {
                return "پس از " + calculateDistance(remainingDist) + " به آرامی به راست بپیچید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp left")) {
                return "پس از " + calculateDistance(remainingDist) + " به چپ دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight left")) {
                return "پس از " + calculateDistance(remainingDist) + " به آرامی به چپ بپیچید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("straight")) {
                return "پس از " + calculateDistance(remainingDist) + " مستقیم به مسیر خود ادامه دهید";
            }
        } else if(minStep.getType().equalsIgnoreCase("depart")) {
            if(minStep.getDrivingSide().equalsIgnoreCase("right")) {
                return "پس از " + calculateDistance(remainingDist) + " به راست بپیچید";
            } else  if(minStep.getDrivingSide().equalsIgnoreCase("left")) {
                return "پس از " + calculateDistance(remainingDist) + " به چپ بپیچید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("uturn")) {
                return "پس از " + calculateDistance(remainingDist) + " دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp right")) {
                return "پس از " + calculateDistance(remainingDist) + " به راست دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight right")) {
                return "پس از " + calculateDistance(remainingDist) + " به آرامی به راست بپیچید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp left")) {
                return "پس از " + calculateDistance(remainingDist) + " به چپ دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight left")) {
                return "پس از " + calculateDistance(remainingDist) + " به آرامی به چپ بپیچید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("straight")) {
                return "پس از " + calculateDistance(remainingDist) + " مستقیم به مسیر خود ادامه دهید";
            }
        } else if(minStep.getType().equalsIgnoreCase("arrive")) {
            return "پس از " + calculateDistance(remainingDist) + " به مقصد رسیده اید";
        } else if(minStep.getType().equalsIgnoreCase("merge")) {
            return "پس از " + calculateDistance(remainingDist) + " به مسیر می پیوندید";
        }  else if(minStep.getType().equalsIgnoreCase("on ramp")) {
            if(minStep.getDrivingSide().equalsIgnoreCase("right")) {
                return "پس از " + calculateDistance(remainingDist) + " از خروجی راست وارد شوید";
            } else  if(minStep.getDrivingSide().equalsIgnoreCase("left")) {
                return "پس از " + calculateDistance(remainingDist) + " از خروجی چپ وارد شوید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("uturn")) {
                return "پس از " + calculateDistance(remainingDist) + " از خروجی دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp right")) {
                return "پس از " + calculateDistance(remainingDist) + " از خروجی به راست دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight right")) {
                return "پس از " + calculateDistance(remainingDist) + " از خروجی راست به آرامی وارد شوید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp left")) {
                return "پس از " + calculateDistance(remainingDist) + " از خروجی به چپ دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight left")) {
                return "پس از " + calculateDistance(remainingDist) + " از خروجی چپ به آرامی وارد شوید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("straight")) {
                return "پس از " + calculateDistance(remainingDist) + " مستقیم به مسیر خود ادامه دهید";
            }
        } else if(minStep.getType().equalsIgnoreCase("off ramp")) {
            if(minStep.getDrivingSide().equalsIgnoreCase("right")) {
                return "پس از " + calculateDistance(remainingDist) + " از خروجی راست خارج شوید";
            } else  if(minStep.getDrivingSide().equalsIgnoreCase("left")) {
                return "پس از " + calculateDistance(remainingDist) + " از خروجی چپ خارج شوید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("uturn")) {
                return "پس از " + calculateDistance(remainingDist) + " از خروجی دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp right")) {
                return "پس از " + calculateDistance(remainingDist) + " از خروجی به راست دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight right")) {
                return "پس از " + calculateDistance(remainingDist) + " از خروجی راست به آرامی خارج شوید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp left")) {
                return "پس از " + calculateDistance(remainingDist) + " از خروجی به چپ دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight left")) {
                return "پس از " + calculateDistance(remainingDist) + " از خروجی چپ به آرامی خارج شوید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("straight")) {
                return "پس از " + calculateDistance(remainingDist) + " مستقیم به مسیر خود ادامه دهید";
            }
        } else if(minStep.getType().equalsIgnoreCase("fork")) {
            if(minStep.getDrivingSide().equalsIgnoreCase("right")) {
                return "پس از " + calculateDistance(remainingDist) + " در تقاطع به راست بروید";
            } else  if(minStep.getDrivingSide().equalsIgnoreCase("left")) {
                return "پس از " + calculateDistance(remainingDist) + " در تقاطع به چپ بروید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("uturn")) {
                return "پس از " + calculateDistance(remainingDist) + " در تقاطع دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp right")) {
                return "پس از " + calculateDistance(remainingDist) + " در تقاطع به راست دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight right")) {
                return "پس از " + calculateDistance(remainingDist) + " در تقاطع به آرامی به راست بروید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp left")) {
                return "پس از " + calculateDistance(remainingDist) + " در تقاطع به چپ دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight left")) {
                return "پس از " + calculateDistance(remainingDist) + " در تقاطع به آرامی به چپ بروید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("straight")) {
                return "پس از " + calculateDistance(remainingDist) + " در تقاطع مستقیم ادامه دهید";
            }
        } else if(minStep.getType().equalsIgnoreCase("end of road")) {
            if(minStep.getDrivingSide().equalsIgnoreCase("right")) {
                return "پس از " + calculateDistance(remainingDist) + " به آرامی از راست خارج شوید";
            } else  if(minStep.getDrivingSide().equalsIgnoreCase("left")) {
                return "پس از " + calculateDistance(remainingDist) + " به آرامی از چپ خارج شوید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("uturn")) {
                return "پس از " + calculateDistance(remainingDist) + " به آرامی دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp right")) {
                return "پس از " + calculateDistance(remainingDist) + " به آرامی از راست خارج شوید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight right")) {
                return "پس از " + calculateDistance(remainingDist) + " به آرامی از راست خارج شوید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp left")) {
                return "پس از " + calculateDistance(remainingDist) + " به آرامی از چپ خارج شوید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight left")) {
                return "پس از " + calculateDistance(remainingDist) + " به آرامی از چپ خارج شوید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("straight")) {
                return "پس از " + calculateDistance(remainingDist) + " به آرامی در مسیر خود ادامه دهید";
            }
        } else if(minStep.getType().equalsIgnoreCase("use lane")) {
            return "پس از " + calculateDistance(remainingDist) + " در مسیر خود ادامه دهید";
        } else if(minStep.getType().equalsIgnoreCase("continue")) {
            if(minStep.getDrivingSide().equalsIgnoreCase("right")) {
                return "پس از " + calculateDistance(remainingDist) + " از راست در مسیر خود ادامه دهید";
            } else  if(minStep.getDrivingSide().equalsIgnoreCase("left")) {
                return "پس از " + calculateDistance(remainingDist) + " از چپ در مسیر خود ادامه دهید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("uturn")) {
                return "پس از " + calculateDistance(remainingDist) + " در مسیر خود دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp right")) {
                return "پس از " + calculateDistance(remainingDist) + " از راست در مسیر خود ادامه دهید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight right")) {
                return "پس از " + calculateDistance(remainingDist) + " از راست در مسیر خود ادامه دهید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp left")) {
                return "پس از " + calculateDistance(remainingDist) + " از چپ در مسیر خود ادامه دهید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight left")) {
                return "پس از " + calculateDistance(remainingDist) + " از چپ در مسیر خود ادامه دهید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("straight")) {
                return "پس از " + calculateDistance(remainingDist) + " مستقیم به مسیر خود ادامه دهید";
            }
        } else if(minStep.getType().equalsIgnoreCase("roundabout")) {
            return "پس از " + calculateDistance(remainingDist) + "در میدان " + roundAboutString(minStep.getExit()) + " خارج شوید";
        } else if(minStep.getType().equalsIgnoreCase("rotary")) {
            return "پس از " + calculateDistance(remainingDist) + "در میدان " + minStep.getRotary_name() + " " + roundAboutString(minStep.getExit()) + " خارج شوید";
        } else if(minStep.getType().equalsIgnoreCase("roundabout turn")) {
            if(minStep.getDrivingSide().equalsIgnoreCase("right")) {
                return "پس از " + calculateDistance(remainingDist) + " به راست بپیچید";
            } else  if(minStep.getDrivingSide().equalsIgnoreCase("left")) {
                return "پس از " + calculateDistance(remainingDist) + " به چپ بپیچید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("uturn")) {
                return "پس از " + calculateDistance(remainingDist) + " دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp right")) {
                return "پس از " + calculateDistance(remainingDist) + " به راست دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight right")) {
                return "پس از " + calculateDistance(remainingDist) + " به آرامی به راست بپیچید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp left")) {
                return "پس از " + calculateDistance(remainingDist) + " به چپ دور بزنید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight left")) {
                return "پس از " + calculateDistance(remainingDist) + " به آرامی به چپ بپیچید";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("straight")) {
                return "پس از " + calculateDistance(remainingDist) + " مستقیم به مسیر خود ادامه دهید";
            }
        } else if(minStep.getType().equalsIgnoreCase("notification")) {
            if(minStep.getDrivingSide().equalsIgnoreCase("right")) {
                return "پس از " + calculateDistance(remainingDist) + " جاده به راست می پیچد";
            } else  if(minStep.getDrivingSide().equalsIgnoreCase("left")) {
                return "پس از " + calculateDistance(remainingDist) + " جاده به چپ می پیچد";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("uturn")) {
                return "پس از " + calculateDistance(remainingDist) + " جاده دور می زند";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp right")) {
                return "پس از " + calculateDistance(remainingDist) + " جاده پیچ خطرناک به راست دارد";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight right")) {
                return "پس از " + calculateDistance(remainingDist) + " جاده به راست می پیچد";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("sharp left")) {
                return "پس از " + calculateDistance(remainingDist) + " جاده پیچ خطرناک به چپ دارد";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("slight left")) {
                return "پس از " + calculateDistance(remainingDist) + " جاده به چپ می پیچد";
            } else if(minStep.getDrivingSide().equalsIgnoreCase("straight")) {
                return "پس از " + calculateDistance(remainingDist) + " در مسیر مستقیم ادامه دهید";
            }
        }
        return "در مسیر خود ادامه دهید";
    }

    public static String roundAboutString(int exit) {
        switch (exit) {
            case 1:
                return "از اولین خروجی";
            case 2:
                return "از دومین خروجی";
            case 3:
                return "از سومین خروجی";
            case 4:
                return "از چهارمین خروجی";
            case 5:
                return "از پنجمین خروجی";
            case 6:
                return "از ششمین خروجی";
            case 7:
                return "از هفتمین خروجی";
            default:
                return "";
        }
    }

    public static String calculateDistance(double distance){
        int kiloMeter = (int) (distance/1000);
        int meter = (int) (distance%1000);

        if(kiloMeter<1){
            return ((meter) + " متر");
        } else if(kiloMeter>=20) {
            return (kiloMeter + " کیلومتر");
        } else {
            meter/=100;
            return (kiloMeter + "." + meter + " کیلومتر");
        }
    }

    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
