package app.taxi.newtaxi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.HashMap;

public class Posting_simple extends AppCompatActivity {
    main_simple main = new main_simple();
    Join join = new Join();

    private DatabaseReference mDatabase,rDatabase;
    TextView PayText, DistanceText, TimeText;
    Button LeftButton, RightButton;
    ImageView PersonImage, CreateButton;
    int person = 2, PAY;
    ProgressDialog pd;
    String START, ARRIVE, DISTANCE, PRICE, TIME, URL, INDEX;
    int service_pay;

    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
    SimpleDateFormat dayNow = new SimpleDateFormat("yyyy/MM/dd");
    String Time = sdfNow.format(date);
    String day = dayNow.format(date);
    int Hour = Integer.parseInt(Time.split(":")[0]);
    int Minute = Integer.parseInt(Time.split(":")[1]);
    TimePickerDialog dialog;

    AlertDialog.Builder alertDialog;

    void init() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        rDatabase = FirebaseDatabase.getInstance("https://taxitogether.firebaseio.com/").getReference();

        SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();
        START = positionDATA.getString("출발지", "");
        ARRIVE = positionDATA.getString("도착지", "");
        INDEX = positionDATA.getString("ID", "");
        URL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
        URL += START;
        URL += "&destinations=" + ARRIVE;
        URL += "&mode=transit&key=AIzaSyBDB-w0MZ3KAbm82L7q5iJ3rLfeNB0Z6Zs";
        if (START.length() >= 1 && ARRIVE.length() >= 1) {
            new JsonTask().execute(URL);
        }


        PayText = findViewById(R.id.PayText);
        DistanceText = findViewById(R.id.DistanceText);
        TimeText = findViewById(R.id.TimeText);
        LeftButton = findViewById(R.id.LeftButton);
        RightButton = findViewById(R.id.RightButton);
        CreateButton = (ImageView) findViewById(R.id.CreateButton);
        PersonImage = findViewById(R.id.PersonImage);

        dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setTitle("이용 시간은\n동승자간의 택시 이용의 편의\n보여주기 위함입니다.");
                if (minute == 0) {
                    if (hourOfDay < Hour) {
                        TIME = Hour + " : " + Minute;
                        Toast.makeText(getApplicationContext(), "현재 이전의 시간은 예약이 안됩니다.", Toast.LENGTH_SHORT).show();
                    } else
                        TIME = hourOfDay + "시";
                } else {
                    if (hourOfDay < Hour) {
                        hourOfDay = Hour;
                        Toast.makeText(getApplicationContext(), "현재 이전의 시간은 예약이 안됩니다.", Toast.LENGTH_SHORT).show();
                    }
                    if (hourOfDay <= Hour && minute < Minute) {
                        minute = Minute;
                        Toast.makeText(getApplicationContext(), "현재 이전의 시간은 예약이 안됩니다.", Toast.LENGTH_SHORT).show();
                    }
                    TIME = hourOfDay + " : " + minute;
                }
                TimeText.setText(TIME);
            }
        }, Hour, Minute, false);

    }

    void click() {
        LeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (person) {
                    case 2:
                        PersonImage.setImageDrawable(getResources().getDrawable(R.drawable.person3));
                        person = 4;
                        PayText.setText("500 P");
                        break;
                    case 3:
                        PersonImage.setImageDrawable(getResources().getDrawable(R.drawable.person1));
                        person--;
                        PayText.setText("1,000 P");
                        break;
                    case 4:
                        PersonImage.setImageDrawable(getResources().getDrawable(R.drawable.person2));
                        person--;
                        PayText.setText("700 P");
                        break;
                }
            }
        });
        RightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (person) {
                    case 2:
                        PersonImage.setImageDrawable(getResources().getDrawable(R.drawable.person2));
                        person++;
                        PayText.setText("700 P");
                        break;
                    case 3:
                        PersonImage.setImageDrawable(getResources().getDrawable(R.drawable.person3));
                        person++;
                        PayText.setText("500 P");
                        break;
                    case 4:
                        PersonImage.setImageDrawable(getResources().getDrawable(R.drawable.person1));
                        person = 2;
                        PayText.setText("1,000 P");
                        break;
                }
            }
        });
        alertDialog = new AlertDialog.Builder(this);

        if (person == 2)
            service_pay = 1000;
        else if (person == 3)
            service_pay = 700;
        else if (person == 4)
            service_pay = 500;

        CreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.setTitle("노선 생성").setMessage("입력하신 정보로 노선을 생성합니다.\n동승 인원 : " + person + " 명\n(서비스 이용료 : " + PayText.getText().toString()  + ")\n" +
                        "예약 시간 : " + TimeText.getText().toString())
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int price = Integer.valueOf(PRICE);
                                if ((price / person) % 100 != 0)
                                    PAY = price / person + (100 - (price / person % 100));
                                else
                                    PAY = price / person;

                                SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
                                SharedPreferences.Editor editor = positionDATA.edit();

                                editor.putString("PERSON", Integer.toString(1));
                                editor.putString("PRICE", PRICE + "");
                                editor.putString("TIME", TIME);
                                editor.putString("INDEX", INDEX);
                                editor.putString("DISTANCE", DISTANCE);
                                editor.putInt("PERSON",person);
                                editor.apply();

                                String userID = positionDATA.getString("USERNAME", "");
                                String URL = positionDATA.getString("PROFILE", "");
                                String Start_latitude = positionDATA.getString("출발", "").split(",")[0];
                                String Start_longitude = positionDATA.getString("출발", "").split(",")[1];
                                String Arrive_latitude = positionDATA.getString("도착", "").split(",")[0];
                                String Arrive_longitude = positionDATA.getString("도착", "").split(",")[1];

                                Data_Post dataPost = new Data_Post(userID //게시자의 이름
                                        , PRICE //총가격
                                        , "" //게시글 제목
                                        , START, Start_latitude, Start_longitude, ARRIVE, Arrive_latitude, Arrive_longitude //출발지/도착지
                                        , 1  //person
                                        , person //최대 인원
                                        , INDEX  //일련번호 인덱스
                                        , DISTANCE //거리
                                        , service_pay
                                        , TimeText.getText().toString() //예약 시
                                        , "", "", "");
                                Data_Members data_members = new Data_Members(userID, String.valueOf(INDEX), URL, "남", String.valueOf(INDEX), true, false);
                                HashMap<String, Object> map = new HashMap<>();
                                map.put(INDEX, dataPost);
                                mDatabase.child("post").updateChildren(map);
                                rDatabase.child("post").child(day).push().setValue(new Data_Members(userID, String.valueOf(INDEX), URL, "남", String.valueOf(INDEX), true, false));
                                map.clear();
                                map.put(INDEX, data_members);
                                mDatabase.child("post-members").updateChildren(map);
                                rDatabase.child("post-members").child(day).push().setValue(new Data_Members(userID, String.valueOf(INDEX), URL, "남", String.valueOf(INDEX), true, false));

                                Intent intent = new Intent(getApplicationContext(), My_taxi_simple.class);
                                startActivity(intent);

                                main.finish();  //main act. finish
                                join.finish();  //join act. finish
                                finish();       //this act. finish
                            }
                        }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
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

                } catch (JSONException e) {
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
            int pay = 3300; //TODO : 지역마다 택시요금 다름
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            DISTANCE = result;
            if (Double.valueOf(result.split(" ")[0]) >= 2.0) {
                meter = Double.valueOf(result.split(" ")[0]) - 2.0; //기본거리 제외
                meter *= 1000;                                             //km -> m 단위로
                pay += (int) meter / 133 * 100;                           //133m 당 100원
            }
            PRICE = String.valueOf(pay);
//            PayText.setText(PRICE + "원");
            DistanceText.setText(DISTANCE);
            TimeText.setText(Hour + " : " + Minute);
        }
    }
}
