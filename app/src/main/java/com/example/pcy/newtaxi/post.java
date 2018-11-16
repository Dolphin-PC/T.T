package com.example.pcy.newtaxi;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class post extends AppCompatActivity {
    private DatabaseReference mDatabase;
    EditText postText;
    EditText startText;
    EditText arriveText;
    EditText pointText;
    Button postButton;
    int index = 1;
    static String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Intent intent = getIntent();
        userID = intent.getExtras().getString("userID");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        postText = findViewById(R.id.post_title_Text);
        startText = findViewById(R.id.startText);
        arriveText = findViewById(R.id.arriveText);
        postButton = findViewById(R.id.postButton);
        pointText = findViewById(R.id.pointText);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
                index++;
            }
        });
        ConstraintLayout LAY1 = findViewById(R.id.LAY3);
        LAY1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(postText.getWindowToken(),0);
            }
        });
    }
    private void submitPost(){
        final String title = postText.getText().toString();
        final String start = startText.getText().toString();
        final String arrive = arriveText.getText().toString();
        final String point = pointText.getText().toString();
        if (TextUtils.isEmpty(title)) {
            postText.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(start)) {
            startText.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(arrive)) {
            arriveText.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(point)){
            pointText.setError("Required");
            return;
        }
        Toast.makeText(this, "모집글 게시중...", Toast.LENGTH_SHORT).show();
        PostData postData = new PostData(userID,title,start,arrive,0,index,Integer.parseInt(point),Integer.parseInt(point),"","","");
        mDatabase.child("post").push().setValue(postData);
    }



}
