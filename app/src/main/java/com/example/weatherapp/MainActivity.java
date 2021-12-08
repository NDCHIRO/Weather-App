package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView textView;
    public void getWeather(View view)
    {
        DownloadJson downloadJson = new DownloadJson();
        try {
            //to handle spaces in the city names like salt lake city (its already handled by the web site but to be more safe)
            String encodedCityName = URLEncoder.encode(editText.getText().toString(),"UTF-8");
            downloadJson.execute("https://api.openweathermap.org/data/2.5/weather?q="
                    +encodedCityName+"&appid=adbd0db9b15072e2031da40e24c58868").get();

            // to hide the android keyboard after hitting the button
            InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"could not find the weather :\"(",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    public class DownloadJson extends AsyncTask<String , Void , String>
    {

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1)
                {
                    char current = (char) data;
                    result += current;
                    data=reader.read();
                }
                return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try
            {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("weather content",weatherInfo);
                JSONArray arr = new JSONArray(weatherInfo);
                String main="";
                String description = "";
                for (int i=0;i<arr.length() ; i++)
                {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    Log.i("main",jsonPart.getString("main"));
                    Log.i("description",jsonPart.getString("description"));
                    main=jsonPart.getString("main");
                    description=jsonPart.getString("description");
                }
                if(main.equals("") && description.equals(""))
                    Toast.makeText(getApplicationContext(),"could not find the weather :(",Toast.LENGTH_SHORT).show();
                else
                    textView.setText(main+": "+description+"\r\n");


            }
            catch (Exception e)
            {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), "could not find the weather :\"(" ,Toast.LENGTH_SHORT );
                        toast.show();
                    }
                });
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText= findViewById(R.id.editText);
        textView = findViewById(R.id.textView);

    }
}