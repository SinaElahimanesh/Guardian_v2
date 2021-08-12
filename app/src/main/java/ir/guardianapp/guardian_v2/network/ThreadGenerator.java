package ir.guardianapp.guardian_v2.network;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ir.guardianapp.guardian_v2.MainActivity;
import ir.guardianapp.guardian_v2.database.SharedPreferencesManager;
import ir.guardianapp.guardian_v2.models.Driving;
import ir.guardianapp.guardian_v2.models.Trip;
import ir.guardianapp.guardian_v2.models.User;
import ir.guardianapp.guardian_v2.trips.TripsAdapter;
import okhttp3.Response;

public class ThreadGenerator {

    public static boolean isJSONArrayValid(String test) {
        try {
            new JSONArray(test);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public static Thread registerUser(String username, String password, String phoneNum, Handler handler){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = Requester.getInstance().RequestRegisterUser(username, password, phoneNum);
                try {
                    String registerResponse = response.body().string();
                    Log.d("REGISTER", registerResponse);
                    String registerMessage = registerResponse.toString();
                    String registerToken = "";
                    if(ThreadGenerator.isJSONArrayValid(registerResponse)) {
                        JSONArray registerArray = new JSONArray(registerResponse);
                        registerMessage = registerArray.getString(0);
                        registerToken = registerArray.getString(1);
                    }
                    Message message = new Message();
                    if(registerMessage.equalsIgnoreCase("successful")) {
                        message.what = MessageResult.SUCCESSFUL;
                        SharedPreferencesManager.writeToSharedPreferences("TOKEN", registerToken);
                        User user = User.getInstance();
                        user.setUsername(username);
                        user.setPassword(password);
                        user.setPhoneNum(phoneNum);
                        user.setToken(registerToken);
                    } else if(registerMessage.equalsIgnoreCase("this username has already exists.")) {
                        message.what = MessageResult.USERNAME_IS_NOT_UNIQUE;
                    } else if(registerMessage.equalsIgnoreCase("this phonenumber has already exists.")) {
                        message.what = MessageResult.PHONE_IS_NOT_UNIQUE;
                    } else {
                        message.what = MessageResult.FAILED;
                    }
                    handler.sendMessage(message);

                } catch (IOException e) {
                    Log.e("internet","response time out");
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                } catch (NullPointerException e){
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                }
            }
        });
    }

    public static Thread loginUser(String phoneNum, String password, Handler handler){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = Requester.getInstance().RequestLoginUser(phoneNum, password);
                try {
                    String loginResponse = response.body().string();
                    String loginMessage = loginResponse.toString();
                    String loginToken = "";
                    String loginUsername = "";
                    if(ThreadGenerator.isJSONArrayValid(loginResponse)) {
                        JSONArray registerArray = new JSONArray(loginResponse);
                        loginMessage = registerArray.getString(0);
                        loginToken = registerArray.getString(1);
                        loginUsername = registerArray.getString(2);
                    }
                    Message message = new Message();
                    if(loginMessage.equalsIgnoreCase("successful")) {
                        message.what = MessageResult.SUCCESSFUL;
                        SharedPreferencesManager.writeToSharedPreferences("TOKEN", loginToken);
                        User user = User.getInstance();
                        user.setPassword(password);
                        user.setPhoneNum(phoneNum);
                        user.setUsername(loginUsername);
                        user.setToken(loginToken);
                    } else if(loginMessage.equalsIgnoreCase("there is no user with this phonenumber.")) {
                        message.what = MessageResult.THIS_PHONE_NOT_REGISTERED;
                    } else if(loginMessage.equalsIgnoreCase("phonenumber and password don't match")) {
                        message.what = MessageResult.PHONE_AND_PASSWORD_DOES_NOT_MATCH;
                    } else {
                        message.what = MessageResult.FAILED;
                    }
                    handler.sendMessage(message);

                } catch (IOException e) {
                    Log.e("internet","response time out");
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                } catch (NullPointerException e){
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        });
    }

    public static Thread getDrivingDetail(String username, String token, Handler handler){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = Requester.getInstance().RequestGetDrivingDetails(username, token);
                try {
                    String drivingResponse = response.body().string();
                    JSONArray arr = new JSONArray(drivingResponse);
                    Driving driving = Driving.getInstance();
                    if(!arr.getJSONObject(0).isNull("avg(sleep_amount)")) {
                        driving.setSleep(arr.getJSONObject(0).getDouble("avg(sleep_amount)"));
                    }
                    if(!arr.getJSONObject(1).isNull("avg(time_data)")) {
                        driving.setTime(arr.getJSONObject(1).getDouble("avg(time_data)"));
                    }
                    if(!arr.getJSONObject(2).isNull("avg(car_speed)")) {
                        driving.setSpeed(arr.getJSONObject(2).getDouble("avg(car_speed)"));
                    }
                    if(!arr.getJSONObject(3).isNull("avg(trip_duration)")) {
                        driving.setTripDuration(arr.getJSONObject(3).getDouble("avg(trip_duration)"));
                    }
                    if(!arr.getJSONObject(4).isNull("avg(road_type)")) {
                        driving.setRoadType(arr.getJSONObject(4).getDouble("avg(road_type)"));
                    }
                    if(!arr.getJSONObject(5).isNull("avg(traffic)")) {
                        driving.setTraffic(arr.getJSONObject(5).getDouble("avg(traffic)"));
                    }
                    if(!arr.getJSONObject(6).isNull("avg(weather)")) {
                        driving.setWeather(arr.getJSONObject(6).getDouble("avg(weather)"));
                    }
                    if(!arr.getJSONObject(7).isNull("avg(radius_30km)")) {
                        driving.setRadius30KM(arr.getJSONObject(7).getDouble("avg(radius_30km)"));
                    }
                    if(!arr.getJSONObject(8).isNull("avg(car_vibration)")) {
                        driving.setVibration(arr.getJSONObject(8).getDouble("avg(car_vibration)"));
                    }
                    if(!arr.getJSONObject(9).isNull("avg(accelerometer)")) {
                        driving.setAcceleration(arr.getJSONObject(9).getDouble("avg(accelerometer)"));
                    }
                    if(!arr.getJSONObject(10).isNull("avg(month_data)")) {
                        driving.setMonth(arr.getJSONObject(10).getDouble("avg(month_data)"));
                    }
                    if(!arr.getJSONObject(11).isNull("avg(average)")) {
                        driving.setAverage(arr.getJSONObject(11).getDouble("avg(average)"));
                    }
                    Message message = new Message();
                    message.what = MessageResult.SUCCESSFUL;
                    handler.sendMessage(message);

                } catch (IOException e) {
                    Log.e("internet","response time out");
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                } catch (NullPointerException e){
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        });
    }

    public static Thread getRecentTrips(String username, String token, int numberOfTrips, Handler handler){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = Requester.getInstance().RequestGetRecentTrips(username, token, numberOfTrips);
                try {
                    String tripsResponse = response.body().string();
                    Message message = new Message();
                    if(tripsResponse.equalsIgnoreCase("Authentication failed!!")) {
                        message.what = MessageResult.AUTHENTICATION_FAILED;
                    } if(isJSONArrayValid(tripsResponse)) {
                        message.what = MessageResult.SUCCESSFUL;
                        JSONArray arr = new JSONArray(tripsResponse);
                        ArrayList<Trip> trips = new ArrayList<>();
                        //
                        String inputPattern = "yyyy-MM-dd hh:mm:ss";
                        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);

                        for(int i = 0; i < arr.length() ;i++){
                            JSONObject tripJSON = arr.getJSONObject(i);
                            Trip trip = new Trip(tripJSON.getString("source_name"),
                                    tripJSON.getString("destination_name"),
                                    inputFormat.parse(tripJSON.getString("start_time")),
//                                    tripJSON.getString("start_time"),
                                    inputFormat.parse(tripJSON.getString("arrival_time")),
//                                    tripJSON.getString("arrival_time"),
                                    tripJSON.getDouble("distance"),
                                    tripJSON.getDouble("avg_driving"));

                            trips.add(trip);
                        }
                        Trip.addAllTrips(trips);
                        Log.d("DATEE", trips.get(0).getStartDate().toString());
                    } else {
                        message.what = MessageResult.FAILED;
                    }
                    handler.sendMessage(message);

                } catch (IOException e) {
                    Log.e("internet","response time out");
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                } catch (NullPointerException e){
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static Thread userCredentials(String token, String version, Handler handler){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = Requester.getInstance().RequestGetLoginCredentials(token, version);

                try {
                    String credentialsResponse = response.body().string();

                    System.out.println(credentialsResponse);
                    JSONObject credentials = new JSONObject(credentialsResponse);
                    String loginState = credentials.getString("LoginState");
                    String username = credentials.getString("Username");
                    String password = credentials.getString("Password");
                    String phoneNum = credentials.getString("PhoneNum");
                    String versionChecker = credentials.getString("VersionChecker");
                    String updateLink = credentials.getString("Link");

                    Message message = new Message();
                    if(versionChecker.equalsIgnoreCase("Reject")) {
                        message.what = MessageResult.VERSION_IS_LESS_THAN_MINIMUM;
                        MainActivity.updateLink = updateLink;
                    } if(loginState.equalsIgnoreCase("LoggedOut")) {
                        message.what = MessageResult.LOGGED_OUT;
                    } if(loginState.equalsIgnoreCase("LoggedIn")) {
                        // done successfully
                        message.what = MessageResult.LOGGED_IN;
                        User user = User.getInstance();
                        user.setUsername(username);
                        user.setPassword(password);
                        user.setPhoneNum(phoneNum);
                    } else {
                        message.what = MessageResult.FAILED;
                    }
                    handler.sendMessage(message);

                } catch (IOException e) {
                    Log.e("internet","response time out");
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                } catch (NullPointerException e){
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        });
    }

    public static Thread logoutUser(String username, String token, Handler handler){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = Requester.getInstance().RequestLogoutUser(username, token);
                try {
                    String logoutResponse = response.body().string();
                    Message message = new Message();
                    if(logoutResponse.equalsIgnoreCase("successful")) {
                        message.what = MessageResult.SUCCESSFUL;
                        SharedPreferencesManager.deleteFromSharedPreferences("TOKEN");
                        User.getInstance().setToken("");
                    } else if(logoutResponse.equalsIgnoreCase("Authentication failed!!")) {
                        message.what = MessageResult.AUTHENTICATION_FAILED;
                    } else {
                        message.what = MessageResult.FAILED;
                    }
                    handler.sendMessage(message);

                } catch (IOException e) {
                    Log.e("internet","response time out");
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                } catch (NullPointerException e){
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                }
            }
        });
    }

    public static Thread editProfile(String oldUsername, String oldPassword, String oldPhoneNum,
                                     String token, String newUsername, String newPassword, String newPhoneNum, Handler handler){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = Requester.getInstance().RequestEditUserProfile(oldUsername, oldPassword, oldPhoneNum, token, newUsername, newPassword, newPhoneNum);
                try {
                    String editResponse = response.body().string();
                    Message message = new Message();
                    if(editResponse.equalsIgnoreCase("successful")) {
                        message.what = MessageResult.SUCCESSFUL;
                        User user = User.getInstance();
                        user.setUsername(newUsername);
                        user.setPassword(newPassword);
                        user.setPhoneNum(newPhoneNum);
                    } else if(editResponse.equalsIgnoreCase("Authentication failed!!")) {
                        message.what = MessageResult.AUTHENTICATION_FAILED;
                    } else if(editResponse.equalsIgnoreCase("this username has already exists.")) {
                        message.what = MessageResult.USERNAME_IS_NOT_UNIQUE;
                    } else if(editResponse.equalsIgnoreCase("this phone number has already exists.")) {
                        message.what = MessageResult.PHONE_IS_NOT_UNIQUE;
                    } else {
                        message.what = MessageResult.FAILED;
                    }
                    handler.sendMessage(message);

                } catch (IOException e) {
                    Log.e("internet","response time out");
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                } catch (NullPointerException e){
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                }
            }
        });
    }

    public static Thread postDrivingDetails(String username, String token, JSONArray drivingJSONArr, Handler handler){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = Requester.getInstance().RequestPostDrivingDetails(username, token, drivingJSONArr, drivingJSONArr.length());
                Log.d("drivingsend", response.toString());
                try {
                    String logoutResponse = response.body().string();
                    Log.d("drivingsend", logoutResponse);
                    Message message = new Message();
                    if(logoutResponse.equalsIgnoreCase("successful")) {
                        message.what = MessageResult.SUCCESSFUL;
                    } else if(logoutResponse.equalsIgnoreCase("Authentication failed!!")) {
                        message.what = MessageResult.AUTHENTICATION_FAILED;
                    } else {
                        message.what = MessageResult.FAILED;
                    }
                    handler.sendMessage(message);

                } catch (IOException e) {
                    Log.e("internet","response time out");
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                } catch (NullPointerException e){
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                }
            }
        });
    }

    public static Thread postATripInformation(String username, String token,
                                              String sourceName, double sourceLongitude, double sourceLatitude,
                                              String destName, double destLongitude, double destLatitude,
                                              double duration, String startTime, String endTime, double average, double distance
                                            , Handler handler){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                // 2021-07-26 10:00:00
//                DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
//                String startDate;
//                String endDate;
//                //
//                Date today = new Date();
//                today.setHours(0);
//                startDate = today.toString();
//                endDate = today.toString();
//
//                String inputPattern = "EEE MMM d HH:mm:ss zzz yyyy";
//                String outputPattern = "yyyy-MM-dd hh:mm:ss";
//
//                SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
//                SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);
//                Date date = null;
//                String str = null;
//                try {
//                    date = inputFormat.parse(startTime.toString());
//                    startDate = outputFormat.format(date);
//
//                    date = inputFormat.parse(endTime.toString());
//                    endDate = outputFormat.format(date);
////                    startDate = format.parse(startTime.toString()).toString();
////                    endDate = format.parse(endTime.toString()).toString();
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
                Response response = Requester.getInstance().RequestPostATrip(username, token,
                        sourceName, sourceLongitude, sourceLatitude,
                        destName, destLongitude, destLatitude,
                        duration, startTime, endTime, average, distance);
                try {
                    String logoutResponse = response.body().string();
                    Log.d("tripresponsee",  logoutResponse);
                    Message message = new Message();
                    if(logoutResponse.equalsIgnoreCase("successful")) {
                        message.what = MessageResult.SUCCESSFUL;
                    } else if(logoutResponse.equalsIgnoreCase("Authentication failed!!")) {
                        message.what = MessageResult.AUTHENTICATION_FAILED;
                    } else {
                        message.what = MessageResult.FAILED;
                    }
                    handler.sendMessage(message);

                } catch (IOException e) {
                    Log.e("internet","response time out");
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                } catch (NullPointerException e){
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                }
            }
        });
    }

}
