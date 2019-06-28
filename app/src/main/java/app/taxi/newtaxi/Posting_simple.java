package app.taxi.newtaxi;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Posting_simple extends AppCompatActivity {
    private DatabaseReference mDatabase;
    TextView PayText,DistanceText, TimeText;
    Button LeftButton,RightButton;
    ImageView PersonImage,CreateButton;
    int person = 1,PAY;
    ProgressDialog pd;
    String START,ARRIVE,DISTANCE,PRICE,TIME,URL,INDEX;

    long now = System.currentTimeMillis ();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
    String Time = sdfNow.format(date);
    int Hour = Integer.parseInt(Time.split(":")[0]);
    int Minute = Integer.parseInt(Time.split(":")[1]);
    TimePickerDialog dialog;

    void init(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();
        START = positionDATA.getString("출발지","");
        ARRIVE = positionDATA.getString("도착지","");
        INDEX = positionDATA.getString("INDEX","");
        URL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
        URL += START;
        URL += "&destinations=" + ARRIVE;
        URL += "&mode=transit&key=AIzaSyBDB-w0MZ3KAbm82L7q5iJ3rLfeNB0Z6Zs";
        if(START.length()>=1 && ARRIVE.length()>=1) {
            new JsonTask().execute(URL);
        }


        PayText = findViewById(R.id.PayText);
        DistanceText = findViewById(R.id.DistanceText);
        TimeText = findViewById(R.id.TimeText);
        LeftButton = findViewById(R.id.LeftButton);
        RightButton = findViewById(R.id.RightButton);
        CreateButton = (ImageView)findViewById(R.id.CreateButton);
        PersonImage = findViewById(R.id.PersonImage);

        dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(minute == 0) {
                    if(hourOfDay < Hour) {
                        TIME = Hour + " : " + Minute;
                        Toast.makeText(getApplicationContext(),"현재 이전의 시간은 예약이 안됩니다.",Toast.LENGTH_SHORT).show();
                    }
                    else
                        TIME = hourOfDay + "시";
                }
                else {
                    if(hourOfDay < Hour) {
                        hourOfDay = Hour;
                        Toast.makeText(getApplicationContext(),"현재 이전의 시간은 예약이 안됩니다.",Toast.LENGTH_SHORT).show();
                    }
                    if(hourOfDay<=Hour && minute < Minute) {
                        minute = Minute;
                        Toast.makeText(getApplicationContext(),"현재 이전의 시간은 예약이 안됩니다.",Toast.LENGTH_SHORT).show();
                    }
                    TIME = hourOfDay + " : " + minute;
                }
                TimeText.setText(TIME);
            }
        },Hour,Minute,false);

    }

    void click(){
        LeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (person){
                    case 1 : PersonImage.setImageDrawable(getResources().getDrawable(R.drawable.person3));
                    person=3; break;
                    case 2 : PersonImage.setImageDrawable(getResources().getDrawable(R.drawable.person1));
                    person--; break;
                    case 3 : PersonImage.setImageDrawable(getResources().getDrawable(R.drawable.person2));
                    person--; break;
                }
            }
        });
        RightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (person){
                    case 1 : PersonImage.setImageDrawable(getResources().getDrawable(R.drawable.person2));
                    person++; break;
                    case 2 : PersonImage.setImageDrawable(getResources().getDrawable(R.drawable.person3));
                    person++; break;
                    case 3 : PersonImage.setImageDrawable(getResources().getDrawable(R.drawable.person1));
                    person=1; break;
                }
            }
        });
        CreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int price = Integer.valueOf(PRICE);
                if( (price/person) % 100 != 0)
                    PAY = price/person + (100 - (price / person % 100));

                SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
                SharedPreferences.Editor editor = positionDATA.edit();

                editor.putString("PERSON",Integer.toString(1));
                editor.putString("PRICE",PRICE+"");
                editor.putString("TIME",TIME);
                editor.putString("INDEX",INDEX);
                editor.putString("DISTANCE",DISTANCE);
                editor.apply();

                String userID = positionDATA.getString("USERNAME","");
                String URL = positionDATA.getString("PROFILE","");
                String Start_latitude = positionDATA.getString("출발","").split(",")[0];
                String Start_longitude = positionDATA.getString("출발","").split(",")[1];
                String Arrive_latitude = positionDATA.getString("도착","").split(",")[0];
                String Arrive_longitude = positionDATA.getString("도착","").split(",")[1];

                Data_Post dataPost = new Data_Post(userID //게시자의 이름
                        ,PRICE //총가격
                        ,"" //게시글 제목
                        ,START,Start_latitude,Start_longitude,ARRIVE,Arrive_latitude,Arrive_longitude //출발지/도착지
                        ,1  //person
                        ,person //최대 인원
                        ,INDEX  //일련번호 인덱스
                        ,DISTANCE //거리
                        ,PAY //각자 부담할 가격
                        ,TimeText.getText().toString() //예약 시
                        ,"","","");
                Data_Members data_members = new Data_Members(userID,String.valueOf(INDEX),URL,"남",String.valueOf(INDEX),false);
                mDatabase.child("post").push().setValue(dataPost);
                mDatabase.child("post-members").push().setValue(data_members);

                Intent intent = new Intent(getApplicationContext(),My_taxi_simple.class);
                startActivity(intent);
                finish();
            }
        });
        TimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_simple);

        init();
        click();
    }
    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(Posting_simple.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                java.net.URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";
                String distance_text = null;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                try {
                    JSONObject jsonObject = new JSONObject(buffer.toString());
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("rows"));
                    jsonObject = jsonArray.getJSONObject(0);
                    jsonArray = new JSONArray(jsonObject.getString("elements"));
                    jsonObject = jsonArray.getJSONObject(0);

                    distance_text = jsonObject.getJSONObject("distance").getString("text");

                }catch (JSONException e){
                    e.printStackTrace();
                }
                return distance_text;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            double meter;
            int pay=3300; //TODO : 지역마다 택시요금 다름
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            DISTANCE=result;
            if(Double.valueOf(result.split(" ")[0]) >= 2.0){
                meter = Double.valueOf(result.split(" ")[0]) - 2.0; //기본거리 제외
                meter *= 1000;                                             //km -> m 단위로
                pay += (int)meter / 133 * 100;                           //133m 당 100원
            }
            PRICE = String.valueOf(pay);
            PayText.setText(PRICE+"원");
            DistanceText.setText(DISTANCE);
            TimeText.setText(Hour + " : " + Minute);
        }
    }
}
