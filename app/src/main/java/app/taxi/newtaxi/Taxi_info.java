package app.taxi.newtaxi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Taxi_info extends AppCompatActivity {

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
        setContentView(R.layout.taxi_driver_call_info);

        driver = findViewById(R.id.driverText);
        taxinumber = findViewById(R.id.taxinumberText);
        phonenumber = findViewById(R.id.phonenumber_Text);

        Intent intent = getIntent();
        userID = intent.getExtras().getString("userid");

        Query query12 = databaseReference.child("post").orderByChild("userID").equalTo(userID);
        query12.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                Data_Post dataPost = nodeDataSnapshot.getValue(Data_Post.class);
                driver.setText(dataPost.getDriver());
                taxinumber.setText(dataPost.getTaxinumber());
                phonenumber.setText(dataPost.getPhonenumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}


