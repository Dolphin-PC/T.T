package app.taxi.newtaxi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Post_Call extends AppCompatActivity {
    ArrayList<Data_message> list = new ArrayList<>();
    DatabaseReference mDatabaseMSG,mDatabase;
    ListView COMMENTlist;
    EditText COMMENTedit;
    String USERNAME,INDEX,PROFILEURL,ID;
    Date today = new Date();
    SimpleDateFormat timeNow = new SimpleDateFormat("a K:mm");
    String Time = timeNow.format(today);
    StringBuffer SB;
    Dialog dialog;
    MapView mMapView;
    TextView TIMEtext,PRICEtext,DISTANCEtext;
    Button CALLbutton,MAPbutton,COMMENTbutton,JOINbutton;
    String SELECT_latitude = "37.566643",SELECT_longitude = "126.978279";
    AlertDialog.Builder OUTdialog,QUITdialog;
    void init(){
        SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();
        mDatabaseMSG = FirebaseDatabase.getInstance().getReference("post-message");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        COMMENTbutton = findViewById(R.id.COMMENTbutton);
        MAPbutton = findViewById(R.id.MAPbutton);
        COMMENTedit = findViewById(R.id.COMMENTedit);
        COMMENTlist = findViewById(R.id.MESSAGElist);

        USERNAME = positionDATA.getString("USERNAME","");
        INDEX = positionDATA.getString("INDEX","");
        PROFILEURL = positionDATA.getString("PROFILE","");
        ID = positionDATA.getString("ID","");
    }
    void click(){
        MAPbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DIALOG();
            }
        });
        COMMENTbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!COMMENTedit.getText().toString().equals("")){
                    SB = new StringBuffer(COMMENTedit.getText().toString());
                    if(SB.length() >= 15){
                        for(int i=1;i<SB.length()/15;i++){
                            SB.insert(15*i,"\n");
                        }
                    }
                    mDatabaseMSG.push().setValue(new Data_message(INDEX,PROFILEURL,ID, USERNAME, SB.toString(), Time));
                    COMMENTedit.setText("");
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_call);
        init();

        final ChatAdapter adapter = new ChatAdapter(getApplicationContext(), R.layout.comment_listview,list,ID);
        COMMENTlist.setAdapter(adapter);
        COMMENTedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(COMMENTedit.getText().toString().equals("")) {
                    COMMENTbutton.setVisibility(View.INVISIBLE);
                    MAPbutton.setVisibility(View.VISIBLE);
                } else {
                    COMMENTbutton.setVisibility(View.VISIBLE);
                    MAPbutton.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        click();

        Query query = mDatabaseMSG.orderByChild("index").equalTo(INDEX);
        Log.e("INDEX",INDEX);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Data_message data_message = dataSnapshot.getValue(Data_message.class);
                    list.add(data_message);
                    adapter.notifyDataSetChanged();
                    COMMENTlist.setSelection(adapter.getCount()-1);
                }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Intent intent = new Intent(getApplicationContext(),main.class);
                main m = main.mainActivity;
                QUIT_PROCESS_databaseDATA();
                QUIT_PROCESS_referenceDATA();
                QUITDIALOG(m);
                QUITdialog.show();
                startActivity(intent);
                finish();
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
    void DIALOG(){
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.map_dialog);
        dialog.show();
        TextView OUTtext = dialog.findViewById(R.id.OUTtext);
        mMapView = dialog.findViewById(R.id.MAP_Dialog);
        TIMEtext = dialog.findViewById(R.id.TIMEtext);
        PRICEtext = dialog.findViewById(R.id.PRICEtext);
        DISTANCEtext = dialog.findViewById(R.id.DISTANCEtext);
        JOINbutton = dialog.findViewById(R.id.JOINbutton);

        Query taxi_call_query = mDatabase.child("taxi-call").orderByChild("index").equalTo(INDEX);
        taxi_call_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildren().iterator().hasNext()) {
                    JOINbutton.setText("이미 택시를 호출했습니다.");
                    JOINbutton.setEnabled(false);
                }else{
                    JOINbutton.setText("택시 호출");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        JOINbutton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Query query = mDatabase.child("post").orderByChild("index").equalTo(INDEX);
                                              query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                  @Override
                                                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                      for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                          Data_Post data_post = snapshot.getValue(Data_Post.class);
                                                          Log.e("INDEX", data_post.getIndex());
                                                          mDatabase.child("taxi-call").push().setValue(data_post);
                                                          Toast.makeText(getApplicationContext(), "택시를 호출했습니다.", Toast.LENGTH_SHORT).show();
                                                          dialog.dismiss();
                                                      }
                                                  }
                                                  @Override
                                                  public void onCancelled(@NonNull DatabaseError databaseError) { }
                                              });
                                          }
                                      });
        Query query1 = mDatabase.child("post").orderByChild("index").equalTo(INDEX);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    Data_Post data_post = appleSnapshot.getValue(Data_Post.class);
                    TIMEtext.setText(data_post.getTime());
                    PRICEtext.setText(data_post.getPoint());
                    DISTANCEtext.setText(data_post.getDistance());
                    SELECT_latitude = data_post.getStart_Latitude();
                    SELECT_longitude = data_post.getStart_Longitude();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        try{
            Thread.sleep(500);     // DB에서 받아오는 시간 지연 -> 로딩(원돌아가는거)로 변경하기
        }catch (Exception e){
            e.printStackTrace();
        }
        MapsInitializer.initialize(this);

        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng latLng = new LatLng(Double.valueOf(SELECT_latitude),Double.valueOf(SELECT_longitude));
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
                OUTDIALOG();
                OUTdialog.show();
            }
        });
    }
    void OUTDIALOG(){
        OUTdialog = new AlertDialog.Builder(this);
        OUTdialog.setTitle("퇴장");
        if(INDEX == ID)
            OUTdialog.setMessage("노선에서 나가시겠습니까?\n(팀원 전체 퇴장됩니다.)");
        else
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
                Intent intent = new Intent(getApplicationContext(),main.class);
                startActivity(intent);
                finish();
            }
        });
    }
    void QUITDIALOG(Context context){
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
    void QUIT_PROCESS_databaseDATA(){
        final Query POSTquery = mDatabase.child("post").orderByChild("index").equalTo(INDEX);
        final Query MEMBERSquery_1 = mDatabase.child("post-members").orderByChild("index").equalTo(ID);  //방장이 나갔을때, post-members전체 삭제
        final Query MEMBERSquery_2 = mDatabase.child("post-members").orderByChild("userid").equalTo(ID); //참가인원이 나갔을 때,
        final Query MESSAGEquery_1 = mDatabase.child("post-message").orderByChild("index").equalTo(ID); //방장이 나갔을때, post-message전체 삭제
        final Query MESSAGEquery_2 = mDatabase.child("post-message").orderByChild("id").equalTo(ID);    //참가인원이 나갔을 때,
        Query TAXI_query = mDatabase.child("taxi-call").orderByChild("index").equalTo(ID);              //방장이 나갔을때, taxi-call 삭제(취소)
        POSTquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data_Post data_post = snapshot.getValue(Data_Post.class);
                    if (ID.equals(data_post.getIndex())){           //방장일 때, 방 전체 파기(post/post-members/post-message)
                        Log.d("post","방장일 때");
                        mDatabase.child("post").child(snapshot.getKey()).removeValue();
                        MEMBERSquery_1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                    mDatabase.child("post-members").child(snapshot1.getKey()).removeValue();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                        MESSAGEquery_1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                    mDatabase.child("post-message").child(snapshot1.getKey()).removeValue();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                    }
                    else{                                           //참가한 인원일경우, person-1, post-members 파기
                        Log.d("post","참가일 때");
                        String path = "/" + dataSnapshot.getKey() + "/" + snapshot.getKey();
                        Map<String,Object> taskMap = new HashMap<String,Object>();
                        taskMap.put("person",data_post.getPerson()-1);
                        mDatabase.child(path).updateChildren(taskMap);
                        MEMBERSquery_2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                    mDatabase.child("post-members").child(snapshot1.getKey()).removeValue();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                        MESSAGEquery_2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                    mDatabase.child("post-message").child(snapshot1.getKey()).removeValue();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        TAXI_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                    mDatabase.child("taxi-call").child(snapshot1.getKey()).removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}

