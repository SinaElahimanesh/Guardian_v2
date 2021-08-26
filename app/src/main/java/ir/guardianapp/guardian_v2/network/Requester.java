package ir.guardianapp.guardian_v2.network;

import org.json.JSONArray;

import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Requester {

    private static Requester requester;
    private final static String URI_PREFIX = "https://guardianapp-api.ir/";

    private Requester(){}

    public static Requester getInstance(){
        if(requester==null) requester = new Requester();
        return requester;
    }

    public Response RequestRegisterUser(String username, String password, String phoneNum){
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(URI_PREFIX + "Register/" + username + "/" + password + "/" + phoneNum)
                .newBuilder();
        String url = urlBuilder.build().toString();
        final Request request = new Request.Builder().url(url).build();
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response RequestLoginUser(String phoneNum, String password){
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(URI_PREFIX + "Login/" + phoneNum + "/" + password)
                .newBuilder();
        String url = urlBuilder.build().toString();
        final Request request = new Request.Builder().url(url).build();
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response RequestGetDrivingDetails(String username, String token){
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(URI_PREFIX + "drivingTotalAvg/" + username + "/" + token)
                .newBuilder();
        String url = urlBuilder.build().toString();
        final Request request = new Request.Builder().url(url).build();
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response RequestGetRecentTrips(String username, String token, int numberOfRecentTrips){
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(URI_PREFIX + "fetchRecentTrips/" + username + "/" + token + "/" + numberOfRecentTrips)
                .newBuilder();
        String url = urlBuilder.build().toString();
        final Request request = new Request.Builder().url(url).build();
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response RequestGetLoginCredentials(String token, String version){
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(URI_PREFIX + "LoginCredentials/" + token + "/" + version)
                .newBuilder();
        String url = urlBuilder.build().toString();
        final Request request = new Request.Builder().url(url).build();
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response RequestLogoutUser(String username, String token){
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(URI_PREFIX + "Logout/" + username + "/" + token)
                .newBuilder();
        String url = urlBuilder.build().toString();
        final Request request = new Request.Builder().url(url).build();
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response RequestEditUserProfile(String oldUsername, String oldPassword, String oldPhoneNum,
                                           String token, String newUsername, String newPassword, String newPhoneNum){
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(URI_PREFIX + "editProfile/" + oldUsername + "/" + oldPassword +"/"
                + oldPhoneNum +"/" + token + "/" + newUsername + "/" + newPassword + "/" + newPhoneNum)
                .newBuilder();
        String url = urlBuilder.build().toString();
        final Request request = new Request.Builder().url(url).build();
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response RequestPostDrivingDetails(String username, String token, JSONArray drivingJSONArr, int numberOfItems){
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, String.valueOf(drivingJSONArr));
        HttpUrl.Builder urlBuilder = HttpUrl.parse(URI_PREFIX + "driverMultiInfoInsertion/" + username + "/" + token + "/" + numberOfItems)
                .newBuilder();
        String url = urlBuilder.build().toString();
        final Request request = new Request.Builder().url(url).post(body).build();
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response RequestPostATrip(String username, String token,
                                     String sourceName, double sourceLongitude, double sourceLatitude,
                                     String destName, double destLongitude, double destLatitude,
                                     double duration, String startTime, String endTime, double average, double distance,
                                     double realDistance, double realLatitude, double realLongitude, String realEndTime){
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(URI_PREFIX + "PostTripInformation/" + username + "/" + token
                + "/" + sourceName + "/" + sourceLongitude + "/" + sourceLatitude
                + "/" + destName + "/" + destLongitude + "/" + destLatitude
                + "/" + ((int)duration) + "/" + startTime + "/" + endTime + "/" + average + "/" + distance
        + "/" + realDistance  + "/" + realLatitude  + "/" + realLongitude  + "/" + realEndTime)
                .newBuilder();
        String url = urlBuilder.build().toString();
        final Request request = new Request.Builder().url(url).build();
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
