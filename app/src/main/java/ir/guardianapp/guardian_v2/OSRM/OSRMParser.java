package ir.guardianapp.guardian_v2.OSRM;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class OSRMParser {
    private volatile static String jsonString = "";
    private volatile static ArrayList<Step> steps = new ArrayList<>();
    private volatile static ArrayList<Line> lines = new ArrayList<>();
    private volatile static boolean parsingFinished = false;

    public static ArrayList<Step> getSteps() {
        return steps;
    }

    public static ArrayList<Line> getLines() {
        return lines;
    }

    public static void setJSONString(String jsonString) {
        OSRMParser.jsonString = jsonString;
    }

    public static String getJsonString() {
        return jsonString;
    }

    public static void clearData() {
        steps.clear();
        lines.clear();
        parsingFinished = false;
    }

    public static boolean isParsingFinished() {
        return parsingFinished;
    }

    public static void startParsing(OSRMParseFinished osrmParseFinished) {
        OSRMParser.parsingFinished = false;
        new Thread(() -> {
            try {
                JSONObject object = new JSONObject(jsonString);
                JSONArray routes = object.getJSONArray("routes");
                if (routes.length() < 1) return;
                JSONObject firstRoute = routes.getJSONObject(0);
                JSONArray legs = firstRoute.getJSONArray("legs");
                if (legs.length() < 1) return;
                JSONObject firstLeg = legs.getJSONObject(0);
                JSONArray steps = firstLeg.getJSONArray("steps");
                for (int i = 0; i < steps.length(); i++) {
                    Log.d("STEPSS", "" + i);
                    JSONObject stepObject = steps.getJSONObject(i);
                    Step javaStep = new Step();
                    javaStep.drivingSide = stepObject.getString("driving_side");
                    javaStep.name = stepObject.getString("name");
                    javaStep.distance = stepObject.getDouble("distance");
                    javaStep.duration = stepObject.getDouble("duration");
                    javaStep.bearing = stepObject.getJSONObject("maneuver").getInt("bearing_after");
                    javaStep.type = stepObject.getJSONObject("maneuver").getString("type");
                    if(stepObject.getJSONObject("maneuver").has("exit"))
                        javaStep.exit = stepObject.getJSONObject("maneuver").getInt("exit");
                    if(stepObject.getJSONObject("maneuver").has("rotary_name"))
                        javaStep.rotary_name = stepObject.getJSONObject("maneuver").getString("rotary_name");
                    JSONArray jsonCoordinates = stepObject.getJSONObject("geometry").getJSONArray("coordinates");
                    if (jsonCoordinates.length() <= 1) continue;
                    for (int j = 0; j < jsonCoordinates.length() - 1; j++) {
                        Line line = new Line();
                        line.step = new WeakReference<>(javaStep);
                        JSONArray firstArray = jsonCoordinates.getJSONArray(j);
                        JSONArray secondArray = jsonCoordinates.getJSONArray(j + 1);
                        line.beginning = new Location(firstArray.getDouble(1), firstArray.getDouble(0));
                        line.end = new Location(secondArray.getDouble(1), secondArray.getDouble(0));
                        javaStep.lines.add(line);
                        OSRMParser.lines.add(line);
                    }
                    OSRMParser.steps.add(javaStep);
                    Log.d("STEPSsS", "" + OSRMParser.steps.size());
                }
                OSRMParser.parsingFinished = true;
                osrmParseFinished.finished();
            } catch (JSONException e) {
                e.printStackTrace();
                osrmParseFinished.error();
            }
        }).start();
    }
}
