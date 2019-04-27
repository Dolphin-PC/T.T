package app.taxi.newtaxi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.BootpayAnalytics;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;
import kr.co.bootpay.listner.CancelListener;
import kr.co.bootpay.listner.CloseListener;
import kr.co.bootpay.listner.ConfirmListener;
import kr.co.bootpay.listner.DoneListener;
import kr.co.bootpay.listner.ErrorListener;
import kr.co.bootpay.listner.ReadyListener;
import kr.co.bootpay.model.BootUser;

public class Charge extends AppCompatActivity {
    long now = System.currentTimeMillis ();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("MM/DD");
    String Time = sdfNow.format(date);
    int MONTH = Integer.parseInt(Time.split("/")[0]);
    int DAY = Integer.parseInt(Time.split("/")[1]);
    private final int stuck = 10;
    Button B1,B2,B3,B4,B5;
    TextView point_textview;
    String userID,USERNAME,PROFILEURL;
    AlertDialog.Builder alertDialogBuilder;
    private DatabaseReference mDatabase;
    void init(){
        SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();

        Intent intent = getIntent();
        B1 = findViewById(R.id.B1);
        B2 = findViewById(R.id.B2);
        B3 = findViewById(R.id.B3);
        B4 = findViewById(R.id.B4);
        B5 = findViewById(R.id.B5);
        point_textview = findViewById(R.id.point_textview);
        point_textview.setText(intent.getExtras().getString("POINT"));

        USERNAME = positionDATA.getString("USERNAME","");
        userID = positionDATA.getString("ID","");
        PROFILEURL = positionDATA.getString("PROFILE","");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        BootpayAnalytics.init(this, "5c5170df396fa67f8155acbe");
    }
    void click(){
        B1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog(B1.getText().toString());
                alertDialogBuilder.show();
            }
        });
        B2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog(B2.getText().toString());
                alertDialogBuilder.show();
            }
        });
        B3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog(B3.getText().toString());
                alertDialogBuilder.show();
            }
        });
        B4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog(B4.getText().toString());
                alertDialogBuilder.show();
            }
        });
        B5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog(B5.getText().toString());
                alertDialogBuilder.show();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);
        init();
        click();
    }

    private void Dialog(String Point) {
        final String P = Point;
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("결제 확인");
        alertDialogBuilder.setMessage("충전 : " + Point);
        alertDialogBuilder.setPositiveButton("결제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (P){
                    case "1,000 P" : PAY(1000); break;
                    case "2,000 P" : PAY(2000); break;
                    case "3,000 P" : PAY(3000); break;
                    case "5,000 P" : PAY(5000); break;
                    case "10,000 P" : PAY(10000); break;
                }
            }
        });

    }
    void PAY(int Price){
        final int P = Price;
        final SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        final SharedPreferences.Editor editor = positionDATA.edit();
        String phonenumber = positionDATA.getString("PHONENUMBER","010-1234-5678");
        String orderID = positionDATA.getString("ID","") + MONTH + DAY;
        BootUser bootUser = new BootUser().setPhone(phonenumber).setUsername(userID);
        Bootpay.init(getFragmentManager())
                .setApplicationId("5c5170df396fa67f8155acbe") // 해당 프로젝트(안드로이드)의 application id 값
                .setPG(PG.KAKAO) // 결제할 PG 사
                .setBootUser(bootUser)
                .setMethod(Method.CARD) // 결제수단
                .setName("택시 비용") // 결제할 상품명
                .setOrderId(orderID) // 결제 고유번호
                .setPrice(Price) // 결제할 금액
                .onConfirm(new ConfirmListener() { // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
                    @Override
                    public void onConfirm(@Nullable String message) {
                        if (0 < stuck) Bootpay.confirm(message); // 재고가 있을 경우.
                        else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                        Log.d("confirm", message);
                    }
                })
                .onDone(new DoneListener() { // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
                    @Override
                    public void onDone(@Nullable String message) {
                        PAY2(P);
                        Log.d("done", message);
                    }
                })
                .onReady(new ReadyListener() { // 가상계좌 입금 계좌번호가 발급되면 호출되는 함수입니다.
                    @Override
                    public void onReady(@Nullable String message) {
                        Log.d("ready", message);
                    }
                })
                .onCancel(new CancelListener() { // 결제 취소시 호출
                    @Override
                    public void onCancel(@Nullable String message) {

                        Log.d("cancel", message);
                    }
                })
                .onError(new ErrorListener() { // 에러가 났을때 호출되는 부분
                    @Override
                    public void onError(@Nullable String message) {
                        Log.d("error", message);
                    }
                })
                .onClose(
                        new CloseListener() { //결제창이 닫힐때 실행되는 부분
                            @Override
                            public void onClose(String message) {
                                Log.d("close", "close");
                            }
                        })
                .request();
    }
    void PAY2(int Price){
        int total;
        total = Integer.valueOf(point_textview.getText().toString()) + Price;
        Update_user(total);

        Intent intent = new Intent(getApplicationContext(),main.class);
        intent.putExtra("USERNAME",USERNAME);
        intent.putExtra("ID",userID);
        intent.putExtra("Profile",PROFILEURL);
        startActivity(intent);
        finish();
    }
    void Update_user(int point){
        Map<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put(userID,new User("","",userID,"",point,""));
        mDatabase.child("user").updateChildren(taskMap);
    }
}
