package com.example.openweatherapp;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

    public class Weather {

        private static final String OPEN_WEATHER_MAP_URL =
                "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric";

        private static final String OPEN_WEATHER_MAP_L_AND_L = "http://api.openweathermap.org/data/2.5/forecast?lat=35&lon=139&cnt=7";
        //You Will need to StringSplice the Lat and Long for the above String.

        private static final String OPEN_WEATHER_MAP_API = "9c77026c7e309404f3f70034e652ab04";

        public interface AsyncResponse {

            void processFinish(String output1, String output2, String output3, String output4, String output5, String output6);
        }


        public static class placeIdTask extends AsyncTask<String, Void, JSONObject> {

            public AsyncResponse delegate = null;
            //Call back interface

            public placeIdTask(AsyncResponse asyncResponse) {
                delegate = asyncResponse;
                //Assigning call back interface through constructor
            }

            @Override
            protected JSONObject doInBackground(String... params) {

                JSONObject jsonWeather = null;
                try {
                    jsonWeather = getWeatherJSON(params[0], params[1]);
                } catch (Exception e) {
                    Log.d("Error", "Cannot process JSON results", e);
                }


                return jsonWeather;
            }

            @Override
            protected void onPostExecute(JSONObject json) {
                try {
                    if(json != null){
                        JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                        System.out.println("The weather is..:" + details);
                        JSONObject main = json.getJSONObject("main");
                        System.out.println("The main is..:" + main); //This contains min and max temp for the day
                        DateFormat df = DateFormat.getDateTimeInstance();


                        String city = json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country");
                        String description = details.getString("description").toUpperCase(Locale.US);

                        //String Magic to Convert Temperature
                        Double originalTemp = main.getDouble("temp");
                        originalTemp = (originalTemp.doubleValue()*9/5)+32;

                        String temperature = String.format("%.2f", originalTemp)+ "°F";

                        String humidity = main.getString("humidity") + "%";
                        String pressure = main.getString("pressure") + " hPa";
                        String updatedOn = df.format(new Date(json.getLong("dt")*1000));

                        delegate.processFinish(city, description, temperature, humidity, pressure, updatedOn);

                    }
                } catch (JSONException e) {

                }



            }
        }

        public static JSONObject getWeatherJSON(String lat, String lon){
            try {
                URL url = new URL(String.format(OPEN_WEATHER_MAP_URL, lat, lon));
                HttpURLConnection connection =
                        (HttpURLConnection)url.openConnection();

                connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                StringBuffer json = new StringBuffer(1024);
                String tmp="";
                while((tmp=reader.readLine())!=null)
                    json.append(tmp).append("\n");
                reader.close();

                JSONObject data = new JSONObject(json.toString());

                // This value will be 404 if the request was not successful
                if(data.getInt("cod") != 200){
                    return null;
                }

                return data;
            }catch(Exception e){
                return null;
            }
        }




    }

