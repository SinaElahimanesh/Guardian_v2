package ir.guardianapp.guardian_v2.network;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ir.guardianapp.guardian_v2.OSRM.OSRMParser;
import ir.guardianapp.guardian_v2.R;
import ir.guardianapp.guardian_v2.extras.AnimationHandler;
import ir.guardianapp.guardian_v2.map.RoutingInformation;
import ir.guardianapp.guardian_v2.map.search.ListViewAdapter;
import ir.guardianapp.guardian_v2.map.search.SearchPlaces;
import ir.guardianapp.guardian_v2.models.ThisTripData;
import ir.guardianapp.guardian_v2.models.Trip;
import ir.guardianapp.guardian_v2.models.User;
import okhttp3.Response;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class MapThreadGenerator {

    public static Polyline backgroundPolyline, pathPolyline;
    private static GoogleMap mMap;
    private static String routeString;
    private static ArrayList<LatLng> pathArr = new ArrayList<>();

    public static Thread getBestRoute(Activity activity, GoogleMap mMap, double source_latitude, double source_longitude, double dest_latitude, double dest_longitude, Handler handler){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = MapRequester.getInstance().RequestBestRoute(source_latitude, source_longitude, dest_latitude, dest_longitude);
                try {
                    if(response==null) {
                        return;
                    }
                    routeString = response.body().string();

                    JSONObject coinJson = new JSONObject(routeString);
                    JSONObject jsonObject = (JSONObject) coinJson.getJSONArray("routes").get(0);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("geometry");
                    double distance = jsonObject.getDouble("distance");
                    double duration = jsonObject.getDouble("duration");
                    JSONArray array = jsonObject1.getJSONArray("coordinates");
                    ArrayList<LatLng> path = new ArrayList<>();
                    for(int i=0; i<array.length(); i++) {
                        String [] map = array.getString(i).split(",");
                        double lng = Double.parseDouble(map[0].substring(1));
                        double lat = Double.parseDouble(map[1].substring(0, map[1].length()-1));
                        path.add(new LatLng(lat, lng));
                    }

                    PolylineOptions backgroundOptions = new PolylineOptions().width(15).geodesic(true);

                    int padding = 280; // padding around start and end marker
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    for (int z = 0; z < path.size(); z++) {
                        LatLng point = path.get(z);
                        builder.include(path.get(z));
                        backgroundOptions.add(point);
                    }

                    MapThreadGenerator.mMap = mMap;
                    MapThreadGenerator.pathArr = path;

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            backgroundOptions.width(15);
                            backgroundOptions.color(Color.rgb(241, 129, 27));
                            backgroundOptions.startCap(new SquareCap());
                            backgroundOptions.endCap(new SquareCap());
                            backgroundOptions.jointType(ROUND);
                            backgroundPolyline = mMap.addPolyline(backgroundOptions);

                            PolylineOptions pathOptions = new PolylineOptions();
                            pathOptions.width(15);
                            pathOptions.color(Color.rgb(254, 168, 28));
                            pathOptions.startCap(new SquareCap());
                            pathOptions.endCap(new SquareCap());
                            pathOptions.jointType(ROUND);
                            pathPolyline = mMap.addPolyline(pathOptions);

                            animatePolyLine(path, backgroundPolyline);

                            LatLngBounds bounds = builder.build();
                            mMap.setPadding(140, 450, 140, 480);
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                            mMap.animateCamera(cu);

                            TextView arrivalTextView = activity.findViewById(R.id.arrivalTextView);
                            arrivalTextView.setText(RoutingInformation.getInstance().calculateArrivalTime(duration));
                            TextView durationTextView = activity.findViewById(R.id.durationTextView);
                            durationTextView.setText(RoutingInformation.getInstance().calculateDuration(duration));
                            TextView distanceTextView = activity.findViewById(R.id.distanceTextView);
                            distanceTextView.setText(RoutingInformation.getInstance().calculateDistance(distance));

                            FrameLayout routingInformationBox = activity.findViewById(R.id.routingInfoLayout);
                            routingInformationBox.setVisibility(View.VISIBLE);
                            AnimationHandler.slideUp(routingInformationBox);
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    });

                    ThisTripData thisTripData = ThisTripData.getInstance();
                    thisTripData.setUsername(User.getInstance().getUsername());
                    thisTripData.setToken(User.getInstance().getToken());
                    thisTripData.setSourceLatitude(source_latitude);
                    thisTripData.setSourceLongitude(source_longitude);
                    thisTripData.setDestLatitude(dest_latitude);
                    thisTripData.setDestLongitude(dest_longitude);

                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.getTime().getHours();
                    int minute = calendar.getTime().getMinutes();

                    int durationInMin = (int)(duration/60);

                    int hourPlus = durationInMin/60;
                    int minPlus = durationInMin%60;
                    hour += hourPlus;
                    minute += minPlus;
                    if(minute>=60){
                        hour ++;
                        minute -= 60;
                    }

                    if(hour >= 24) {
                        hour -= 24;
                    }

                    Date today = new Date();
                    System.out.println(Calendar.getInstance().get(Calendar.YEAR));
                    calendar.set(Calendar.getInstance().get(Calendar.YEAR), today.getMonth(), today.getDay(), hour, minute, 0);
                    Date endTime = calendar.getTime();

                    thisTripData.setStartDate(new Date());
                    thisTripData.setEndDate(endTime);
                    thisTripData.setDistance(distance);
                    thisTripData.setDuration(duration);
                    thisTripData.setAverage(100);
                    thisTripData.setEnable(true);

//                    OSRMParser.setJSONString(routeString);
//                    System.out.println(routeString);
                    Message message = new Message();
                    message.what = MessageResult.SUCCESSFUL;
                    handler.sendMessage(message);

                } catch (JSONException | IOException e) {
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        });
    }

    public static Thread getBestRoute2(double source_latitude, double source_longitude, double dest_latitude, double dest_longitude, Handler handler){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = MapRequester.getInstance().RequestBestRoute2(source_latitude, source_longitude, dest_latitude, dest_longitude);
                try {
                    if(response==null) {
                        Message message = new Message();
                        message.what = MessageResult.FAILED;
                        handler.sendMessage(message);
                        return;
                    }
                    routeString = response.body().string();

                    OSRMParser.setJSONString(routeString);
                    System.out.println(routeString);
                    Message message = new Message();
                    message.what = MessageResult.SUCCESSFUL;
                    handler.sendMessage(message);

                } catch (IOException e) {
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        });
    }

    public static Thread getBestTreePointsRoute(double source_latitude, double source_longitude, double mid_latitude, double mid_longitude, double dest_latitude, double dest_longitude, Handler handler){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = MapRequester.getInstance().RequestBestThreePointsRoute(source_latitude, source_longitude, mid_latitude, mid_longitude, dest_latitude, dest_longitude);
                try {
                    if(response==null) {
                        Message message = new Message();
                        message.what = MessageResult.FAILED;
                        handler.sendMessage(message);
                        return;
                    }
                    routeString = response.body().string();

                    OSRMParser.setJSONString(routeString);
                    System.out.println(routeString);
                    Message message = new Message();
                    message.what = MessageResult.SUCCESSFUL;
                    handler.sendMessage(message);

                } catch (IOException e) {
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        });
    }


    private static void animatePolyLine(ArrayList<LatLng> listLatLng, Polyline blackPolyLine) {

        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(2500);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {

                List<LatLng> latLngList = blackPolyLine.getPoints();
                int initialPointSize = latLngList.size();
                int animatedValue = (int) animator.getAnimatedValue();
                int newPoints = (animatedValue * listLatLng.size()) / 100;

                if (initialPointSize <= newPoints ) {
                    latLngList.addAll(listLatLng.subList(initialPointSize, newPoints));
                    blackPolyLine.setPoints(latLngList);
                }
            }
        });

        animator.addListener(polyLineAnimationListener);
        animator.start();
    }

    static Animator.AnimatorListener polyLineAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {

            List<LatLng> blackLatLng = backgroundPolyline.getPoints();
            List<LatLng> greyLatLng = pathPolyline.getPoints();

            greyLatLng.clear();
            greyLatLng.addAll(blackLatLng);
            blackLatLng.clear();

            backgroundPolyline.setPoints(blackLatLng);
            pathPolyline.setPoints(greyLatLng);

            backgroundPolyline.setZIndex(100);

            animator.start();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {
            animator.cancel();

        }
    };

    //
    public static Thread getPlaces(Activity activity, String places, ListViewAdapter adapter, Handler handler) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = MapRequester.getInstance().RequestPlaces(places);
                try {
                    if (response == null) {
                        return;
                    }
                    String placesString = response.body().string();

                    JSONArray jsonArray = new JSONArray(placesString);
                    ArrayList<SearchPlaces> placesArray = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject placeObject = jsonArray.getJSONObject(i);
                        String displayName = placeObject.getString("display_name");
                        JSONObject address = placeObject.getJSONObject("address");
                        String country = "Iran";
                        if (address.has("country")) {
                            country = address.getString("country");
                        }
                        double longitude = placeObject.getDouble("lon");
                        double latitude = placeObject.getDouble("lat");

                        String city = "";//address.getString("city");
                        String neighbourhood = "neighbourhood";//address.getString("neighbourhood");

                        // use keys() iterator, you don't need to know what keys are there/missing
                        Iterator<String> iter = address.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            String lightObject = address.getString(key);
                            System.out.println("key: " + key + ", OBJECT " + lightObject);
                            neighbourhood = lightObject;
                            break;

                        }

                        if (country.contains("ایران") || country.contains("Iran") || country.contains("IR")) {
                            placesArray.add(new SearchPlaces(neighbourhood, city, displayName, latitude, longitude));
                        }
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setArraylist(placesArray);
                            adapter.filter();

                            ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });

                    Message message = new Message();
                    message.what = MessageResult.SUCCESSFUL;
                    message.obj = placesArray;
                    handler.sendMessage(message);

                } catch (JSONException | IOException e) {
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        });
    }

    public static Thread getPlaceFromCoordinates(double latitude, double longitude, Handler handler) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = MapRequester.getInstance().RequestReversePlaces(latitude, longitude);
                try {
                    if (response == null) {
                        return;
                    }
                    String placesString = response.body().string();

                    JSONObject jsonObject = new JSONObject(placesString);
                    JSONObject address = jsonObject.getJSONObject("address");
                    StringBuilder result = new StringBuilder("");
                    if(address.has("city")) {
                        result.append(address.getString("city")).append("-");
                    }
                    if(address.has("road")) {
                        result.append(address.getString("road")).append("-");
                    } else if(address.has("neighbourhood")) {
                        result.append(address.getString("neighbourhood")).append("-");
                    }
                    if(result.equals("")) {
                        result = new StringBuilder("ایران").append("-");
                    }
                    if(result.charAt(result.length()-1) == '-')
                        result = new StringBuilder(result.substring(0, result.length()-1));

                    Message message = new Message();
                    message.what = MessageResult.SUCCESSFUL;
                    message.obj = result;
                    handler.sendMessage(message);

                } catch (JSONException | IOException e) {
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        });
    }

}
