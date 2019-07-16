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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Call_Info extends AppCompatActivity {
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    TextView DriverNameText, PhoneText, TaxiNumberText;
    Button InfoButton, DriveButton;

    String INDEX;

    AlertDialog.Builder Dialog;

    android.app.Dialog PayDialog;

    TextView PayText, PersonText, ServiceText, ResultText;
    Button PayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call__info);

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
                    if(data_taxi.getComplete_ride()){
                        DriveButton.setText("운행 종료");
                        DriveButton.setBackgroundColor(Color.parseColor("#323E4D"));
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
        Dialog.setTitle(Title).setMessage(Message);
        if (Title.equals("포인트 부족")) {

        }
        Dialog.show();
    }

    void DIALOG(int pay, int person, final int service) {
        PayDialog = new Dialog(this);
        PayDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        PayDialog.setContentView(R.layout.activity_pay__info);
        PayDialog.show();
        PayText = PayDialog.findViewById(R.id.PayText);
        PersonText = PayDialog.findViewById(R.id.PersonText);
        ServiceText = PayDialog.findViewById(R.id.ServiceText);
        ResultText = PayDialog.findViewById(R.id.ResultText);
        PayButton = PayDialog.findViewById(R.id.PayButton);

        PayText.setText(pay + " 원");
        PersonText.setText("/ " + person);
        ServiceText.setText(service + " 원");
        ResultText.setText(pay / person + service + " 원");
        final int PAY = pay / person + service;
        final int PAY_Service = pay/person;

        Dialog = new AlertDialog.Builder(this);
        PayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                                POINTmap.put("point", user.getPoint() - PAY);
                                                mDatabase.child(path).updateChildren(POINTmap);
                                                Query query1 = mDatabase.child("taxi-call").orderByChild("index").equalTo(INDEX);     //INDEX를 통해 JOIN을 바꾸면, 전체가 바뀜(ID를 통해 접근을 해서 해당 ID만 바꾸기)
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
                                                });
                                                Toast.makeText(getApplicationContext(), PAY + " P가 결제되었습니다.", Toast.LENGTH_SHORT).show();
                                                mDatabase.child("call-taxi").child(INDEX).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        Data_Taxi data_taxi = dataSnapshot.getValue(Data_Taxi.class);
                                                        if(data_taxi.getPay_complete() <= 0){
                                                            Intent intent = new Intent(getApplicationContext(),main_simple.class);
                                                            QUIT_PROCESS_referenceDATA(); //마무으리
                                                            QUIT_PROCESS_databaseDATA();
                                                            startActivity(intent);
                                                            finish();
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
                onDestroy();
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
                }else if(DriveButton.getText().toString().equals("운행 종료")){
                    Drive_Dialog("운행 종료","목적지에 도착하셨나요?");
                }
            }
        });
        PhoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog.setTitle("전화하기").setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent CallIntent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+PhoneText.getText().toString()));
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
                                    HashMap<String,Object> map = new HashMap<>();
                                    map.put("complete_ride",true);
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

    void QUIT_PROCESS_databaseDATA() {
        final Query POSTquery = mDatabase.child("post").orderByChild("index").equalTo(INDEX);
        final Query MEMBERSquery_1 = mDatabase.child("post-members").orderByChild("index").equalTo(INDEX);  //방장이 나갔을때, post-members전체 삭제
        final Query MEMBERSquery_2 = mDatabase.child("post-members").orderByChild("userid").equalTo(INDEX); //참가인원이 나갔을 때,
        final Query MESSAGEquery_1 = mDatabase.child("post-message").orderByChild("index").equalTo(INDEX); //방장이 나갔을때, post-message전체 삭제
        final Query MESSAGEquery_2 = mDatabase.child("post-message").orderByChild("id").equalTo(INDEX);    //참가인원이 나갔을 때,
        Query TAXI_query = mDatabase.child("taxi-call").orderByChild("index").equalTo(INDEX);              //방장이 나갔을때, taxi-call 삭제(취소)
        POSTquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data_Post data_post = snapshot.getValue(Data_Post.class);
                    if (INDEX.equals(data_post.getIndex())) {           //방장일 때, 방 전체 파기(post/post-members/post-message)
                        Log.d("post", "방장일 때");
                        mDatabase.child("post").child(snapshot.getKey()).removeValue();
                        MEMBERSquery_1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    mDatabase.child("post-members").child(snapshot1.getKey()).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                        MESSAGEquery_1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    mDatabase.child("post-message").child(snapshot1.getKey()).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    } else {                                           //참가한 인원일경우, person-1, post-members 파기
                        Log.d("post", "참가일 때");
                        String path = "/" + dataSnapshot.getKey() + "/" + snapshot.getKey();
                        Map<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put("person", data_post.getPerson() - 1);
                        mDatabase.child(path).updateChildren(taskMap);
                        MEMBERSquery_2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    mDatabase.child("post-members").child(snapshot1.getKey()).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                        MESSAGEquery_2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    mDatabase.child("post-message").child(snapshot1.getKey()).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        TAXI_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    mDatabase.child("taxi-call").child(snapshot1.getKey()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
