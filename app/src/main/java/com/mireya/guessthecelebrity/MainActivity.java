package com.mireya.guessthecelebrity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> celebURL = new ArrayList<>();
    private ArrayList<String> celebNames = new ArrayList<>();
    private int choosenCeleb = 0;
    private String[] answet = new String[4];
    private int locationOfCorrexctAnswer = 0;
    private ImageView imageView;
    private Button btn0, btn1, btn2, btn3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);

        DownloadTask task = new DownloadTask();
        //String result = null;
        try {
            String result = task.execute("http://www.posh24.se/kandisar").get();
            Log.i("URL", result);
            String[] splitResult = result.split("<div class=\"listedArticles\">");

            Pattern pattern = Pattern.compile("img src=\"(.*?)\"");
            Matcher matcher = pattern.matcher(splitResult[0]);

            while (matcher.find()){
                celebURL.add(matcher.group(1));
            }
            pattern = Pattern.compile("alt=\"(.*?)\"");
            matcher = pattern.matcher(splitResult[0]);

            while (matcher.find()){
                celebNames.add(matcher.group(1));
            }
            newQuestion();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void  celebChosen(View view){
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrexctAnswer))){
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Worng! It was " + celebNames.get(choosenCeleb), Toast.LENGTH_SHORT).show();
        }
        newQuestion();
    }

    @SuppressLint("StaticFieldLeak")
    public class  ImageDowloader extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL  url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                return BitmapFactory.decodeStream(inputStream);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class DownloadTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            URL url;
            //HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while (data != -1){
                    char current = (char) data;
                    result.append(current);
                    data = reader.read();
                }
                return result.toString();
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    public void newQuestion(){
        try {
            Random random = new Random();
            choosenCeleb = random.nextInt(celebURL.size());
            ImageDowloader imageTask = new ImageDowloader();
            Bitmap celebImage = imageTask.execute(celebURL.get(choosenCeleb)).get();
            imageView.setImageBitmap(celebImage);
            locationOfCorrexctAnswer = random.nextInt(4);
            int incorrectAnswerLocation;
            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrexctAnswer) {
                    answet[i] = celebNames.get(choosenCeleb);
                } else {
                    incorrectAnswerLocation = random.nextInt(celebURL.size());
                    while (incorrectAnswerLocation == choosenCeleb) {
                        incorrectAnswerLocation = random.nextInt(celebURL.size());
                    }
                    answet[i] = celebNames.get(incorrectAnswerLocation);
                }
            }
            btn0.setText(answet[0]);
            btn1.setText(answet[1]);
            btn2.setText(answet[2]);
            btn3.setText(answet[3]);
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
