package com.example.openweatherapp;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;
    double myLat, myLong;
    boolean locationObtained = false;

    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, updatedField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        System.out.println("Hi!");
                        if (location != null) {
                            // Logic to handle location object
                            System.out.println("We Got Location!!");
                            myLat = location.getLatitude();
                            myLong = location.getLongitude();
                            locationObtained = true;
                            System.out.println("Latitude is : " + myLat);
                            System.out.println("Longitude is : " + myLong);

                        }
                        else
                        {
                            //We dont have GPS. Prompt for a Zip Code.
                            System.out.println("Need a Zip Code");
                        }
                    }
                });


        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        cityField = (TextView)findViewById(R.id.city_field);
        updatedField = (TextView)findViewById(R.id.updated_field);
        detailsField = (TextView)findViewById(R.id.details_field);
        currentTemperatureField = (TextView)findViewById(R.id.current_temperature_field);
        humidity_field = (TextView)findViewById(R.id.humidity_field);
        pressure_field = (TextView)findViewById(R.id.pressure_field);


        Weather.placeIdTask asyncTask =new Weather.placeIdTask(new Weather.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn) {
                System.out.println("Inside ProcessFinish");
                cityField.setText(weather_city);
                updatedField.setText(weather_updatedOn);
                detailsField.setText(weather_description);
                currentTemperatureField.setText(weather_temperature);
                humidity_field.setText("Humidity: "+weather_humidity);
                pressure_field.setText("Pressure: "+weather_pressure);
            }
        });

//        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        double longitude = location.getLongitude();
//        double latitude = location.getLatitude();
//        System.out.println("Latitude is: " + latitude);
//        System.out.println("Longitude is: " + longitude);

        asyncTask.execute(String.valueOf(myLat), String.valueOf(myLong)); //  asyncTask.execute("Latitude", "Longitude")
        //asyncTask.execute(String.valueOf(latitude),String.valueOf(longitude)); //  asyncTask.execute("Latitude", "Longitude")




    }
}
