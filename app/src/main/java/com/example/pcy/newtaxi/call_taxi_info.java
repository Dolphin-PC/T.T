package com.example.pcy.newtaxi;

import android.content.Intent;
import android.service.autofill.UserData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class call_taxi_info extends AppCompatActivity {

    private TextView driver;
    private TextView taxinumber;
    private TextView phonenumber;
    private String userID;
    private DatabaseReference mPostReference;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_taxi_info);

        driver = findViewById(R.id.driverText);
        taxinumber = findViewById(R.id.taxinumberText);
        phonenumber = findViewById(R.id.phonenumberText);

        Intent intent = getIntent();
        userID = intent.getExtras().getString("userid");

        Query query12 = databaseReference.child("post").orderByChild("userID").equalTo(userID);
        query12.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                PostData postData = nodeDataSnapshot.getValue(PostData.class);
                driver.setText(postData.getDriver());
                taxinumber.setText(postData.getTaxinumber());
                phonenumber.setText(postData.getPhonenumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}


