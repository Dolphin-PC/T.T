package app.taxi.newtaxi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class Selector extends AppCompatActivity {

    Button createButton;
    TextView startTEXT,arriveTEXT,distanceText,priceText;
    String START,ARRIVE,resultText,URL;
    int PRICE = 3300;
    double DISTANCE;
    ProgressDialog pd;
    void init(){
        startTEXT=findViewById(R.id.startTEXT);
        arriveTEXT=findViewById(R.id.arriveTEXT);
        distanceText = findViewById(R.id.distanceText);
        priceText = findViewById(R.id.priceText);
        createButton = findViewById(R.id.createButton);
    }

    void click(){
        startTEXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Map.class);
                intent.putExtra("POSITION","출발");
                startActivity(intent);
                finish();
            }
        });
        arriveTEXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Map.class);
                intent.putExtra("POSITION","도착");
                startActivity(intent);
                finish();
            }
        });
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Posting.class);
                intent.putExtra("START",startTEXT.getText().toString());
                intent.putExtra("ARRIVE",arriveTEXT.getText().toString());
                intent.putExtra("DISTANCE",distanceText.getText().toString());
                intent.putExtra("PRICE",priceText.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
        init();
        click();
        final SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();

       /* startTEXT.setText(positionDATA.getString("START",""));
        arriveTEXT.setText(positionDATA.getString("ARRIVE",""));*/

        startTEXT.setText(positionDATA.getString("출발지",""));
        arriveTEXT.setText(positionDATA.getString("도착지",""));

        URL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
        URL += startTEXT.getText().toString();
        URL += "&destinations=" + arriveTEXT.getText().toString();
        URL += "&mode=transit&key=AIzaSyBDB-w0MZ3KAbm82L7q5iJ3rLfeNB0Z6Zs";
        if(startTEXT.getText().toString().length()>=1 && arriveTEXT.getText().toString().length()>=1) {
            new JsonTask().execute(URL);
        }
    }
    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(Selector.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
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
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            distanceText.setText("예상 거리 : " + result);
            if(Double.valueOf(result.split(" ")[0]) >= 2.0){
                DISTANCE = Double.valueOf(result.split(" ")[0]) - 2.0; //기본거리 제외
                DISTANCE *= 1000;                                              //km -> m 단위로
                PRICE += (int)DISTANCE / 133 * 100;                               //133m 당 100원
            }
            priceText.setText("예상 금액 : " + String.valueOf(PRICE) +" 원");
        }
    }
}

