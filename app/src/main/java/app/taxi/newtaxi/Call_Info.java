package app.taxi.newtaxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Call_Info extends AppCompatActivity {
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    TextView DriverNameText, PhoneText, TaxiNumberText;
    Button InfoButton, DriveButton;

    String INDEX, ID;
    int POINT;

    AlertDialog.Builder Dialog;

    android.app.Dialog PayDialog;

    TextView TitleText, PayText, PersonText, ServiceText, ResultText;
    Button PayButton;

    Boolean payable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call__info);

        PayDialog = new Dialog(this);
        PayDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        PayDialog.setContentView(R.layout.activity_pay__info);
        PayDialog.setCancelable(false);

        Dialog = new AlertDialog.Builder(this);

        init();
        init_Database();
        click();

    }

    void init() {
        DriverNameText = findViewById(R.id.DriverNameText);
        PhoneText = findViewById(R.id.PhoneText);
        TaxiNumberText = findViewById(R.id.TaxiNumberText);
        InfoButton = findViewById(R.id.InfoButton);
        DriveButton = findViewById(R.id.DriveButton);

        SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
        INDEX = positionDATA.getString("INDEX", "");
        ID = positionDATA.getString("ID", "");
        POINT = positionDATA.getInt("POINT", 0);
        Log.e("ID",ID);
    }

    void init_Database() {
        Query query = mDatabase.child("taxi-call").orderByChild("index").equalTo(INDEX);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data_Taxi data_taxi = snapshot.getValue(Data_Taxi.class);
                    DriverNameText.setText(data_taxi.getDriver());
                    PhoneText.setText(data_taxi.getTaxiPhonenumber());
                    TaxiNumberText.setText(data_taxi.getTaxinumber());
                    if (data_taxi.getComplete_Driver() && data_taxi.getComplete_Client() && data_taxi.getPay_complete() >= 1) {
                        DIALOG(data_taxi.getPay(), data_taxi.getPerson(), data_taxi.getService_each());
                    }
                    if (data_taxi.getComplete_ride()) {
                        DriveButton.setText("운행 종료");
                        DriveButton.setBackgroundColor(Color.parseColor("#323E4D"));
                    } else if(data_taxi.getComplete_Client()){
                        DriveButton.setText("결제 대기중");
                        DriveButton.setEnabled(false);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void AlertDialog(String Title, String Message) {
        Dialog = new AlertDialog.Builder(this);
        Dialog.setTitle(Title).setMessage(Message).show();
        if (Title.equals("포인트 부족")) {

        }
    }

    void DIALOG(int pay, int person, final int service) {
        PayDialog.show();
        TitleText = PayDialog.findViewById(R.id.TitleText);
        PayText = PayDialog.findViewById(R.id.PayText);
        PersonText = PayDialog.findViewById(R.id.PersonText);
        ServiceText = PayDialog.findViewById(R.id.ServiceText);
        ResultText = PayDialog.findViewById(R.id.ResultText);
        PayButton = PayDialog.findViewById(R.id.PayButton);
        //label
        final TextView pay_label = PayDialog.findViewById(R.id.pay_label);
        final ImageView imageview = PayDialog.findViewById(R.id.imageView);
        final TextView service_label = PayDialog.findViewById(R.id.service_label);
        final TextView result_label = PayDialog.findViewById(R.id.result_label);

        TitleText.setText("요금 요청 대기중");
        PayText.setText(pay + " 원");
        PersonText.setText("/ " + person);
        ServiceText.setText(service + " 원");
        ResultText.setText(pay / person + service + " 원");
        final int PAY = pay / person + service;
        final int PAY_Service = pay / person;

        Dialog = new AlertDialog.Builder(this);

        //요금 요청
        mDatabase.child("taxi-call").child(INDEX).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Data_Taxi data_taxi = dataSnapshot.getValue(Data_Taxi.class);
                if (data_taxi.getPay_complete() == 1) { //택시 기사가 요금을 요청하기 전
                    TitleText.setText("요금 요청 대기 중");
                    PayText.setText("요청 대기");
                    ServiceText.setText("요청 대기");
                    ResultText.setText("요청 대기");
                    PayButton.setText("요청 대기중 입니다.");
                } else if (data_taxi.getPay_complete() != 1 && payable) { //택시 기사가 요금을 요청 함
                    PayDialog.dismiss();
                    PayDialog.show();
                    TitleText.setText("결제 요청");
                    PayText.setText(data_taxi.getPay() + " 원");
                    PersonText.setText("/ " + data_taxi.getPerson());
                    ServiceText.setText(data_taxi.getService_each() + " 원");
                    ResultText.setText(data_taxi.getPay() / data_taxi.getPerson() + data_taxi.getService_each() + " 원");
                    PayButton.setText("결제");
                    //요금 결제 완료 후, post-members에 'pay',boolean을 추가하여, 그 여부로 텍스트 변경
                    Query query = mDatabase.child("post-members").orderByChild("userid").equalTo(ID);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Data_Members data_members = snapshot.getValue(Data_Members.class);
                                if (data_members.getPAY()) { // 해당 사용자가 결제를 완료했을때(true)
                                    PayDialog.dismiss();
                                    PayDialog.show();
                                    TitleText.setText("결제 완료");
                                    pay_label.setText("결제 전 포인트");
                                    PayText.setText(POINT + ""); // 결제 전 내 포인트
                                    PersonText.setVisibility(View.INVISIBLE);
                                    imageview.setVisibility(View.VISIBLE);
                                    service_label.setText("결제 요금");
                                    ServiceText.setText(PAY + " P"); // 결제 요금
                                    result_label.setText("결제 후, 내 포인트");
                                    ResultText.setText(POINT - Integer.valueOf(PAY) +" P");
                                    PayButton.setText("동승자의 결제를 기다리고 있습니다.");
                                    PayButton.setEnabled(false);
                                    payable = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        PayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //결제하기 버튼 눌렀을때, user에서 포인트 차감하고, pay_complete결제
                if (PayButton.getText().toString().equals("요청 대기중 입니다.")) {
                    Toast.makeText(getApplicationContext(), "결제 진행 중입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Dialog.setTitle("결제 확인").setMessage(ResultText.getText().toString() + "을 자동결제합니다.")
                            .setPositiveButton("결제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Query query = mDatabase.child("user").orderByChild("email").equalTo(INDEX);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                User user = snapshot.getValue(User.class);
                                                String path = "/" + dataSnapshot.getKey() + "/" + snapshot.getKey();
                                                java.util.Map<String, Object> POINTmap = new HashMap<String, Object>();
                                                if (user.getPoint() - PAY < 0) {
                                                    AlertDialog("포인트 부족", "포인트를 충전하러 가시겠습니까?");
                                                } else {
                                                    POINTmap.put("point", user.getPoint() - PAY); // 해당 사용자의 포인트 차감
                                                    mDatabase.child(path).updateChildren(POINTmap);
                                                    Query query1 = mDatabase.child("taxi-call").orderByChild("index").equalTo(INDEX);
                                                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                Data_Taxi data_taxi = snapshot.getValue(Data_Taxi.class);
                                                                Map<String, Object> map = new HashMap<>();
                                                                map.put("pay_complete", data_taxi.getPay_complete() - PAY_Service);
                                                                mDatabase.child("taxi-call").child(INDEX).updateChildren(map);
                                                            }
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        }
                                                    }); // 결제 완료
                                                    Query query2 = mDatabase.child("post-members").orderByChild("userid").equalTo(ID);
                                                    Log.e("ID",ID); //TODO : ?왜 안되는겨?
                                                    query2.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                            for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                                                                String path = "/" + dataSnapshot1.getKey() + "/" + snapshot1.getKey();
                                                                HashMap<String, Object> map = new HashMap<>();
                                                                map.put("pay", true);
                                                                mDatabase.child(path).updateChildren(map);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    }); // post-members pay <- false(결제 완료_
                                                    Toast.makeText(getApplicationContext(), PAY + " P가 결제되었습니다.", Toast.LENGTH_SHORT).show();
                                                    Query query_1 = mDatabase.child("taxi-call").orderByChild("index").equalTo(INDEX);
                                                    query_1.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                                Data_Taxi data_taxi = snapshot1.getValue(Data_Taxi.class);
                                                                final int pay_complete = data_taxi.getPay_complete();
                                                                Log.e("pay", pay_complete + "");
                                                                Log.e("pay_1", data_taxi.getPay_complete() + "");

                                                                if (pay_complete == 0) {
                                                                    Toast.makeText(getApplicationContext(), "결제가 정상적으로 처리되었습니다.\n이용해주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(getApplicationContext(), Review.class);
                                                                    startActivity(intent);
                                                                    QUIT_PROCESS_referenceDATA(); //마무으리
                                                                    finish();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });

                                                    //TODO : 리뷰작성 페이지(끝마치는 페이지) 작성하
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }).show();
                }
            }
        });

    }

    void click() {
        InfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO : 다이얼로그로 기사 사진, 면허증 보여주기
            }
        });
        DriveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DriveButton.getText().toString().equals("탑승 완료")) {
                    Drive_Dialog("탑승 완료", "택시에 탑승하셨나요?");
                } else if (DriveButton.getText().toString().equals("운행 종료")) {
                    Drive_Dialog("운행 종료", "목적지에 도착하셨나요?");
                }
            }
        });
        PhoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog.setTitle("전화하기").setMessage("").setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent CallIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + PhoneText.getText().toString()));
                        startActivity(CallIntent);
                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onDestroy();
                    }
                }).show();
            }
        });
    }

    void Drive_Dialog(String Title, String Message) {
        Dialog = new AlertDialog.Builder(this);
        Dialog.setTitle(Title).setMessage(Message);
        if (Title.equals("탑승 완료")) {
            Dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Dialog.setMessage("목적지에 도착하신 후\n운행 종료 버튼을 눌러\n결제를 진행해주세요!").setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("complete_ride", true);
                            mDatabase.child("taxi-call").child(INDEX).updateChildren(map);
                        }
                    }).show();
                }
            })
                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
        } else if (Title.equals("운행 종료")) {
            Dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("complete_client", true);
                    mDatabase.child("taxi-call").child(INDEX).updateChildren(map);
                }
            })
                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
        }
        Dialog.show();
    }

    void QUIT_PROCESS_referenceDATA() {
        SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();
        editor.remove("DISTANCE");
        editor.remove("PERSON");
        editor.remove("MAX");
        editor.remove("도착지");
        editor.remove("TIME");
        editor.remove("도착");
        editor.remove("출발");
        editor.remove("출발지");
        editor.remove("INDEX");
        editor.apply();
    }

}
