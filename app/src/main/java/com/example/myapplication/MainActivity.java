package com.example.myapplication;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private EditText userField;
    private Button mainButton;
    private TextView out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userField = findViewById(R.id.user_field);
        mainButton = findViewById(R.id.main_button);
        out = findViewById(R.id.weather);

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userField.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.no_input, Toast.LENGTH_LONG).show();
                else {
                    String city = userField.getText().toString().trim();
                    String key = "bd8081d3aeee5fa25b2a93d0f49ac95a";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";
                    new GetData().execute(url);
                }
            }
        });
    }

    private class GetData extends AsyncTask<String, String, String > {

        protected void onPreExecute() {
            super.onPreExecute();
            out.setText("Жди бля.");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject JSONObj = new JSONObject(result);
                StringBuilder ghuwgh = new StringBuilder("");
                ghuwgh.append(JSONObj.getString("name") + "\n");
                ghuwgh.append(JSONObj.getJSONArray("weather").getJSONObject(0).getString("description") + "\n");
                ghuwgh.append("Температура: " + (int)(JSONObj.getJSONObject("main").getDouble("temp")) + "C°\n");
                ghuwgh.append("Давление: " + JSONObj.getJSONObject("main").getInt("pressure") + " мм.рт.с.\n");
                ghuwgh.append("Скорость ветра " + JSONObj.getJSONObject("wind").getInt("speed") + " м.с\n");
                out.setText(ghuwgh);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}