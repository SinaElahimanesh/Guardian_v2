package ir.guardianapp.guardian_v2.DrivingStatus.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ir.guardianapp.guardian_v2.DrivingStatus.WeatherType;


public class Weather {

    private static final String KEY = "91ff80ce4b10edc0f4ebf7391d2edfb2";  //api key

    private float temperature;
    private WeatherType weatherType;
    private String description;
    private float windSpeed;
    private String Id;

    private Weather(Float temperature, String weatherType, String description, Float windSpeed, String Id) {
        this.temperature = temperature;
        this.weatherType = WeatherType.valueOf(weatherType);
        this.description = description;
        this.windSpeed = windSpeed;
        this.Id = Id;
    }

    public static Weather getWeather(double lon, double lat) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&units=metric&appid=" + KEY).openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        JSONObject response = null;
        try {
            response = new JSONObject(content.toString());
            JSONObject weather = response.getJSONArray("weather").getJSONObject(0);
            JSONObject wind = response.getJSONObject("wind");
            Float windSpeed = Float.valueOf(wind.get("speed").toString());
            Float temperature = Float.valueOf(response.getJSONObject("main").get("temp").toString());
            String main = weather.get("main").toString().replace("\"", "");
            String description = weather.get("description").toString().replace("\"", "");
            String icon = weather.get("icon").toString().replace("\"", "");
            System.out.println(response.toString());
            return new Weather(temperature, main, description, windSpeed, icon);
        } catch (JSONException e) {
            return null;
        }
    }

    public static Weather getCurrentLocationWeather(Context context) throws IOException {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location == null){
            System.out.println("weather invalid");
           return getWeather(58,30);
        }
        return getWeather(location.getLongitude(),location.getLatitude());
    }


    public float getTemperature() {
        return temperature;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public String getDescription() {
        return description;
    }

    public WeatherType getWeatherType() {
        return weatherType;
    }

    public String getWeatherTypePersian(){
        if(weatherType.equals(WeatherType.Thunderstorm)) return "رعد و برق";
        if(weatherType.equals(WeatherType.Drizzle)) return "باران ملایم";
        if(weatherType.equals(WeatherType.Rain)) return "باران";
        if(weatherType.equals(WeatherType.Snow)) return "برف";
        if(weatherType.equals(WeatherType.Clear)) return "صاف";
        if(weatherType.equals(WeatherType.Clouds)) return "ابری";
        if(weatherType.equals(WeatherType.Mist)) return "مه";
        if(weatherType.equals(WeatherType.Smoke)) return "دود";
        if(weatherType.equals(WeatherType.Haze)) return "مه";
        if(weatherType.equals(WeatherType.Dust)) return "شن";
        if(weatherType.equals(WeatherType.Fog)) return "مه";
        if(weatherType.equals(WeatherType.Sand)) return "شنی";
        if(weatherType.equals(WeatherType.Ash)) return "خاکستر";
        if(weatherType.equals(WeatherType.Tornado)) return "طوفانی";
        if(weatherType.equals(WeatherType.Squall)) return "باد شدید";
        return "?";
    }

    public String getImageUrl() {
        return "http://openweathermap.org/img/wn/" + Id + "@2x.png";
    }

    //Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);
}