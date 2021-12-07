package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView textView;
    public void getWeather(View view)
    {
        DownloadJson downloadJson = new DownloadJson();
        try {
            downloadJson.execute("https://api.openweathermap.org/data/2.5/weather?q="
                    +editText.getText()+"&appid=adbd0db9b15072e2031da40e24c58868").get();
        }
        catch (Exception e)
        {
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
                for (int i=0;i<arr.length() ; i++)
                {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    Log.i("main",jsonPart.getString("main"));
                    Log.i("description",jsonPart.getString("description"));
                    textView.setText(jsonPart.getString("main")+"\n"+jsonPart.getString("description"));
                }

            }
            catch (Exception e)
            {
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