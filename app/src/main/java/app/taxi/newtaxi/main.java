package app.taxi.newtaxi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

public class main extends AppCompatActivity {
    private BackPressCloseHandler backPressCloseHandler;
    int INDEX;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase;
    String nickname,userid,profileURL,email;
    ImageView profile_imageview;
    TextView Name_textview,Email,Point_textview,UserID_textview;
    Button My_button,charge_btn,start_btn,JOINbutton;
    void init(){

        profile_imageview = findViewById(R.id.profile_imageview);
        Name_textview = findViewById(R.id.Name_textview);
        UserID_textview = findViewById(R.id.UserID_textview);
        Point_textview = findViewById(R.id.Point_textview);
        My_button = findViewById(R.id.My_button);
        charge_btn = findViewById(R.id.charge_btn);
        start_btn = findViewById(R.id.start_btn);
        JOINbutton=findViewById(R.id.JOINbutton);

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    void Setting(){
        SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();

        nickname = positionDATA.getString("USERNAME","");
        userid = positionDATA.getString("ID","");
        profileURL = positionDATA.getString("PROFILE","");

        Name_textview.setText(nickname);
        /*Email.setText(email);*/
        UserID_textview.setText(userid);

        profile_imageview.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21) {
            profile_imageview.setClipToOutline(true);
        }

       Glide.with(this)
               .load(profileURL)
               .into(profile_imageview);

       My_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent1 = new Intent(getApplicationContext(),My.class);
               startActivity(intent1);
           }
       });
       charge_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent1 = new Intent(getApplicationContext(),Charge.class);
               intent1.putExtra("POINT",Point_textview.getText().toString());
               startActivity(intent1);

           }
       });
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start_btn.getText().toString().equals("노선 생성")) {
                    Intent intent1 = new Intent(getApplicationContext(), Selector.class);
                    intent1.putExtra("START", "");
                    intent1.putExtra("ARRIVE", "");
                    startActivity(intent1);
                }
                else{
                    Intent intent1 = new Intent(getApplicationContext(),My_taxi.class);
                    intent1.putExtra("INDEX",String.valueOf(INDEX));
                    startActivity(intent1);
                }
            }
        });
        JOINbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Join.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Setting();
        backPressCloseHandler = new BackPressCloseHandler(this);

        Query query = mDatabase.child("user").orderByChild("email").equalTo(userid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren().iterator().hasNext()){
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        User user = appleSnapshot.getValue(User.class);
                        Point_textview.setText(String.valueOf(user.getPoint()));
                        Update_user(user.getPoint(),user.getPhonenumber());
                        return;
                    }
                }else{
                    Update_user(0,"0");
                    Point_textview.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Query query1 = mDatabase.child("post-members").orderByChild("userid").equalTo(userid);
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren().iterator().hasNext()){
                    Data_Members data_members = dataSnapshot.getValue(Data_Members.class);
                        start_btn.setText("내 노선 보기");
                        INDEX = Integer.valueOf(userid);
                        JOINbutton.setVisibility(View.INVISIBLE);

                }else{
                    start_btn.setText("노선 생성");
                    JOINbutton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void Update_user(int point,String phonenumber){
        Map<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put(userid,new User(nickname,null,userid,phonenumber,point,null));
        mDatabase.child("user").updateChildren(taskMap);
    }

    @Override
    public void onBackPressed(){
        backPressCloseHandler.onBackPressed();
    }
}
