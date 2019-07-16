package app.taxi.newtaxi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class My_taxi_simple extends AppCompatActivity {
    private DatabaseReference mDatabase;

    TextView PersonText, TimeText;
    ImageView QuitButton, InfoButton, ChattingButton, RecruitImage;
    View View1, View2;

    String PERSON, TIME, INDEX, DISTANCE;
    int PAY, MY_POINT, MAX;

    AlertDialog.Builder alertDialogBuilder;
    Dialog dialog;
    My_taxiAdapter adapter;
    Query MEMBERSquery;
    private ListView LISTview, MYDIALOGlist;
    private TextView INDEXtext, TIMEtext, PRICEtext, DISTANCEtext, PERSONtext;
    Button OUTbutton, PAYbutton;

    void init() {
        SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();

        INDEX = positionDATA.getString("ID", "");

        PersonText = findViewById(R.id.PayperText);
        TimeText = findViewById(R.id.TimeText);
        QuitButton = findViewById(R.id.QuitButton);
        InfoButton = findViewById(R.id.InfoButton);
        ChattingButton = findViewById(R.id.ChattingButton);
        View1 = findViewById(R.id.View1);
        View2 = findViewById(R.id.View2);
        RecruitImage = findViewById(R.id.RecruitImage);
        Glide.with(this).load(R.drawable.recruiting).into(RecruitImage);
        init_Database();

        dialog = new Dialog(this);
        adapter = new My_taxiAdapter();

        View1.setBackgroundColor(Color.parseColor("#000000"));
        View2.setBackgroundColor(Color.parseColor("#000000"));
    }

    void init_Database() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = mDatabase.child("post").orderByChild("index").equalTo(INDEX);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data_Post data_post = snapshot.getValue(Data_Post.class);
                    PersonText.setText(data_post.getPerson() + " / " + data_post.getMaxPerson());
                    TimeText.setText(data_post.getTime());
                    PAY = data_post.getPay();
                    DISTANCE = data_post.getDistance();
                    MAX = data_post.getMaxPerson();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        MEMBERSquery = mDatabase.child("post-members").orderByChild("userid").equalTo(INDEX);
        MEMBERSquery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Data_Members data_members = dataSnapshot.getValue(Data_Members.class);
                adapter.addItem(data_members.getPROFILEURL(), data_members.getUSER1(), data_members.getGENDER());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    void click() {
        QuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog("퇴장", "노선에서 이탈하시겠습니까?");
                alertDialogBuilder.show();
            }
        });//TODO : 방 만들었던 내역, 방 취소 내역 만들기(분석용)
        InfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.my_taxi_dialog);
                dialog.show();
                MYDIALOGlist = dialog.findViewById(R.id.MYDIALOGlist);
                TIMEtext = dialog.findViewById(R.id.TIMEtext);
                PRICEtext = dialog.findViewById(R.id.PRICEtext);
                DISTANCEtext = dialog.findViewById(R.id.DISTANCEtext);
                PERSONtext = dialog.findViewById(R.id.PERSONtext);
                OUTbutton = dialog.findViewById(R.id.OUTbutton);
                PAYbutton = dialog.findViewById(R.id.PAYbutton);

                MEMBERSquery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Data_Members data_members = snapshot.getValue(Data_Members.class);
                            if (data_members.getJOIN())
                                PAYbutton.setText("채팅 창으로");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                MYDIALOGlist.setAdapter(adapter);
                TIMEtext.setText(TimeText.getText().toString());
                PRICEtext.setText(PAY + "원");
                DISTANCEtext.setText(DISTANCE);
                PERSONtext.setText(PersonText.getText().toString());

                OUTbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Dialog("퇴장", "노선에서 이탈하시겠습니까?");
                        alertDialogBuilder.show();
                    }
                });

            }
        });
        ChattingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query = mDatabase.child("post-members").orderByChild("index").equalTo(INDEX);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Data_Members data_members = snapshot.getValue(Data_Members.class);
                            if (data_members.getJOIN()) {
                                Intent intent = new Intent(getApplicationContext(), Post_Call.class);
                                startActivity(intent);
                            } else {
                                Dialog("결제필요", "결제 후, 채팅이 가능합니다.\n결제를 진행하시겠습니까?");
                                alertDialogBuilder.show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void Dialog(String Title, final String Message) {
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder.setMessage(Message);
        if (Title.equals("퇴장")) {
            alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), main_simple.class);
                    QUIT_PROCESS_database();
                    QUIT_PROCESS_reference();
                    startActivity(intent);
                    finish();
                }
            });
        } else if (Title.equals("결제확인")) {
            alertDialogBuilder.setPositiveButton("결제", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    final Query query = mDatabase.child("user").orderByChild("email").equalTo(INDEX);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                String path = "/" + dataSnapshot.getKey() + "/" + snapshot.getKey();
                                java.util.Map<String, Object> POINTmap = new HashMap<String, Object>();
                                if (user.getPoint() - PAY < 0) {
                                    Dialog("포인트 부족", "포인트를 충전하러 가시겠습니까?");
                                    alertDialogBuilder.show();
                                } else {
                                    POINTmap.put("point", user.getPoint() - PAY);
                                    mDatabase.child(path).updateChildren(POINTmap);
                                    Query query1 = mDatabase.child("post-members").orderByChild("index").equalTo(INDEX);     //INDEX를 통해 JOIN을 바꾸면, 전체가 바뀜(ID를 통해 접근을 해서 해당 ID만 바꾸기)
                                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                Data_Members data_members = snapshot.getValue(Data_Members.class);
                                                String path = "/" + dataSnapshot.getKey() + "/" + snapshot.getKey();
                                                Map<String, Object> JOINmap = new HashMap<String, Object>();
                                                JOINmap.put("join", true);
                                                mDatabase.child(path).updateChildren(JOINmap);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                                    });
                                    Intent intent = new Intent(getApplicationContext(), Post_Call.class);
                                    intent.putExtra("INDEX", INDEX);
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), PAY + " P가 결제되었습니다.", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            });
        } else if (Title.equals("포인트 부족")) {
            alertDialogBuilder.setPositiveButton("충전", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(), Charge_simple.class);
                    startActivity(intent);
                }
            });
        } else if (Title.equals("결제필요")) {
            alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Dialog("결제확인", PAY + " P를 결제하시겠습니까?");
                    alertDialogBuilder.show();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_taxi_simple);
        init();
        click();
        BackPressCloseHandler backPressCloseHandler = new BackPressCloseHandler(this);

    }

    void InfoDialog_init() {
        LISTview = findViewById(R.id.LISTview);

        adapter = new My_taxiAdapter();
        LISTview.setAdapter(adapter);

        Query query = mDatabase.child("post-members").orderByChild("index").equalTo(INDEX);
        final Query query1 = mDatabase.child("post").orderByChild("index").equalTo(INDEX);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Data_Members data_members = dataSnapshot.getValue(Data_Members.class);
                adapter.addItem(data_members.getPROFILEURL(), data_members.getUSER1(), data_members.getGENDER());
                adapter.notifyDataSetChanged();
                query1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            Data_Post data_post = appleSnapshot.getValue(Data_Post.class);
                            Log.e("COUNT", adapter.getCount() + "");
                            if (adapter.getCount() == data_post.getMaxPerson()) {
                                Dialog("결제", PAY + "P 결제하시겠습니까?");
                                //TODO : 다른 방식으로 결제하기 알리기(Toast, Background Message)
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Intent QUITintent = new Intent(getApplicationContext(), main.class);
                QUITintent.putExtra("MESSAGE", "방장이 퇴장하여 퇴장처리 되었습니다.");
                QUIT_PROCESS_reference();
                startActivity(QUITintent);
                finish();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        if (MAX == adapter.getCount())
            dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), advertisement.class);
        startActivity(intent);
    }

    void QUIT_PROCESS_reference() {
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

    void QUIT_PROCESS_database() {
        final Query POSTquery = mDatabase.child("post").orderByChild("index").equalTo(INDEX);
        final Query MEMBERSquery_1 = mDatabase.child("post-members").orderByChild("index").equalTo(INDEX);  //방장이 나갔을때, post-members전체 삭제
        final Query MEMBERSquery_2 = mDatabase.child("post-members").orderByChild("userid").equalTo(INDEX); //참가인원이 나갔을 때,
        final Query MESSAGEquery_1 = mDatabase.child("post-message").orderByChild("index").equalTo(INDEX); //방장이 나갔을때, post-message전체 삭제
        final Query MESSAGEquery_2 = mDatabase.child("post-message").orderByChild("id").equalTo(INDEX);    //참가인원이 나갔을 때,
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
    }
}
