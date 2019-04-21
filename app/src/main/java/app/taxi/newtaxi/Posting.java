package app.taxi.newtaxi;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    private DatabaseReference mDatabase;
    String START,ARRIVE,DISTANCE,PRICE;
    TextView STARTtext,ARRIVEtext,DISTANCEtext,PRICEtext,PERSONPRICEtext,TIMEtext;
    Button PERSONbutton,PERSONbutton2,POSTbutton;
    CheckBox TIMEcheck;
    long now = System.currentTimeMillis ();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
    String Time = sdfNow.format(date);
    int Hour = Integer.parseInt(Time.split(":")[0]);
    int Minute = Integer.parseInt(Time.split(":")[1]);
    TimePickerDialog dialog;
    AlertDialog.Builder alertDialogBuilder;
    void init(){
        Intent intent = getIntent();
        START= intent.getExtras().getString("START");
        ARRIVE= intent.getExtras().getString("ARRIVE");
        DISTANCE= intent.getExtras().getString("DISTANCE");
        PRICE= intent.getExtras().getString("PRICE");

        STARTtext = findViewById(R.id.STARTtext);
        ARRIVEtext = findViewById(R.id.ARRIVEtext);
        DISTANCEtext = findViewById(R.id.DISTANCEtext);
        PRICEtext = findViewById(R.id.PRICEtext);
        PERSONPRICEtext = findViewById(R.id.PERSONPRICEtext);
        TIMEtext = findViewById(R.id.TIMEtext);
        PERSONbutton = findViewById(R.id.PERSONbutton);
        PERSONbutton2 = findViewById(R.id.PERSONbutton2);
        POSTbutton=findViewById(R.id.POSTbutton);
        TIMEcheck = findViewById(R.id.TIMEcheck);

        STARTtext.setText(START);
        ARRIVEtext.setText(ARRIVE);
        DISTANCEtext.setText(DISTANCE);
        PRICEtext.setText(PRICE);
        dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                TIMEtext.setText("예약 : " + hourOfDay + "시 "+ minute + "분");
            }
        },Hour,Minute,false);
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("정보 확인")
                .setMessage("게시글을 등록하시겠습니까?")
                .setPositiveButton("등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        POST();
                        Intent intent1 = new Intent(getApplicationContext(),My_taxi.class);
                        startActivity(intent1);
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void POST() {
        SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();
        String userID = positionDATA.getString("NAME","");
        int index = Integer.parseInt(positionDATA.getString("ID","1"));                  //카카오톡 사용자의 일련번호로 인덱스 번호.
        Data_Post dataPost = new Data_Post(userID //게시자의 이름
                ,"" //게시글 제목
                ,START,ARRIVE //출발지/도착지
                ,1  //person
                ,index  //일련번호 인덱스
                ,Integer.valueOf(PRICE.split(" ")[3]) //전체 가격
                ,Integer.parseInt(PERSONPRICEtext.getText().toString().split(" ")[3])       //인당 지불 금액
                ,"","","");
        mDatabase.child("post").push().setValue(dataPost);
    }

    void click(){
        PERSONbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PERSONbutton.setTextColor(Color.parseColor("#FF6600"));
                PERSONbutton2.setTextColor(Color.parseColor("#000000"));
                PERSONPRICEtext.setText("개인 부담 금액은 " + (Integer.valueOf(PRICE.split(" ")[3])/2)
                        + " 원 입니다.(2인 탑승)");
                PERSONPRICEtext.setVisibility(View.VISIBLE);
            }
        });
        PERSONbutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PERSONbutton.setTextColor(Color.parseColor("#000000"));
                PERSONbutton2.setTextColor(Color.parseColor("#FF6600"));
                PERSONPRICEtext.setText("개인 부담 금액은 " + (Integer.valueOf(PRICE.split(" ")[3])/3)
                        + " 원 입니다.(3인 탑승)");
                PERSONPRICEtext.setVisibility(View.VISIBLE);
            }
        });
        TIMEcheck.setOnClickListener(new CheckBox.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(TIMEcheck.isChecked()){
                    dialog.show();
                    TIMEtext.setVisibility(View.VISIBLE);
                }
                else{
                    TIMEtext.setVisibility(View.INVISIBLE);
                }
            }
        });
        POSTbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBuilder.show();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);
        init();
        click();
        POSTbutton.setEnabled(false);
        if(PERSONPRICEtext != null){
            POSTbutton.setEnabled(true);
        }
    }
}
