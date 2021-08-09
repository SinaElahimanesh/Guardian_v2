package ir.guardianapp.guardian_v2.database;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ir.guardianapp.guardian_v2.MainActivity;

public class SharedPreferencesManager {

    private static SharedPreferences sharedPref;

    public static void initializeSharedPreferences(Activity activity) {
        sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public static void writeToSharedPreferences(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String readFromSharedPreferences(String key) {
        return sharedPref.getString(key, "");
    }

    public static void deleteFromSharedPreferences(String key) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.apply();
    }

    public static String getToken(Activity activity) {
        return SharedPreferencesManager.readFromSharedPreferences("TOKEN");
    }

    public static boolean hasToken(Activity activity) {
        return sharedPref.contains("TOKEN");
    }
}
