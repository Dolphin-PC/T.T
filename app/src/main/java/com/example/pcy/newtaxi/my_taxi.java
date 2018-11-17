package com.example.pcy.newtaxi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
    private String userID,title,start,arrive,driver,taxinumber,phonenumber;
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
        title = intent.getExtras().getString("title");
        arrive = intent.getExtras().getString("arrive");
        start = intent.getExtras().getString("start");
        person = intent.getExtras().getInt("person");
        pay = point = intent.getExtras().getInt("point");
        userID = intent.getExtras().getString("userID");
        final int perpoint = point / 4;
        textView.setText(String.valueOf(index) + "번 글");
        titleText.setText(title);
        startText.setText(start);
        arriveText.setText(arrive);
        personText.setText(String.valueOf(person) + "/4");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        commentList.setAdapter(mAdapter);
        commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String call_taxi = (String)parent.getAdapter().getItem(position);
                if(call_taxi.equals("택시가 호출되었습니다. 클릭해서 확인")){
                    Intent intent1 = new Intent(getApplicationContext(),call_taxi_info.class);
                    intent1.putExtra("userid",userID);
                    startActivity(intent1);
                }
            }
        });
        mCommentsReference = mDatabase.child("post-comments");
        mCommentsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CommentData commentData = dataSnapshot.getValue(CommentData.class);
                if (commentData.getIndex() == index) {
                    mAdapter.add(commentData.getuserID() + " : " + commentData.getComment());
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
        mPostReference = mDatabase.child("post");
        mPostReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PostData postData = dataSnapshot.getValue(PostData.class);
                if (!postData.getDriver().equals("")) {
                    mAdapter.add("택시가 호출되었습니다. 클릭해서 확인");
                }
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
                AddComment(userID, commentText.getText().toString(), index);
            }
        });
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query5 = databaseReference.child("post").orderByChild("index").equalTo(index);
                query5.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                        PostData postData = nodeDataSnapshot.getValue(PostData.class);
                        pay = postData.getPay() - perpoint;
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("pay", pay);
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
                        int pay1;
                        if(userData.getPoint()-perpoint < 0){
                            Toast.makeText(getApplicationContext(),"포인트가 부족합니다.",Toast.LENGTH_LONG).show();
                            return;
                        }
                        else{
                            pay1 = userData.getPoint()-perpoint;
                        }
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("point", pay1);
                        databaseReference.child(path).updateChildren(result);

                        String str = userID + "님이 " + perpoint + "원을 지불하셨습니다.(" + pay + "원 남음)";
                        AddComment("system",str,index);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pay > 0){
                    Toast.makeText(getApplicationContext(),"호출 비용이 모자랍니다.",Toast.LENGTH_SHORT).show();
                }else{
                    PostData postData = new PostData(userID, title, start, arrive, person, index, point,pay,"","","");
                    databaseReference.child("call-taxi").push().setValue(postData);
                }
            }
        });

    }
    public void Addcmt(String str,int index){                 //시스템 댓글
        CommentData cmt = new CommentData(str,index);
        mCommentsReference.push().setValue(cmt);
    }
    public void AddComment(String userID,String comment,int index) {       //사용자 댓글
        CommentData commentData = new CommentData(userID,comment,index);
        mCommentsReference.push().setValue(commentData);
    }
}
