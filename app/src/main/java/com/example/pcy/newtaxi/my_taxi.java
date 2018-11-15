package com.example.pcy.newtaxi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class my_taxi extends AppCompatActivity{
    private DatabaseReference mDatabase;
    private TextView textView;
    private TextView titleText;
    private TextView startText;
    private TextView arriveText;
    private TextView personText;
    private TextView pointText;
    private EditText commentText;
    private ListView commentList;
    private Button commentButton;
    private Button taxiButton;
    private ArrayAdapter mAdapter;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private DatabaseReference mPostReference;
    private DatabaseReference mCommentsReference;
    private String userID,title,start,arrive;
    private int index,point,person;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_taxi);
        ConstraintLayout LAY4 = findViewById(R.id.LAY4);
        LAY4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);
            }
        });

        textView = findViewById(R.id.indexView);
        titleText = findViewById(R.id.titleText);
        startText = findViewById(R.id.startText);
        arriveText = findViewById(R.id.arriveText);
        personText = findViewById(R.id.personText);
        commentList = findViewById(R.id.commentListView);
        commentText = findViewById(R.id.commentText);
        pointText = findViewById(R.id.pointText);
        commentButton = findViewById(R.id.commentButon);
        taxiButton = findViewById(R.id.taxiButton);

        final Intent intent = getIntent();
        index = intent.getExtras().getInt("Index");
        userID = intent.getExtras().getString("userID");
        title = intent.getExtras().getString("title");
        start = intent.getExtras().getString("start");
        arrive = intent.getExtras().getString("arrive");
        point = intent.getExtras().getInt("point");
        person = intent.getExtras().getInt("person");
        textView.setText(String.valueOf(index) + "번 글");
        titleText.setText(title);
        startText.setText(start);
        arriveText.setText(arrive);
        pointText.setText("Point :"+point);
        personText.setText(String.valueOf(person)+"/4");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 , android.R.id.text1);
        commentList.setAdapter(mAdapter);
        mPostReference = mDatabase.child("post").equalTo(index).getRef();
        mCommentsReference = mDatabase.child("post-comments");
        Query query_post = mPostReference.orderByChild("index").equalTo(index);

        mCommentsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CommentData commentData = dataSnapshot.getValue(CommentData.class);
                if(commentData.getIndex() == index) {
                    mAdapter.add(userID + " : " + commentData.getComment());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddComment(userID,commentText.getText().toString(),index);
            }
        });
        if(person == 4){
            taxiButton.setVisibility(View.VISIBLE);
        }
        taxiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(),call_taxi_info.class);
                intent1.putExtra("point",pointText.getText().toString());
                intent1.putExtra("person",personText.getText().toString());
                intent1.putExtra("userID",userID);
                intent1.putExtra("title",title);
                intent1.putExtra("start",start);
                intent1.putExtra("arrive",arrive);
                intent1.putExtra("index",index);

                startActivity(intent1);
            }
        });
    }

    private void AddComment(String userID,String comment,int index) {
        CommentData commentData = new CommentData(userID,comment,index);
        mCommentsReference.push().setValue(commentData);
    }
}
