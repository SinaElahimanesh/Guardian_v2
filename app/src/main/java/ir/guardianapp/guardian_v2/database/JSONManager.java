package ir.guardianapp.guardian_v2.database;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JSONManager {

    private static JSONArray drivingJSONArray = new JSONArray();

    public static JSONObject createDrivingJSONObject(double sleep, double time, double speed,
                                                     double withoutStop, double roadType, double traffic,
                                                     double weather, double nearCities, double vibration,
                                                     double acceleration, double month, double average,
                                                     Date regDate, double longitude, double latitude) throws JSONException, ParseException {
        JSONObject jsonObject = new JSONObject();
        //
        String inputPattern = "EEE MMM d HH:mm:ss zzz yyyy";
        String outputPattern = "yyyy-MM-dd hh:mm:ss";
        String registerDate = "";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);
        try {
            registerDate = outputFormat.format(inputFormat.parse(regDate.toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        jsonObject.put("sleep", sleep);
        jsonObject.put("time", time);
        jsonObject.put("speed", speed);
        jsonObject.put("trip_duration", withoutStop);
        jsonObject.put("road", roadType);
        jsonObject.put("traffic", traffic);
        jsonObject.put("weather", weather);
        jsonObject.put("radius", nearCities);
        jsonObject.put("vib", vibration);
        jsonObject.put("acc", acceleration);
        jsonObject.put("month", month);
        jsonObject.put("avg", average);
        jsonObject.put("reg", registerDate);
        jsonObject.put("long", longitude);
        jsonObject.put("li", latitude);
        return jsonObject;
    }

    public static void addJSONObject2JSONArray(JSONObject jsonObject) {
        drivingJSONArray.put(jsonObject);
    }

    public static void writeJSONArrIntoJSONFile(Context context) throws IOException {
        // Convert JsonObject to String Format
        String userString = drivingJSONArray.toString();
        // Define the File Path and its Name
        File file = new File(context.getFilesDir(),"driving.json");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(userString);
        bufferedWriter.close();
        Log.d("wrote in the file!", "done");
    }

    public static JSONArray readJSONArrFromJSONFile(Context context) throws IOException, JSONException {
        File file = new File(context.getFilesDir(), "driving.json");
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null){
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        // This response will have Json Format String
        String response = stringBuilder.toString();
        return new JSONArray(response);
    }

    public static boolean fileDoesExist(Context context) {
        File f = new File(context.getFilesDir(), "driving.json");
        if(f.exists() && !f.isDirectory()) {
            return true;
        }
        return false;
    }

    public static boolean deleteFile(Context context) {
        File f = new File(context.getFilesDir(), "driving.json");
        return f.delete();
    }

    public static JSONArray getDrivingJSONArray() {
        return drivingJSONArray;
    }

    public static void setDrivingJSONArray(JSONArray drivingJSONArray) {
        JSONManager.drivingJSONArray = drivingJSONArray;
    }

    public static void clearDrivingJSONArray() {
        for(int i=0; i<JSONManager.drivingJSONArray.length() ;i++) {
            JSONManager.drivingJSONArray.remove(i);
        }
    }
}
