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
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

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
    private Button commentButton,callButton,payButton;
    private ArrayAdapter mAdapter;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    final private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private DatabaseReference mPostReference;
    private DatabaseReference mCommentsReference;
    private String userID,title,start,arrive;
    private int index,pay,person,point;

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
        commentButton = findViewById(R.id.commentButon);
        callButton = findViewById(R.id.callButton);
        payButton = findViewById(R.id.payButton);

        final Intent intent = getIntent();
        index = intent.getExtras().getInt("Index");
        userID = intent.getExtras().getString("userID");
        title = intent.getExtras().getString("title");
        start = intent.getExtras().getString("start");
        arrive = intent.getExtras().getString("arrive");
        pay = intent.getExtras().getInt("point");
        point = pay;
        person = intent.getExtras().getInt("person");
        final int perpoint = point / 4;
        textView.setText(String.valueOf(index) + "번 글");
        titleText.setText(title);
        startText.setText(start);
        arriveText.setText(arrive);
        personText.setText(String.valueOf(person)+"/4");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 , android.R.id.text1);
        commentList.setAdapter(mAdapter);
        mPostReference = mDatabase.child("post").equalTo(index).getRef();
        mCommentsReference = mDatabase.child("post-comments");
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
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = databaseReference.child("post").orderByChild("index").equalTo(index);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                        PostData postData = nodeDataSnapshot.getValue(PostData.class);
                        int point1 = postData.getPoint() - perpoint;
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("pay", point1);
                        databaseReference.child(path).updateChildren(result);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                Query query1 = databaseReference.child("user").orderByChild("username").equalTo(userID);
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
                        databaseReference.child(path).updateChildren(result);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                pay -= perpoint;
                payButton.setEnabled(false);
            }
        });
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pay != 0){
                    Toast.makeText(getApplicationContext(),"호출 비용이 모자랍니다.",Toast.LENGTH_SHORT).show();
                }else{
                    PostData postData = new PostData(userID, title, start, arrive, person, index, point,pay,"","","");
                    mPostReference.child("call-taxi").push().setValue(postData);

                }
            }
        });

    }

    private void AddComment(String userID,String comment,int index) {
        CommentData commentData = new CommentData(userID,comment,index);
        mCommentsReference.push().setValue(commentData);
    }
}
