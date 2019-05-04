package app.taxi.newtaxi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class My extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String ID,PHONENUMBER,POINT,PROFILE_URL,NAME;
    private EditText phonenumber_Text;
    private TextView UserID_textview,Name_textview,Point_textview,LOGOUTbutton;
    private ImageView profile_imageview;
    private Button updateBtn;
    private Boolean Enable = false;
    private DatabaseReference mDatabase;
    void init(){
        SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();
        Intent intent = getIntent();
        ID = positionDATA.getString("USERNAME","");
        NAME = positionDATA.getString("ID","");
        PROFILE_URL = positionDATA.getString("PROFILE","");
        POINT = intent.getExtras().getString("POINT");

        phonenumber_Text = findViewById(R.id.phonenumber_Text);
        updateBtn = findViewById(R.id.updateBtn);
        UserID_textview = findViewById(R.id.UserID_textview);
        Name_textview = findViewById(R.id.Name_textview);
        profile_imageview = findViewById(R.id.profile_imageview);
        Point_textview = findViewById(R.id.Point_textview);
        LOGOUTbutton = findViewById(R.id.LOGOUTbutton);

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    void setting(){
        phonenumber_Text.setEnabled(Enable);
        Name_textview.setText(NAME);
        UserID_textview.setText(ID);
        Point_textview.setText(POINT);

        profile_imageview.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21) {
            profile_imageview.setClipToOutline(true);
        }
        Glide.with(this)
                .load(PROFILE_URL)
                .into(profile_imageview);
    }

    void clickEvent(){
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Enable == false) {
                    Enable = true;
                    phonenumber_Text.setEnabled(Enable);
                }else{
                    Enable = false;
                    phonenumber_Text.setEnabled(Enable);
                    Update_user(phonenumber_Text.getText().toString());
                }
            }
        });
        LOGOUTbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent logoutIntent = new Intent(getApplicationContext(), Login.class);
                startActivity(logoutIntent);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        init();
        setting();
        clickEvent();
        Query query = mDatabase.child("user").orderByChild("email").equalTo(ID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    User user = appleSnapshot.getValue(User.class);
                    Point_textview.setText(String.valueOf(user.getPoint()));
                    phonenumber_Text.setText(user.getPhonenumber());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    void Update_user(String phonenumber){
        final SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        final SharedPreferences.Editor editor = positionDATA.edit();

        Query query = mDatabase.child("user").orderByChild("email").equalTo(ID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    User user = appleSnapshot.getValue(User.class);
                    mDatabase = mDatabase.child("user");
                    final String phonenumber = phonenumber_Text.getText().toString();
                    editor.putString("PHONENUMBER",phonenumber_Text.getText().toString());
                    editor.apply();

                    Map<String, Object> taskMap = new HashMap<String, Object>();
                    taskMap.put(ID,new User(NAME,null,ID,phonenumber,user.getPoint(),PROFILE_URL));
                    mDatabase.updateChildren(taskMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}