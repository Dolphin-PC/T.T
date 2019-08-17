package app.taxi.newtaxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Review extends AppCompatActivity {
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    Button PassButton;
    String INDEX, ID, POINT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        PassButton = findViewById(R.id.PassButton);

        SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
        INDEX = positionDATA.getString("INDEX", "");
        ID = positionDATA.getString("ID", "");
        POINT = positionDATA.getString("POINT", "");

        Log.e("INDEX", INDEX + "." + ID + "." + POINT);
        PassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QUIT_PROCESS_databaseDATA();
                Intent intent = new Intent(getApplicationContext(), main_simple.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void QUIT_PROCESS_databaseDATA() {
        /*final Query MEMBERSquery_1 = mDatabase.child("post-members").orderByChild("index").equalTo(INDEX);  //방장이 나갔을때, post-members전체 삭제*/
        /*final Query MESSAGEquery_1 = mDatabase.child("post-message").orderByChild("index").equalTo(INDEX); //방장이 나갔을때, post-message전체 삭제*/

        final Query POSTquery = mDatabase.child("post").orderByChild("id").equalTo(ID);
        final Query MESSAGEquery = mDatabase.child("post-message").orderByChild("id").equalTo(ID);    //참가인원이 나갔을 때,
        Query TAXI_query = mDatabase.child("taxi-call").orderByChild("id").equalTo(ID);


        POSTquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data_Post data_post = snapshot.getValue(Data_Post.class);
                    if (INDEX.equals(data_post.getIndex())) {
                        mDatabase.child("post").child(ID).removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });//방장일 때, 방 전체 파기(post)

        mDatabase.child("post-members").child(ID).removeValue();

        MESSAGEquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    mDatabase.child("post-message").child(snapshot1.getKey()).removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        TAXI_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data_Taxi data_taxi = snapshot.getValue(Data_Taxi.class);
                    HashMap<String, Object> map = new HashMap<>();
                    int p = Integer.valueOf(data_taxi.getPerson()) - 1;
                    map.put("person", p);
                    Log.e("person",p+"");
                    mDatabase.child("taxi-call").child(INDEX).updateChildren(map);
                    if (p == 0) { //0명이면, 콜 파기
                        mDatabase.child("taxi-call").child(INDEX).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
