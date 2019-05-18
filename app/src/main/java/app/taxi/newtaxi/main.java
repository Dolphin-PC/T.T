package app.taxi.newtaxi;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public static main mainActivity;
    private BackPressCloseHandler backPressCloseHandler;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase;
    String nickname,userid,profileURL,email,INDEX;
    ImageView profile_imageview;
    TextView Name_textview,Email,Point_textview,UserID_textview;
    Button My_button,charge_btn,start_btn,JOINbutton;
    private boolean GPSgrant;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String KEY_LOCATION = "location";
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                }
            } else {

            }
        }
    }
    public boolean chkGpsService() {
        String gps = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
            gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            }).create().show();
            return false;
        } else {
            return true;
        }
    }
    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    void init(){
        Intent QUITintent = getIntent();
        String QUITmessage = QUITintent.getExtras().getString("MESSAGE");
        if(!QUITmessage.equals(""))
            Toast.makeText(getApplicationContext(),QUITmessage,Toast.LENGTH_SHORT).show();

        GPSgrant=chkGpsService();
        if(!GPSgrant)
            chkGpsService();

        getPermission();
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
    void Setting() {
        s

        nickname = positionDATA.getString("USERNAME", "");
        userid = positionDATA.getString("ID", "");
        profileURL = positionDATA.getString("PROFILE", "");
        INDEX = positionDATA.getString("INDEX","");

        Name_textview.setText(nickname);
        /*Email.setText(email);*/
        UserID_textview.setText(userid);

        profile_imageview.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            profile_imageview.setClipToOutline(true);
        }

        Glide.with(this)
                .load(profileURL)
                .into(profile_imageview);
    }
    void click(){
       My_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent1 = new Intent(getApplicationContext(),My.class);
               intent1.putExtra("POINT",Point_textview.getText().toString());
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
                    intent1.putExtra("ARRIVE", ""); //Selector <-> Map간의 intent를 위함.
                    startActivity(intent1);
                }
                else{
                    Intent intent1 = new Intent(getApplicationContext(),My_taxi.class);
                    intent1.putExtra("INDEX",INDEX);
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
        click();
        backPressCloseHandler = new BackPressCloseHandler(this);

        Query query = mDatabase.child("user").orderByChild("email").equalTo(userid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren().iterator().hasNext()){
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
                        SharedPreferences.Editor editor = positionDATA.edit();

                        User user = appleSnapshot.getValue(User.class);
                        Point_textview.setText(String.valueOf(user.getPoint()));
                        editor.putString("POINT",Point_textview.getText().toString());
                        editor.apply();

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
                        start_btn.setText("내 노선 보기");
                        JOINbutton.setVisibility(View.INVISIBLE);
                }else{
                    start_btn.setText("노선 생성");
                    JOINbutton.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
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
