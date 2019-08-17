package app.taxi.newtaxi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Post_Call extends AppCompatActivity {
    ArrayList<Data_message> list = new ArrayList<>();
    DatabaseReference mDatabaseMSG, mDatabase, rDatabase;
    ListView COMMENTlist;
    EditText COMMENTedit;
    String USERNAME, INDEX, PROFILEURL, ID, Menu, Opinion;
    Date today = new Date();
    SimpleDateFormat timeNow = new SimpleDateFormat("a K:mm");
    SimpleDateFormat dayNow = new SimpleDateFormat("MM/dd");
    String Time = timeNow.format(today);
    String Day = dayNow.format(today);
    StringBuffer SB;
    Dialog dialog, Guide_Dialog, DeclarationDialog, MenuDialog, VoteDialog;
    MapView mMapView;
    TextView TIMEtext, PRICEtext, DISTANCEtext, GuideText, ConfirmText;
    Button CALLbutton, MAPbutton, COMMENTbutton, JOINbutton;
    String SELECT_latitude = "37.566643", SELECT_longitude = "126.978279";
    AlertDialog.Builder OUTdialog, QUITdialog,alertDialog;

    Boolean once, Guide,call_taxi;

    Spinner menuSpinner;
    View footer,header,taxiheader;

    void init() {
        SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();
        once = true;
        Guide = positionDATA.getBoolean("GUIDE", true);

        mDatabaseMSG = FirebaseDatabase.getInstance().getReference("post-message");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        rDatabase = FirebaseDatabase.getInstance("https://taxitogether.firebaseio.com/").getReference();


        COMMENTbutton = findViewById(R.id.COMMENTbutton);
        MAPbutton = findViewById(R.id.MAPbutton);
        COMMENTedit = findViewById(R.id.COMMENTedit);
        COMMENTlist = findViewById(R.id.MESSAGElist);
        GuideText = findViewById(R.id.GuideText);

        footer = getLayoutInflater().inflate(R.layout.footer_postcall_commentlist, null, false);
        header = getLayoutInflater().inflate(R.layout.header, null, false);
        taxiheader = getLayoutInflater().inflate(R.layout.taxiheader, null, false);
        footer.setClickable(false); // footer 클릭 안되게(클릭 되면 오류 뜸)
        header.setClickable(false); // footer 클릭 안되게(클릭 되면 오류 뜸)

        if (Guide) {
            Guide_Dialog();
        }
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.map_dialog);
        mMapView = dialog.findViewById(R.id.MAP_Dialog);
        TIMEtext = dialog.findViewById(R.id.TIMEtext);
        PRICEtext = dialog.findViewById(R.id.PRICEtext);
        DISTANCEtext = dialog.findViewById(R.id.DISTANCEtext);
        JOINbutton = dialog.findViewById(R.id.JOINbutton);
        JOINbutton.setText("택시 호출");

        USERNAME = positionDATA.getString("USERNAME", "");
        INDEX = positionDATA.getString("INDEX", "");
        PROFILEURL = positionDATA.getString("PROFILE", "");
        ID = positionDATA.getString("ID", "");

        alertDialog = new AlertDialog.Builder(this);
    }

    void init_database() {
        Query taxi_query = mDatabase.child("taxi-call").orderByChild("index").equalTo(INDEX);
        taxi_query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data_Taxi data_taxi = snapshot.getValue(Data_Taxi.class);
                    if (dataSnapshot.getChildren().iterator().hasNext()) {
                        COMMENTlist.addHeaderView(taxiheader,"택시를 호출했습니다.",false);
                        TextView systemText = taxiheader.findViewById(R.id.SystemText);
                        systemText.setText("택시를 호출했습니다.");
                        JOINbutton.setText("이미 택시를 호출했습니다.");
                        call_taxi=false;
                    }
                    if (!data_taxi.getDriver().equals("") && once) {
                        TextView systemText = taxiheader.findViewById(R.id.SystemText);
                        systemText.setText("택시가 호출되었습니다.");
                        JOINbutton.setText("택시 정보 보기");
                        Intent intent = new Intent(getApplicationContext(), Call_Info.class);
                        call_taxi = true;
                        once = false;
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    void click() {
        MAPbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DIALOG();
            }
        });
        COMMENTbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!COMMENTedit.getText().toString().equals("")) {
                    SB = new StringBuffer(COMMENTedit.getText().toString());
                    if (SB.length() >= 15) {
                        for (int i = 1; i < SB.length() / 15; i++) {
                            SB.insert(15 * i, "\n");
                        }
                    }
                    mDatabaseMSG.push().setValue(new Data_message(INDEX, PROFILEURL, ID, USERNAME, SB.toString(), Time));
                    rDatabase.child("messages").push().setValue(new Data_message(INDEX, PROFILEURL, ID, USERNAME, SB.toString(), Time));
                    COMMENTedit.setText("");
                }
            }
        });
        GuideText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Guide_Dialog();
            }
        });
        taxiheader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(call_taxi) {
                    Intent intent = new Intent(getApplicationContext(), Call_Info.class);
                    startActivity(intent);
                }
            }
        });
    }

    void Guide_Dialog() {
        Guide_Dialog = new Dialog(this);
        Guide_Dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Guide_Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Guide_Dialog.setContentView(R.layout.activity_chat_guide);
        Guide_Dialog.setCancelable(false);
        Guide_Dialog.show();

        ConfirmText = Guide_Dialog.findViewById(R.id.ConfirmText);
        TextView NeverText = Guide_Dialog.findViewById(R.id.NeverText);

        NeverText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
                SharedPreferences.Editor editor = positionDATA.edit();
                editor.putBoolean("GUIDE", false);
                editor.apply();
                Guide_Dialog.dismiss();
            }
        });
        ConfirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Guide_Dialog.dismiss();
            }
        });
    }       // 채팅 가이드 다이얼로그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_call);
        init();
        init_database();
        click();
        vote();
        vote_exit(); //투표에 의한 퇴장 처리

        final ChatAdapter adapter = new ChatAdapter(getApplicationContext(), R.layout.comment_listview, list, ID);
        COMMENTlist.setAdapter(adapter);
        COMMENTedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (COMMENTedit.getText().toString().equals("")) {
                    COMMENTbutton.setVisibility(View.INVISIBLE);
                    MAPbutton.setVisibility(View.VISIBLE);
                } else {
                    COMMENTbutton.setVisibility(View.VISIBLE);
                    MAPbutton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        COMMENTlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (!view.equals("")) {
                    MenuDialog(ChatAdapter.getUserID(position), ChatAdapter.getUserName(position));
                }
                return false;
            }
        });

        Query query = mDatabaseMSG.orderByChild("index").equalTo(INDEX);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Data_message data_message = dataSnapshot.getValue(Data_message.class);
                list.add(data_message);
                adapter.notifyDataSetChanged();
                COMMENTlist.setSelection(adapter.getCount() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Data_message data_message = dataSnapshot.getValue(Data_message.class);
                /*Intent intent = new Intent(getApplicationContext(), main.class);
                main m = main.mainActivity;
                QUIT_PROCESS_databaseDATA();
                QUIT_PROCESS_referenceDATA();
                QUITDIALOG(m);
                QUITdialog.show();
                startActivity(intent);
                finish();*/
                list.remove(data_message);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    void MenuDialog(final String UserID, final String USERNAME) {
        MenuDialog = new Dialog(this);
        MenuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MenuDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        MenuDialog.setContentView(R.layout.activity_postcall_menu_dialog);
        MenuDialog.show();

        Button VoteButton = MenuDialog.findViewById(R.id.VoteButton);
        Button DeclarationButton = MenuDialog.findViewById(R.id.DeclarationButton);
        Button CancelButton = MenuDialog.findViewById(R.id.CancelButton);

        VoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoteDialog(UserID, USERNAME);
                MenuDialog.dismiss();
            }
        });

        DeclarationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeclarationDialog(UserID, USERNAME);
                MenuDialog.dismiss();
            }
        });

        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuDialog.dismiss();
            }
        });
    }   //사용자를 길게 눌렀을 때, 표시될 메뉴 다이얼로그

    void VoteDialog(final String UserID, final String USERNAME) {
        VoteDialog = new Dialog(this);
        VoteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        VoteDialog.setContentView(R.layout.dialog_vote);
        VoteDialog.show();

        final TextView vote_NameText = VoteDialog.findViewById(R.id.NameText);
        final Spinner menuSpinner = VoteDialog.findViewById(R.id.voteMenuSpinner);
        final Button DeclarationButton = VoteDialog.findViewById(R.id.DeclarationButton);

        vote_NameText.setText(USERNAME);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("욕설");
        arrayList.add("개인정보 요구");
        arrayList.add("음란한 언행");
        arrayList.add("No Show(잠수)");
        arrayList.add("기타");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_dropdown_item, arrayList);

        menuSpinner.setAdapter(arrayAdapter);
        menuSpinner.setSelection(0);
        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                menuSpinner.setSelection(i);
                switch (i) {
                    case 0:
                        Menu = "욕설";
                        break;
                    case 1:
                        Menu = "개인정보 요구";
                        break;
                    case 2:
                        Menu = "음란한 언행";
                        break;
                    case 3:
                        Menu = "No show(잠수)";
                        break;
                    default:
                        Menu = "기타";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Menu = "기타";
            }
        });
        DeclarationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("day", Day);
                map.put("menu", Menu);
                map.put("reporter", ID);
                map.put("ID", UserID);
                map.put("name", USERNAME);
                map.put("yes", 0);
                map.put("no", 0);
                map.put("index", INDEX);
                mDatabase.child("exit").child(INDEX).updateChildren(map);
                Toast.makeText(getApplicationContext(), "'" + USERNAME + "' 의 강제 퇴장 투표가 진행됩니다.", Toast.LENGTH_SHORT).show();

                TextView voteNameText = footer.findViewById(R.id.voteNameText);
                TextView opinionText = footer.findViewById(R.id.opinionText);

                voteNameText.setText(USERNAME);
                opinionText.setText("사유 : " + Menu);
                VoteDialog.dismiss();
            }
        });

        final Button YesButton = footer.findViewById(R.id.vote_YesButton);
        final Button NoButton = footer.findViewById(R.id.vote_NoButton);
        final TextView completeText = footer.findViewById(R.id.completeText);

    }   //메뉴 다이얼로그 > 강제퇴장투표 다이얼로그

    void DeclarationDialog(final String UserID, final String USERNAME) {
        DeclarationDialog = new Dialog(this);
        DeclarationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DeclarationDialog.setContentView(R.layout.declaration_dialog);
        DeclarationDialog.show();
        final TextView NameText = DeclarationDialog.findViewById(R.id.NameText);
        menuSpinner = DeclarationDialog.findViewById(R.id.menu);
        final TextView OpinioinText = DeclarationDialog.findViewById(R.id.OpinionText);
        Button SubmitButton = DeclarationDialog.findViewById(R.id.SubmitButton);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("항목 선택");
        arrayList.add("욕설");
        arrayList.add("개인정보 요구");
        arrayList.add("음란한 언행");
        arrayList.add("기타");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_dropdown_item, arrayList);

        NameText.setText(USERNAME);
        menuSpinner.setAdapter(arrayAdapter);
        menuSpinner.setSelection(0);
        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                menuSpinner.setSelection(i);
                switch (i) {
                    case 1:
                        Menu = "욕설";
                        break;
                    case 2:
                        Menu = "개인정보 요구";
                        break;
                    case 3:
                        Menu = "음란한 언행";
                        break;
                    case 4:
                        Menu = "기타";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Menu = "기타";
            }
        });

        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Opinion = OpinioinText.getText().toString();
                HashMap<String, Object> map = new HashMap<>();
                map.put("day", Day);
                map.put("menu", Menu);
                map.put("opinion", Opinion);
                map.put("reporter", ID);
                map.put("ID", UserID);
                map.put("name", USERNAME);
                rDatabase.child("declaration").push().setValue(map);
                Toast.makeText(getApplicationContext(), "신고가 정상적으로 접수되었습니다.", Toast.LENGTH_SHORT).show();
                DeclarationDialog.dismiss();
            }
        });
    }       //메뉴 다이얼로그 -> 신고 다이얼로그

    void DIALOG() {
        dialog.show();
        TextView OUTtext = dialog.findViewById(R.id.OUTtext);

        Query taxi_call_query = mDatabase.child("taxi-call").orderByChild("index").equalTo(INDEX);
        taxi_call_query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data_Taxi data_taxi = snapshot.getValue(Data_Taxi.class);
                    if (dataSnapshot.getChildren().iterator().hasNext()) {
                        JOINbutton.setText("이미 택시를 호출했습니다.");
                        JOINbutton.setEnabled(true);
                    }
                    if (!data_taxi.getDriver().equals("")) {
                        JOINbutton.setText("택시 정보 보기");
                        JOINbutton.setEnabled(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        JOINbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JOINbutton.getText().toString().equals("이미 택시를 호출했습니다.")) {
                    JOINbutton.setEnabled(true);
                    alertDialog.setTitle("콜 취소").setMessage("택시 콜을 정말취소하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mDatabase.child("taxi-call").child(INDEX).removeValue();
                                }
                            }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
                } else if (JOINbutton.getText().toString().equals("택시 정보 보기")) {
                    Intent intent = new Intent(getApplicationContext(), Call_Info.class);
                    startActivity(intent);
                } else if (JOINbutton.getText().toString().equals("택시 호출")) {
                    Query query = mDatabase.child("post").orderByChild("index").equalTo(INDEX);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Data_Post data_post = snapshot.getValue(Data_Post.class);
                                Call_Dialog(data_post);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
        Query query1 = mDatabase.child("post").orderByChild("index").equalTo(INDEX);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    Data_Post data_post = appleSnapshot.getValue(Data_Post.class);
                    TIMEtext.setText(data_post.getTime().split(" ")[0] + "시 " +
                            data_post.getTime().split(" ")[2] + "분");
                    PRICEtext.setText(data_post.getPay() + " P");
                    DISTANCEtext.setText(data_post.getDistance());
                    SELECT_latitude = data_post.getStart_Latitude();
                    SELECT_longitude = data_post.getStart_Longitude();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        try {
            Thread.sleep(500);     // DB에서 받아오는 시간 지연 -> 로딩(원돌아가는거)로 변경하기
        } catch (Exception e) {
            e.printStackTrace();
        }
        MapsInitializer.initialize(this);

        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng latLng = new LatLng(Double.valueOf(SELECT_latitude), Double.valueOf(SELECT_longitude));
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("출발 위치"));
                CameraPosition position = new CameraPosition.Builder().target(latLng).zoom(15).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
                googleMap.getUiSettings().setZoomControlsEnabled(true);
            }
        });
        OUTtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                /*OUTDIALOG();
                OUTdialog.show();*/
            }
        });
    }       //map 다이얼로그

    void OUTDIALOG() {
        OUTdialog = new AlertDialog.Builder(this);
        OUTdialog.setTitle("퇴장");
        OUTdialog.setMessage("노선에서 나가시겠습니까?");
        OUTdialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDestroy();
            }
        });
        OUTdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                QUIT_PROCESS_databaseDATA();
                QUIT_PROCESS_referenceDATA();
                Intent intent = new Intent(getApplicationContext(), main.class);
                startActivity(intent);
                finish();
            }
        });
    }   //퇴장 다이얼로그

    void QUITDIALOG(Context context) {
        QUITdialog = new AlertDialog.Builder(context);
        QUITdialog.setTitle("퇴장");
        QUITdialog.setMessage("방장님이 퇴장하여,\n전체퇴장 처리되었습니다.");
        QUITdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDestroy();
            }
        });
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
        /*final Query MEMBERSquery_1 = mDatabase.child("post-members").orderByChild("index").equalTo(INDEX);  //방장이 나갔을때, post-members전체 삭제*/
        /*final Query MESSAGEquery_1 = mDatabase.child("post-message").orderByChild("index").equalTo(INDEX); //방장이 나갔을때, post-message전체 삭제*/

        final Query MESSAGEquery_2 = mDatabase.child("post-message").orderByChild("id").equalTo(ID);    //참가인원이 나갔을 때,

        mDatabase.child("post").child(INDEX).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Data_Post data_post = dataSnapshot.getValue(Data_Post.class);

                int person = data_post.getPerson() - 1;
                if (person == 0) {
                    mDatabase.child("post").child(INDEX).removeValue();
                } else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("person", person);
                    mDatabase.child("post").child(INDEX).updateChildren(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "참가 중인 노선이 없습니다.", Toast.LENGTH_SHORT).show();
                QUIT_PROCESS_referenceDATA();
            }
        });

        mDatabase.child("post-members").child(ID).removeValue();

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

        mDatabase.child("taxi-call").child(INDEX).removeValue();
    }

    void vote() {
        // 투표 시, 다른 사용자들에게 뿌려짐
        SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
        final int person = positionDATA.getInt("PERSON", 4);

        TextView TitleText = footer.findViewById(R.id.TitleText);
        TextView voteNameText = footer.findViewById(R.id.voteNameText);
        TextView opinionText = footer.findViewById(R.id.opinionText);
        final Button YesButton = footer.findViewById(R.id.vote_YesButton);
        final Button NoButton = footer.findViewById(R.id.vote_NoButton);
        final TextView completeText = footer.findViewById(R.id.completeText);
        TitleText.setVisibility(View.INVISIBLE);
        voteNameText.setVisibility(View.INVISIBLE);
        opinionText.setVisibility(View.INVISIBLE);
        YesButton.setVisibility(View.INVISIBLE);
        NoButton.setVisibility(View.INVISIBLE);

        mDatabase.child("exit").child(INDEX).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildren().iterator().hasNext()) {
                    COMMENTlist.addFooterView(footer);

                    Data_exit data_exit = dataSnapshot.getValue(Data_exit.class);
                    voteNameText.setText(data_exit.getName());
                    opinionText.setText("사유 : " + data_exit.getMenu());
                    TitleText.setVisibility(View.VISIBLE);
                    voteNameText.setVisibility(View.VISIBLE);
                    opinionText.setVisibility(View.VISIBLE);
                    YesButton.setVisibility(View.VISIBLE);
                    NoButton.setVisibility(View.VISIBLE);

                    if (Integer.valueOf(data_exit.get_yes() + data_exit.getNo()) == (person - 1)) {
                        if (data_exit.get_yes() >= data_exit.getNo()) {
                            voteNameText.setVisibility(View.INVISIBLE);
                            YesButton.setVisibility(View.INVISIBLE);
                            NoButton.setVisibility(View.INVISIBLE);
                            opinionText.setVisibility(View.INVISIBLE);
                            completeText.setText(data_exit.getName() + " 님이 강제 퇴장되었습니다.");
                            mDatabase.child("post").child(INDEX).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Data_Post data_post = dataSnapshot.getValue(Data_Post.class);
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("person", data_post.getPerson() - 1);
                                    mDatabase.child("post").child(INDEX).updateChildren(map);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            String exit_id = data_exit.getID();
                            mDatabase.child("post-members").child(exit_id).removeValue();

                            mDatabase.child("exit").child(INDEX).removeValue();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put(INDEX, data_exit);
                            rDatabase.child("exit").push().setValue(map);
                        } else {
                            voteNameText.setVisibility(View.INVISIBLE);
                            opinionText.setVisibility(View.INVISIBLE);
                            YesButton.setVisibility(View.INVISIBLE);
                            NoButton.setVisibility(View.INVISIBLE);
                            completeText.setText(data_exit.getName() + " 님의 퇴장이 거절되었습니다.");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        YesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("exit").child(INDEX).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Data_exit data_exit = dataSnapshot.getValue(Data_exit.class);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("yes", Integer.valueOf(data_exit.get_yes()) + 1);
                        mDatabase.child("exit").child(INDEX).updateChildren(map);
                        YesButton.setVisibility(View.INVISIBLE);
                        NoButton.setVisibility(View.INVISIBLE);
                        completeText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        NoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("exit").child(INDEX).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Data_exit data_exit = dataSnapshot.getValue(Data_exit.class);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("no", data_exit.getNo() + 1);
                        mDatabase.child("exit").child(INDEX).updateChildren(map);
                        YesButton.setVisibility(View.INVISIBLE);
                        NoButton.setVisibility(View.INVISIBLE);
                        completeText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        mDatabase.child("taxi-call").child(INDEX).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildren().iterator().hasNext()){
                    Data_Taxi data_taxi = dataSnapshot.getValue(Data_Taxi.class);
                    if(data_taxi.getCall() == false && data_taxi.getPerson() != 1){
                        COMMENTlist.addHeaderView(header);
                        Button call_yesButton = header.findViewById(R.id.call_yesButton);
                        Button call_noButton = header.findViewById(R.id.call_noButton);
                        HashMap<String,Object> map = new HashMap<>();
                        call_yesButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                map.put("yes",data_taxi.getYes()+1);
                                mDatabase.child("taxi-call").child(INDEX).updateChildren(map);
                                if(data_taxi.getYes() == data_taxi.getPerson()){
                                    map.clear();
                                    map.put("call",true);
                                    mDatabase.child("taxi-call").child(INDEX).updateChildren(map);
                                }else if(data_taxi.getNo() >= 1){
                                    Toast.makeText(getApplicationContext(),"택시 콜이 투표로 인해\n거절 되었습니다.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        call_noButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                map.put("no",data_taxi.getNo()+1);
                                mDatabase.child("taxi-call").child(INDEX).updateChildren(map);
                                if(data_taxi.getYes() == data_taxi.getPerson()){
                                    map.clear();
                                    map.put("call",true);
                                    mDatabase.child("taxi-call").child(INDEX).updateChildren(map);
                                }else if(data_taxi.getNo() >= 1){
                                    Toast.makeText(getApplicationContext(),"택시 콜이 투표로 인해\n거절 되었습니다.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        COMMENTlist.removeHeaderView(header);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }   //퇴장, 택시 호출 투표

    void Call_Dialog(Data_Post data_post){
        AlertDialog.Builder call = new AlertDialog.Builder(this);
        call.setTitle("택시 호출");
        if(data_post.getPerson() == 1){
            call.setMessage("일반 콜로 택시를 호출하시겠습니까?\n(서비스 이용료가 계산되지 않습니다.)").setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    HashMap map = new HashMap<String, Object>();
                    map.put(INDEX, data_post);
                    mDatabase.child("taxi-call").updateChildren(map);
                    rDatabase.child("taxi-call").updateChildren(map);
                    map.put("complete_driver", false);
                    map.put("complete_client", false);
                    map.put("complete_ride", false);
                    mDatabase.child("taxi-call").updateChildren(map);
                    rDatabase.child("taxi-call").child(INDEX).push().setValue(map);
                    Toast.makeText(getApplicationContext(), "택시를 호출했습니다.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }else{
            call.setMessage("현재 인원으로 동승 택시 호출을 진행합니다.\n(동승자와 투표를 통해 호출이 됩니다.)").setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    HashMap<String,Object> map = new HashMap<>();
                    map.put(INDEX,data_post);
                    mDatabase.child("taxi-call").updateChildren(map);
                    map.clear();
                    map.put("call",false);
                    map.put("yes",0);
                    map.put("no",0);
                    mDatabase.child("taxi-call").child(INDEX).updateChildren(map);
                }
            }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

        }
        call.show();
    }
    void vote_exit() {
        mDatabase.child("post-members").child(ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Intent intent = new Intent(getApplicationContext(), main_simple.class);
                startActivity(intent);
                QUIT_PROCESS_databaseDATA();
                QUIT_PROCESS_referenceDATA();
                finish();
            }
        });
    } //강제 퇴장 투표에 의한 퇴장 처리
}


//TODO : 택시 호출 버튼 늘리고, 반투명한(설명글) Dialog 표시