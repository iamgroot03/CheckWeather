package com.example.checkweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView minTempTV,maxTempTV,temperatureTV,cityNameTV,dayTV,dateTV,humidityTV,windSpeedTV,sunriseTV,sunsetTV,conditionTV,seaTV;
    ProgressBar loadingBar;
    ConstraintLayout homePage;

    EditText searchbarET;
//     847890ee01ce6cbaa7ef7fc4e52f36ec
//    https://api.openweathermap.org/data/2.5/weather?q=gujarat&appid=847890ee01ce6cbaa7ef7fc4e52f36ec

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temperatureTV=findViewById(R.id.idTVTemperature);
        minTempTV=findViewById(R.id.idTVMinTemp);
        maxTempTV=findViewById(R.id.idTVMaxTemp);
        cityNameTV=findViewById(R.id.idTVCityLabel);
        dayTV=findViewById(R.id.idTVDayName);
        dateTV=findViewById(R.id.idTVDate);
        searchbarET=findViewById(R.id.idEVSearchBar);
        humidityTV=findViewById(R.id.idTVHumidity);
        windSpeedTV=findViewById(R.id.idTVWind);
        sunriseTV=findViewById(R.id.idTVSunrise);
        sunsetTV=findViewById(R.id.idTVSunset);
        conditionTV=findViewById(R.id.idTVRain);
        seaTV=findViewById(R.id.idTVSea);
        loadingBar=findViewById(R.id.progressBar);
        homePage=findViewById(R.id.home_page);

        CheckInternate();
        weatherAppdata("gujarat");

        searchbarET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchCity=searchbarET.getText().toString();
                weatherAppdata(searchCity);
            }
        });



    }





    private void CheckInternate() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()) {
            // Handle no internet connection
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void weatherAppdata(String city) {

        String url=" https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=847890ee01ce6cbaa7ef7fc4e52f36ec";
        RequestQueue queue = Volley.newRequestQueue(this);

// Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LocalDateTime now = LocalDateTime.now();
                String dayName = now.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());

                Date currentDate = new Date();
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                String formattedDate = outputFormat.format(currentDate);

                Log.d("api","onResponse:"+response);
                loadingBar.setVisibility(View.GONE);
                try {
                    String cName=response.getString("name");
                    double temp=response.getJSONObject("main").getDouble("temp");
                    double minTemp=response.getJSONObject("main").getDouble("temp_min");
                    double maxTemp=response.getJSONObject("main").getDouble("temp_max");
                    JSONArray condition= response.getJSONArray("weather");
                    JSONObject conditionObject=condition.getJSONObject(0);

                    String con=conditionObject.getString("main");


                    String humidity= String.valueOf(response.getJSONObject("main").getInt("humidity"));
                    String speed=String.valueOf(response.getJSONObject("wind").getDouble("speed"));
                    String sunrise=String.valueOf(response.getJSONObject("sys").getLong("sunrise"));
                    String sunset=String.valueOf(response.getJSONObject("sys").getLong("sunset"));

                    cityNameTV.setText(cName);
                    temperatureTV.setText(String.format("%s°", kelvinToTemp(temp)));
                    minTempTV.setText(String.format("Max:%s°", kelvinToTemp(minTemp)));
                    maxTempTV.setText(String.format("Min:%s°", kelvinToTemp(maxTemp)));
                    dayTV.setText(dayName);
                    dateTV.setText(formattedDate);

                    humidityTV.setText(humidity);
                    windSpeedTV.setText(speed);
                    sunriseTV.setText(sunrise);
                    sunsetTV.setText(sunset);
                    homePage.setVisibility(View.VISIBLE);
                    conditionTV.setText(con);


//

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "error:"+e, Toast.LENGTH_SHORT).show();
                    Log.d("api","Exception:"+e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("api","onErrorResponse:"+error);

            }
        });
        queue.add(jsonObjectRequest);
    }

    public String kelvinToTemp(double kelvin){
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        double temp=kelvin-273.15;
        String formattedTemperature=decimalFormat.format(temp);
        return formattedTemperature;
    }
}