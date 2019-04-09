package app.taxi.newtaxi;

import android.content.Intent;
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
    private static final String TAG = "main";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase;
    String nickname,userid,profileURL,email;
    ImageView profile_imageview;
    TextView Name_textview,Email,Point_textview,UserID_textview;
    Button My_button,charge_btn,logout_btn,start_btn;
    void init(){
        profile_imageview = findViewById(R.id.profile_imageview);
        Name_textview = findViewById(R.id.Name_textview);
        UserID_textview = findViewById(R.id.UserID_textview);
        Point_textview = findViewById(R.id.Point_textview);
        My_button = findViewById(R.id.My_button);
        charge_btn = findViewById(R.id.charge_btn);
        logout_btn = findViewById(R.id.logout_btn);
        start_btn = findViewById(R.id.start_btn);

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }
    void Setting(){
        final Intent intent = getIntent();
        nickname = intent.getExtras().getString("Nickname");
        userid = intent.getExtras().getString("ID");
        profileURL = intent.getExtras().getString("Profile");

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

       logout_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mAuth.signOut();
               Intent logoutIntent = new Intent(getApplicationContext(), Login.class);
               startActivity(logoutIntent);
               finish();
           }
       });
       My_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent1 = new Intent(getApplicationContext(),My.class);
               intent1.putExtra("ID",userid);
               intent1.putExtra("NAME",nickname);
               intent1.putExtra("PROFILE",profileURL);
               startActivity(intent1);
           }
       });
       charge_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent1 = new Intent(getApplicationContext(),Charge.class);
           }
       });
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(),Map.class);
                startActivity(intent1);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Setting();

        Query query = mDatabase.child("user").orderByChild("email").equalTo(userid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren().iterator().hasNext()){
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        User user = appleSnapshot.getValue(User.class);
                        Point_textview.setText(user.getPoint());
                        Update_user(user.getPoint(),user.getPhonenumber());
                        return;
                    }
                }else{
                    Update_user("0","0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    void Update_user(String point,String phonenumber){
        Map<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put(userid,new User(nickname,null,userid,phonenumber,point,null));
        mDatabase.child("user").updateChildren(taskMap);
    }

}
