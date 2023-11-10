package com.example.weatherapp3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static String apiKey = "a877574c492b791013dfa3297a62c25f";
    private static String tempPostFix = "\u2109";
    private static String tempMinPreFix = " Low: ";
    private static String tempMaxPreFix = " High: ";
    private static String defaultTxt = "--";
    Button b;
    EditText editText;
    TextView quote;
    TextView mainTemp;
    ImageView mainImage;
    TextView mainTime;

    ImageView image1;
    ImageView image2;
    ImageView image3;
    ImageView image4;

    TextView high1;
    TextView high2;
    TextView high3;
    TextView high4;

    TextView low1;
    TextView low2;
    TextView low3;
    TextView low4;

    TextView time1;
    TextView time2;
    TextView time3;
    TextView time4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b=findViewById(R.id.button);
        editText=findViewById(R.id.editText);
        quote=findViewById(R.id.quote);
        mainTemp=findViewById(R.id.mainTemp);
        mainImage=findViewById(R.id.mainImage);
        mainTime=findViewById(R.id.mainTime);
        image1=findViewById(R.id.image1);
        image2=findViewById(R.id.image2);
        image3=findViewById(R.id.image3);
        image4=findViewById(R.id.image4);
        high1=findViewById(R.id.high1);
        high2=findViewById(R.id.high2);
        high3=findViewById(R.id.high3);
        high4=findViewById(R.id.high4);
        low1=findViewById(R.id.low1);
        low2=findViewById(R.id.low2);
        low3=findViewById(R.id.low3);
        low4=findViewById(R.id.low4);
        time1=findViewById(R.id.time1);
        time2=findViewById(R.id.time2);
        time3=findViewById(R.id.time3);
        time4=findViewById(R.id.time4);
       b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zipcode = editText.getText().toString();
                AsyncThread task= new AsyncThread();
                task.execute(zipcode);
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    class AsyncThread extends AsyncTask<String,Void,String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... strings) {
            String zip =strings[0];
            // https://api.openweathermap.org/data/2.5/forecast?zip=08852,us&units=imperial&appid=a877574c492b791013dfa3297a62c25f
            String openWeatherURL = "https://api.openweathermap.org/data/2.5/forecast?zip=" +zip + ",us" + "&units=imperial" + "&appid=" + apiKey;
            Log.d("TAG","openWeatherURL:"+openWeatherURL);
            String result = "";

            try {
                URL url = new URL(openWeatherURL);
                URLConnection httpURLConnection = (URLConnection) url.openConnection();
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                result = bufferedReader.readLine();
                while((line = bufferedReader.readLine()) != null){
                    result += line;
                }

                bufferedReader.close();
                String weatherResult = result;
                Log.d("TAG",""+weatherResult.length());
            } catch (Exception ex) {
                //resetAllFields();
                ex.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            super.onPostExecute(jsonResponse);
            try {
                //Log.d("TAG","jsonResponse##############################:"+jsonResponse);
                if(jsonResponse != null && jsonResponse.length()>0){
                    JSONObject jsonObj = new JSONObject(jsonResponse);
                    JSONArray list = jsonObj.getJSONArray("list");
                    processMainRow((JSONObject) list.get(0));
                    for (int i=1;i<=4;i++) {
                        processRow(i, (JSONObject) list.get(i));
                    }
                } else {
                    resetAllFields();
                }
        } catch(Exception e) {
                e.printStackTrace();
            }
        }

        protected void processMainRow(JSONObject listItem) {
            try {
                String dt = formatDate(listItem.getString("dt_txt"), "hh:mm a");
                //MM/dd/yyyy HH:mm:ss a
                JSONObject itemMain = listItem.getJSONObject("main");
                String temp = itemMain.getString("temp") + tempPostFix;
                String temp_min = tempMinPreFix + itemMain.getString("temp_min") + tempPostFix;
                String temp_max = tempMaxPreFix + itemMain.getString("temp_max") + tempPostFix;
                JSONArray itemWeatherParent = listItem.getJSONArray("weather");
                JSONObject itemWeather = (JSONObject) itemWeatherParent.get(0);

                int itemId = itemWeather.getInt("id");
                String itemWeatherType = itemWeather.getString("main");
                String itemDescription = itemWeather.getString("description");
                String itemIcon =  itemWeather.getString("icon");
                Log.d("TAG","itemId:"+itemId);
                Log.d("TAG","itemWeatherType"+itemWeatherType);
                Log.d("TAG","itemDescription"+itemDescription);
                Log.d("TAG","itemIcon"+itemIcon);
                mainTemp.setText(temp);
                setWeatherImage(itemIcon,mainImage);
                mainTime.setText(itemDescription + "\n" + dt );
                quote.setText(generateQOTD(itemIcon));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        protected void processRow(int rowNum, JSONObject listItem) {
            try {
                String dt = formatDate(listItem.getString("dt_txt"), "ha");
                JSONObject itemMain = listItem.getJSONObject("main");
                String temp = itemMain.getString("temp") + tempPostFix;
                String temp_min = tempMinPreFix + itemMain.getString("temp_min") + tempPostFix;
                String temp_max = tempMaxPreFix + itemMain.getString("temp_max") + tempPostFix;
                JSONArray itemWeatherParent = listItem.getJSONArray("weather");
                JSONObject itemWeather = (JSONObject) itemWeatherParent.get(0);
                int itemId = itemWeather.getInt("id");
                String itemWeatherType = itemWeather.getString("main");
                String itemDescription = itemWeather.getString("description");
                String itemIcon =  itemWeather.getString("icon");
                switch (rowNum) {
                    case 1:
                        high1.setText(temp_max);
                        low1.setText(temp_min);
                        time1.setText(dt);
                        setWeatherImage(itemIcon,image1);
                        //image1.setImageResource(R.drawable.tempimage);
                        break;
                    case 2:
                        high2.setText(temp_max);
                        low2.setText(temp_min);
                        time2.setText(dt);
                        setWeatherImage(itemIcon,image2);
                        //image2.setImageResource(R.drawable.tempimage);
                        break;
                    case 3:
                        high3.setText(temp_max);
                        low3.setText(temp_min);
                        time3.setText(dt);
                        setWeatherImage(itemIcon,image3);
                        //image3.setImageResource(R.drawable.tempimage);
                        break;
                    case 4:
                        high4.setText(temp_max);
                        low4.setText(temp_min);
                        time4.setText(dt);
                        setWeatherImage(itemIcon,image4);
                        //image4.setImageResource(R.drawable.tempimage);
                        break;
                    default:
                        break;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }



        }

        private String formatDate(String dt_txt, String reqTS) throws Exception{

            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date utcDate = utcFormat.parse(dt_txt);
            //SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
            SimpleDateFormat outputFormat = new SimpleDateFormat(reqTS);
            outputFormat.setTimeZone(TimeZone.getTimeZone("EST"));
            String estTime = outputFormat.format(utcDate);
            return estTime;
        }

        private void setWeatherImage(String iconCode, ImageView imageToSet){
            switch (iconCode) {
                case "01d":
                    //clear sky day
                    imageToSet.setImageResource(R.drawable.oned);
                    break;
                case "01n":
                    // clear sky night
                    imageToSet.setImageResource(R.drawable.onen);
                    break;
                case "02d":
                    // few clouds day
                    imageToSet.setImageResource(R.drawable.twod);
                    break;
                case "02n":
                    // few clouds night
                    imageToSet.setImageResource(R.drawable.twon);
                    break;
                case "03d":
                    // scattered clouds day
                    imageToSet.setImageResource(R.drawable.threed);
                    break;
                case "03n":
                    // scattered clouds night
                    imageToSet.setImageResource(R.drawable.threen);
                    break;
                case "04d":
                    //broken clouds day
                    imageToSet.setImageResource(R.drawable.fourd);
                    break;
                case "04n":
                    // broken clouds night
                    imageToSet.setImageResource(R.drawable.fourn);
                    break;
                case "09d":
                    // shower rainy day
                    imageToSet.setImageResource(R.drawable.nined);
                    break;
                case "09n":
                    //shower rainy night
                    imageToSet.setImageResource(R.drawable.ninen);
                    break;
                case "10d":
                    // rain day
                    imageToSet.setImageResource(R.drawable.tend);
                    break;
                case "10n":
                    // rain night
                    imageToSet.setImageResource(R.drawable.tenn);
                    break;
                case "11d":
                    // thunderstorm day
                    imageToSet.setImageResource(R.drawable.elevend);
                    break;
                case "11n":
                    // thunderstorm night
                    imageToSet.setImageResource(R.drawable.elevenn);
                    break;
                case "13d":
                    // snow day
                    imageToSet.setImageResource(R.drawable.thirteend);
                    break;
                case "13n":
                    // snow night
                    imageToSet.setImageResource(R.drawable.thirteenn);
                    break;
                case "50d":
                    // mist day
                    imageToSet.setImageResource(R.drawable.fiftyd);
                    break;
                case "50n":
                    // mist night
                    imageToSet.setImageResource(R.drawable.fiftyn);
                    break;
                default:
                    imageToSet.setImageResource(R.drawable.tempimage);
                    break;
            }
        }

        private String generateQOTD(String iconCode){

            String quote = "Everyday is a good day";

            switch (iconCode) {
                case "01d":
                case "01n":
                    // clear sky night
                    //clear sky day
                    quote = "The skies are clear, and I can prove it mathematically.";
                    break;
                case "02d":
                case "02n":
                    // few clouds night
                    // few clouds day
                    quote = "A few clouds are just a “few clouds”";
                    break;
                case "03d":
                case "03n":
                    // scattered clouds night
                    // scattered clouds day
                    quote = "I learned one thing today, there’s scattered clouds";
                    break;
                case "04d":
                case "04n":
                    // broken clouds night
                    //broken clouds day
                    quote = "I’m just explaining why there’s overcast clouds.";
                    break;
                case "09d":
                case "09n":
                    //shower rainy night
                    // shower rainy day
                    quote = "Everybody’s going to die, and  it's showering rain";
                    break;
                case "10d":
                case "10n":
                    // rain night
                    // rain day
                    quote = "Everything either is or isn’t, and it is raining";
                    break;
                case "11d":
                case "11n":
                    // thunderstorm night
                    // thunderstorm day
                    quote = "Oh jeez it’s a thunderstorm";
                    break;
                case "13d":
                case "13n":
                    // snow night
                    // snow day
                    quote = "Wubba Lubba Dub Dub it's snowing!!";
                    break;
                case "50d":
                case "50n":
                    // mist night
                    // mist day
                    quote = "It’s misty right now, this reality sucks.";
                    break;
                default:
                    quote = "What a great day";
                    break;
            }
            return quote;

        }

        private void resetAllFields(){
            mainTemp.setText(defaultTxt);
            setWeatherImage("00",mainImage);
            mainTime.setText(defaultTxt);
            quote.setText("Enter a valid zipcode");
            // reset
            high1.setText(defaultTxt);
            low1.setText(defaultTxt);
            time1.setText(defaultTxt);
            setWeatherImage("00",image1);
            //image1.setImageResource(R.drawable.tempimage);
            high2.setText(defaultTxt);
            low2.setText(defaultTxt);
            time2.setText(defaultTxt);
            setWeatherImage("00",image2);
            //image2.setImageResource(R.drawable.tempimage);

            high3.setText(defaultTxt);
            low3.setText(defaultTxt);
            time3.setText(defaultTxt);
            setWeatherImage("00",image3);
            //image3.setImageResource(R.drawable.tempimage);

            high4.setText(defaultTxt);
            low4.setText(defaultTxt);
            time4.setText(defaultTxt);
            setWeatherImage("00",image4);
            //image4.setImageResource(R.drawable.tempimage);
        }
    } // end of inner class
}