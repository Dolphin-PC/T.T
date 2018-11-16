package com.example.pcy.newtaxi;

import android.content.Intent;
import android.service.autofill.UserData;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class call_taxi_info extends AppCompatActivity {

    TextView driverName;
    TextView taxiNumber;
    TextView phoneNumber;
    TextView total_pointText;
    TextView per_pointText;
    Button pay;
    Button call;
    int totalpoint,perpoint;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    final DatabaseReference reference = firebaseDatabase.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_taxi_info);
        driverName = findViewById(R.id.drivernameText);
        taxiNumber = findViewById(R.id.taxinumberText);
        phoneNumber = findViewById(R.id.phonenumberText);
        pay = findViewById(R.id.payButton);
        call = findViewById(R.id.callButton);
        total_pointText = findViewById(R.id.pointText);
        per_pointText = findViewById(R.id.perpointText);
        Intent intent = getIntent();
        String pointS = intent.getExtras().getString("point");
        String personS = intent.getExtras().getString("person");
        final String userID = intent.getExtras().getString("userID");
        final String title = intent.getExtras().getString("title");
        final String start = intent.getExtras().getString("start");
        final String arrive = intent.getExtras().getString("arrive");
        final int index = intent.getExtras().getInt("index");

        final int person = Integer.parseInt(personS.split("/")[0]);
        final int point = Integer.parseInt(pointS.split(":")[1]);

        totalpoint = point;
        perpoint = totalpoint / person;

        String p = String.valueOf(totalpoint);
        String p1 = String.valueOf(perpoint);

        total_pointText.setText("남은 금액: " + p + "원");
        per_pointText.setText("개인 부담금 : " + p1 + "원");

        Query query = reference.child("post").orderByChild("index").equalTo(index);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                PostData postData = nodeDataSnapshot.getValue(PostData.class);
                driverName.setText(postData.getDriver());
                phoneNumber.setText(postData.getPhonenumber());
                taxiNumber.setText(postData.getTaxinumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int point1 = 0;
                if (perpoint == 0) {
                    Toast.makeText(getApplicationContext(), "이미 부담하셨습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Query query = reference.child("post").orderByChild("index").equalTo(index);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                            PostData postData = nodeDataSnapshot.getValue(PostData.class);
                            int point1 = postData.getPoint() - perpoint;
                            String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                            String path = "/" + dataSnapshot.getKey() + "/" + key;
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("point", point1);
                            reference.child(path).updateChildren(result);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    Query query1 = reference.child("user").orderByChild("username").equalTo(userID);
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                            User userData = nodeDataSnapshot.getValue(User.class);
                            int point1 = userData.getPoint() - perpoint;
                            String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                            String path = "/" + dataSnapshot.getKey() + "/" + key;
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("point", point1);
                            reference.child(path).updateChildren(result);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    totalpoint -= perpoint;
                    total_pointText.setText("남은 금액 : " + totalpoint + "원");
                    per_pointText.setText("개인 부담금 : 0원");
                    perpoint=0;
                }
            }
        });
        /*call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalpoint == 0) {
                    PostData postData = new PostData(userID, title, start, arrive, person, index, point,"","","");
                    reference.child("call-taxi").push().setValue(postData);
                    call.setEnabled(false);
                } else {
                    Toast.makeText(getApplicationContext(), "호출 비용이 부족합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }
}


