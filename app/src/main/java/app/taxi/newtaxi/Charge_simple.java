package app.taxi.newtaxi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

public class Charge_simple extends AppCompatActivity {
    long now = System.currentTimeMillis ();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String Time = sdfNow.format(date);
    int YEAR = Integer.parseInt(Time.split("/")[0]);
    int MONTH = Integer.parseInt(Time.split("/")[1]);
    int DAY = Integer.parseInt(Time.split("/")[2].split(" ")[0]);
    private final int stuck = 10;

    DatabaseReference mDatabase,rDatabase;

    TextView MyPointText,CreditText,ResultText;
    ImageView CoinImage;
    Button CreditButton;
    String INDEX,USERNAME,PHONENUMBER,PROFILEURL;
    int CREDIT = 0;
    AlertDialog.Builder alertDialogBuilder;

    void init(){
        SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
        final SharedPreferences.Editor editor = positionDATA.edit();

        INDEX = positionDATA.getString("ID","");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        rDatabase = FirebaseDatabase.getInstance("https://taxitogether.firebaseio.com/").getReference();

        MyPointText = findViewById(R.id.MyPointText);
        CreditText = findViewById(R.id.CreditText);
        ResultText = findViewById(R.id.ResultText);
        CoinImage = findViewById(R.id.CoinImage);
        CreditButton = findViewById(R.id.CreditButton);

        BootpayAnalytics.init(this, "5c5170df396fa67f8155acbe");

        Query query = mDatabase.child("user").orderByChild("email").equalTo(INDEX);
        Log.e("INDEX",INDEX);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    MyPointText.setText(user.getPoint() + " P");
                    editor.putString("POINT",user.getPoint()+"");
                    USERNAME = user.getUsername();
                    PHONENUMBER = user.getPhonenumber();
                    PROFILEURL = user.getProfile_url();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void click(){
        CoinImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreditText.setText(Integer.valueOf(CreditText.getText().toString().split(" ")[0]) + 1000 + " 원");
                ResultText.setText(Integer.valueOf(MyPointText.getText().toString().split(" ")[0])
                + Integer.valueOf(CreditText.getText().toString().split(" ")[0]) + " P");
                CREDIT = Integer.valueOf(CreditText.getText().toString().split(" ")[0]);
            }
        });
        CoinImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                CreditText.setText("0 원");
                return false;
            }
        });
        CreditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog(CREDIT);
                alertDialogBuilder.show();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_simple);
        init();
        click();
    }
    private void Dialog(final int Point) {
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("결제 확인");
        alertDialogBuilder.setMessage("충전 : " + Point + " 원");
        alertDialogBuilder.setPositiveButton("결제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PAY(Point);
            }
        });

    }
    void PAY(int Price){
        final int P = Price;
        final SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        final SharedPreferences.Editor editor = positionDATA.edit();
        String phonenumber = positionDATA.getString("PHONENUMBER","010-1234-5678");
        String orderID = positionDATA.getString("ID","") + MONTH + DAY;
        BootUser bootUser = new BootUser().setPhone(phonenumber).setUsername(INDEX);
        Bootpay.init(getFragmentManager())
                .setApplicationId("5c5170df396fa67f8155acbe") // 해당 프로젝트(안드로이드)의 application id 값
                .setPG(PG.KAKAO) // 결제할 PG 사
                .setBootUser(bootUser)
                .setMethod(Method.CARD) // 결제수단
                .setName("T.T 포인트 결제") // 결제할 상품명
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
        total = Integer.valueOf(MyPointText.getText().toString().split(" ")[0]) + Price;
        Update_user(total);
        REPORT_POINT(Price);

        Intent intent = new Intent(getApplicationContext(),main.class);
        intent.putExtra("MESSAGE","포인트 충전이 완료되었습니다.");
        startActivity(intent);
        finish();
    }
    void Update_user(int point){
        SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
        final SharedPreferences.Editor editor = positionDATA.edit();

        Map<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put(INDEX,new User(USERNAME,"",INDEX,PHONENUMBER,point,PROFILEURL));
        mDatabase.child("user").updateChildren(taskMap);
        rDatabase.child("user").updateChildren(taskMap);
        editor.putString("POINT",point+"");
        editor.apply();
    }
    void REPORT_POINT(int point){
        Data_report_charge data_report_charge = new Data_report_charge(Time,point+"");
        rDatabase.child("report")
                .child("charge")
                .child(INDEX).push().setValue(data_report_charge);
    }
}
