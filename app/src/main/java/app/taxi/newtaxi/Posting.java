package app.taxi.newtaxi;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
public class Posting extends AppCompatActivity {
    Selector selector = Selector.Selector;
    private DatabaseReference mDatabase;
    String START,ARRIVE,DISTANCE,PRICE,TIME,INDEX;
    TextView STARTtext,ARRIVEtext,DISTANCEtext,PRICEtext,PERSONPRICEtext,TIMEtext;
    Button PERSONbutton,PERSONbutton2,PERSONbutton3,POSTbutton;
    CheckBox TIMEcheck;
    long now = System.currentTimeMillis ();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
    String Time = sdfNow.format(date);
    int Hour = Integer.parseInt(Time.split(":")[0]);
    int Minute = Integer.parseInt(Time.split(":")[1]);
    int MaxPerson,PAY_up=0,PAY;
    TimePickerDialog dialog;
    AlertDialog.Builder alertDialogBuilder;
    void init(){
        SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();

        Intent intent = getIntent();
        START= intent.getExtras().getString("START");
        ARRIVE= intent.getExtras().getString("ARRIVE");
        DISTANCE= intent.getExtras().getString("DISTANCE");
        PRICE= intent.getExtras().getString("PRICE");
        INDEX = positionDATA.getString("ID","1");//카카오톡 사용자의 일련번호로 인덱스 번호.

        STARTtext = findViewById(R.id.ArriveText);
        ARRIVEtext = findViewById(R.id.ARRIVEtext);
        DISTANCEtext = findViewById(R.id.DISTANCEtext);
        PRICEtext = findViewById(R.id.PRICEtext);
        PERSONPRICEtext = findViewById(R.id.PERSONPRICEtext);
        TIMEtext = findViewById(R.id.TIMEtext);
        PERSONbutton = findViewById(R.id.PERSONbutton);
        PERSONbutton2 = findViewById(R.id.PERSONbutton2);
        PERSONbutton3 = findViewById(R.id.PERSONbutton3);
        POSTbutton=findViewById(R.id.POSTbutton);
        TIMEcheck = findViewById(R.id.TIMEcheck);

        STARTtext.setText(START);
        ARRIVEtext.setText(ARRIVE);
        DISTANCEtext.setText(DISTANCE);
        PRICEtext.setText(PRICE);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        TIME = Hour+"시 " + Minute + "분";
    }

    private void POST() {
        SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();

        editor.putString("PERSON",Integer.toString(1));
        editor.putString("PRICE",PRICE.split(" ")[3]);
        editor.putString("TIME",TIME);
        editor.putString("INDEX",INDEX);
        editor.putString("??",INDEX);
        editor.putString("DISTANCE",DISTANCE.split(":")[1]);
        editor.apply();

        String userID = positionDATA.getString("USERNAME","");
        String URL = positionDATA.getString("PROFILE","");
        String Start_latitude = positionDATA.getString("출발","").split(",")[0];
        String Start_longitude = positionDATA.getString("출발","").split(",")[1];
        String Arrive_latitude = positionDATA.getString("도착","").split(",")[0];
        String Arrive_longitude = positionDATA.getString("도착","").split(",")[1];

        Data_Post dataPost = new Data_Post(userID //게시자의 이름
                ,(PAY*MaxPerson)+""
                ,"" //게시글 제목
                ,START,Start_latitude,Start_longitude,ARRIVE,Arrive_latitude,Arrive_longitude //출발지/도착지
                ,1  //person
                ,MaxPerson
                ,INDEX  //일련번호 인덱스
                ,DISTANCE.split(":")[1]
                ,PAY
                ,TIMEtext.getText().toString().split(":")[0]
                ,"","","");
        Data_Members data_members = new Data_Members(userID,String.valueOf(INDEX),URL,"남",String.valueOf(INDEX),false,false);
        mDatabase.child("post").push().setValue(dataPost);
        mDatabase.child("post-members").push().setValue(data_members);
    }

    void click(){
        SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        final SharedPreferences.Editor editor = positionDATA.edit();
        PAY = Integer.valueOf(PRICE.split(" ")[3]);
        PERSONbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PERSONbutton.setTextColor(Color.parseColor("#FF6600"));
                PERSONbutton2.setTextColor(Color.parseColor("#000000"));
                PERSONbutton3.setTextColor(Color.parseColor("#000000"));
                if((PAY / 2) % 100 == 0)
                    PERSONPRICEtext.setText("개인 부담 금액은 " + PAY/2 + " 원 입니다.(2인 탑승)");
                else {
                    PAY_up = 100 - (PAY / 2 % 100);
                    PERSONPRICEtext.setText("개인 부담 금액은 " + (PAY / 2 + PAY_up)
                            + " 원 입니다.(2인 탑승)");
                }
                PERSONPRICEtext.setVisibility(View.VISIBLE);
                MaxPerson=2;
                editor.putString("MAX",String.valueOf(MaxPerson));
                editor.putString("PAY",(PAY/2+PAY_up)+"");
                POSTbutton.setEnabled(true);
            }
        });
        PERSONbutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PERSONbutton.setTextColor(Color.parseColor("#000000"));
                PERSONbutton2.setTextColor(Color.parseColor("#FF6600"));
                PERSONbutton3.setTextColor(Color.parseColor("#000000"));
                if((PAY / 3) % 100 == 0)
                    PERSONPRICEtext.setText("개인 부담 금액은 " + PAY/3 + " 원 입니다.(3인 탑승)");
                else {
                    PAY_up = 100 - (PAY / 3 % 100);
                    PERSONPRICEtext.setText("개인 부담 금액은 " + (PAY / 3 + PAY_up)
                            + " 원 입니다.(3인 탑승)");
                }
                PERSONPRICEtext.setVisibility(View.VISIBLE);
                MaxPerson=3;
                editor.putString("MAX",String.valueOf(MaxPerson));
                editor.putString("PAY",(PAY/3+PAY_up)+"");
                POSTbutton.setEnabled(true);
            }
        });
        PERSONbutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PERSONbutton.setTextColor(Color.parseColor("#000000"));
                PERSONbutton2.setTextColor(Color.parseColor("#000000"));
                PERSONbutton3.setTextColor(Color.parseColor("#FF6600"));
                if((PAY / 4) % 100 == 0)
                    PERSONPRICEtext.setText("개인 부담 금액은 " + PAY/4 + " 원 입니다.(4인 탑승)");
                else {
                    PAY_up = 100 - (PAY / 4 % 100);
                    PERSONPRICEtext.setText("개인 부담 금액은 " + (PAY / 4 + PAY_up)
                            + " 원 입니다.(4인 탑승)");
                }
                PERSONPRICEtext.setVisibility(View.VISIBLE);
                MaxPerson=4;
                editor.putString("MAX",String.valueOf(MaxPerson));
                editor.putString("PAY",(PAY/4+PAY_up)+"");
                POSTbutton.setEnabled(true);
            }
        });
        TIMEcheck.setOnClickListener(new CheckBox.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(TIMEcheck.isChecked()){
                    dialog.show();
                }
                else
                    TIME = Hour+"시 " + Minute + "분";
            }
        });
        POSTbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                POSTDIALOG();
                alertDialogBuilder.show();
                editor.apply();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);
        init();
        click();
        dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(minute == 0)
                    TIME = hourOfDay + "시";
                else
                    TIME = hourOfDay + "시 "+ minute + "분";
                TIMEtext.setText(TIME);
            }
        },Hour,Minute,false);

        POSTbutton.setEnabled(false);
    }
    void POSTDIALOG(){
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("정보 확인")
                .setMessage("게시글을 등록하시겠습니까? \n" + TIME)
                .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        POST();
                        Intent intent1 = new Intent(getApplicationContext(),My_taxi.class);
                        intent1.putExtra("INDEX",String.valueOf(INDEX));
                        startActivity(intent1);
                        selector.finish();
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    }
}
