package app.taxi.newtaxi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
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
//TODO : 1번째 상세정보 클릭시, 게시판의 정보와 결제 버튼 구현(다이얼로그)
// 나가기 버튼(방장은 나갈때, 다이얼로그 표시)
//TODO : Adapter 문제 해결하기(초기화 문제), 됐다 안됐다 함.
//TODO : 지도안에, 출발지와 도착지가 표시되도록
//TODO : 사용자 상세정보 클릭시, 사용자 정보 표시(다이얼로그)


public class My_taxi extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnCameraIdleListener,GoogleMap.OnCameraMoveListener {
    private DatabaseReference mDatabase;
    private TextView INDEXtext,TIMEtext;
    private GoogleMap MAPview;
    GoogleApiClient googleApiClient;
    Marker marker;
    private static int DEFAULT_ZOOM = 15; //0~21 level
    private ListView LISTview;
    private ArrayAdapter mAdapter;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    final private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private DatabaseReference mPostReference;
    private DatabaseReference mCommentsReference;
    private String userID,title,start,arrive,driver,taxinumber,phonenumber,ID;
    private int index,pay,person,point,PayPerPerson;
    long now = System.currentTimeMillis ();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("MM/DD");
    String Time = sdfNow.format(date);
    int MONTH = Integer.parseInt(Time.split("/")[0]);
    int DAY = Integer.parseInt(Time.split("/")[1]);
    private final int stuck = 10;
    LatLng STARTlatlng,ARRIVElatlng;
    AlertDialog.Builder alertDialogBuilder;

    void init(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();
        String start_lati = positionDATA.getString("출발","").split(",")[0];
        String start_long = positionDATA.getString("출발","").split(",")[1];
        STARTlatlng = new LatLng(Double.valueOf(start_lati),Double.valueOf(start_long));

        String arrive_lati=positionDATA.getString("도착","").split(",")[0];
        String arrive_long=positionDATA.getString("도착","").split(",")[1];
        ARRIVElatlng = new LatLng(Double.valueOf(arrive_lati),Double.valueOf(arrive_long));

        INDEXtext = findViewById(R.id.indexView);
        TIMEtext = findViewById(R.id.TIMEtext);
        LISTview = findViewById(R.id.LISTview);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.MY_MAP);
        mapFragment.getMapAsync(this);

        String time = positionDATA.getString("TIME","");
        if(!time.isEmpty())
            TIMEtext.setText(time);
        else
            TIMEtext.setText("예약 없음");
    }
    void click(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_taxi);

        init();

        click();
        SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        final SharedPreferences.Editor editor = positionDATA.edit();

        Intent intent = getIntent();
        index = Integer.valueOf(intent.getExtras().getString("INDEX"));
        title = positionDATA.getString("TITLE","");
        arrive = positionDATA.getString("ARRIVE","");
        start = positionDATA.getString("START","");
        person = Integer.valueOf(positionDATA.getString("PERSON","1"));
        pay = point = Integer.valueOf(positionDATA.getString("POINT","1000"));
        userID = positionDATA.getString("USERNAME","");
        ID = positionDATA.getString("ID","");

        final int Max = Integer.valueOf(positionDATA.getString("MAX","3"));

        PayPerPerson = point / Max;
        INDEXtext.setText(String.valueOf(index) + " 번");

        // Custom Adapter Instance 생성 및 ListView에 Adapter 지정
        final My_taxiAdapter adapter = new My_taxiAdapter();
        LISTview.setAdapter(adapter);

        adapter.addItem("","",""); //1번째가 추가가 안되있으면 표시가 안됨.

        /*LISTview.addHeaderView();*/
        Query query = mDatabase.child("post-members").orderByChild("index").equalTo(String.valueOf(index));
        final Query query1 = mDatabase.child("post").orderByChild("index").equalTo(String.valueOf(index));

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    Data_Members data_members = appleSnapshot.getValue(Data_Members.class);
                    adapter.addItem(data_members.getPROFILEURL(),data_members.getUSER1(),data_members.getGENDER());
                    if(data_members.getJOIN()){
                        Intent intent1 = new Intent(getApplicationContext(),Post_Call.class);
                        intent1.putExtra("INDEX",index);
                        startActivity(intent1);
                        finish();
                    }
                    else{
                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                    Data_Post data_post = appleSnapshot.getValue(Data_Post.class);
                                    if (adapter.getCount() - 1 == data_post.getMaxPerson()){
                                        Dialog(data_post.getPay()/data_post.getMaxPerson());
                                        alertDialogBuilder.show();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                    }
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
/*
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        commentList.setAdapter(mAdapter);

        commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String call_taxi = (String)parent.getAdapter().getItem(position);
                if(call_taxi.equals("택시가 호출되었습니다. 클릭해서 확인")){
                    Intent intent1 = new Intent(getApplicationContext(),Taxi_info.class);
                    intent1.putExtra("userid",userID);
                    startActivity(intent1);
                }
            }
        });
        mCommentsReference = mDatabase.child("post-comments");
        mCommentsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Data_Comment dataComment = dataSnapshot.getValue(Data_Comment.class);
                if (dataComment.getIndex() == index) {
                    mAdapter.add(dataComment.getuserID() + " : " + dataComment.getComment());
                }
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
        mPostReference = mDatabase.child("post");
        mPostReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Data_Post dataPost = dataSnapshot.getValue(Data_Post.class);
                if (!dataPost.getDriver().equals("")) {
                    mAdapter.add("택시가 호출되었습니다. 클릭해서 확인");
                }
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
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!commentText.getText().toString().isEmpty()){
                    AddComment(userID, commentText.getText().toString(), index);
                }
                commentText.setText("");
            }
        });*/

    }
    private void Dialog(final int Point) {
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("결제 확인");
        alertDialogBuilder.setMessage(Point + "P : 결제 하시겠습니까?\n(결제 후, 택시가 호출됩니다.)");
        alertDialogBuilder.setPositiveButton("결제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Query query = mDatabase.child("user").orderByChild("email").equalTo(ID);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            User user = snapshot.getValue(User.class);
                            String path = "/" + dataSnapshot.getKey() + "/" + snapshot.getKey();
                            Map<String,Object> POINTmap = new HashMap<String,Object>();
                            if(user.getPoint()-Point < 0){
                                //TODO:포인트가 부족합니다. 충전하러 가시겠습니까? ' 다이얼로그 후, 충전 액티비티로
                                Intent intent = new Intent(getApplicationContext(),Charge.class);
                                startActivity(intent);
                            }
                            else {
                                POINTmap.put("point", user.getPoint() - Point);
                                mDatabase.child(path).updateChildren(POINTmap);
                                Query query1 = mDatabase.child("post-members").orderByChild("index").equalTo(INDEXtext.getText().toString().split(" ")[0]);
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
                                Intent intent = new Intent(getApplicationContext(),Post_Call.class);
                                intent.putExtra("INDEX",index);
                                startActivity(intent);
                                finish();
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
    public void Addcmt(String str,int index){                 //시스템 댓글
        Data_Comment cmt = new Data_Comment(str,index);
        mCommentsReference.push().setValue(cmt);
    }
    public void AddComment(String userID,String comment,int index) {       //사용자 댓글
        Data_Comment dataComment = new Data_Comment(userID,comment,index);
        mCommentsReference.push().setValue(dataComment);
    }
    void PAY2(){
        Query query5 = databaseReference.child("post").orderByChild("index").equalTo(index);
        query5.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                Data_Post dataPost = nodeDataSnapshot.getValue(Data_Post.class);
                pay = dataPost.getPay() - PayPerPerson;
                String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                String path = "/" + dataSnapshot.getKey() + "/" + key;
                HashMap<String, Object> result = new HashMap<>();
                result.put("pay", pay);
                databaseReference.child(path).updateChildren(result);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        Query query1 = databaseReference.child("user").orderByChild("username").equalTo(userID);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                User userData = nodeDataSnapshot.getValue(User.class);
                int pay1;
                if(Integer.valueOf(userData.getPoint())-PayPerPerson < 0){
                    Toast.makeText(getApplicationContext(),"포인트가 부족합니다.",Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    pay1 = Integer.valueOf(userData.getPoint())-PayPerPerson;
                }
                String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                String path = "/" + dataSnapshot.getKey() + "/" + key;
                HashMap<String, Object> result = new HashMap<>();
                result.put("point", pay1);
                databaseReference.child(path).updateChildren(result);

                String str = userID + "님이 " + PayPerPerson + "원을 지불하셨습니다.(" + pay + "원 남음)";
                AddComment("system",str,index);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MAPview = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(STARTlatlng,DEFAULT_ZOOM));
        marker = googleMap.addMarker(new MarkerOptions().position(STARTlatlng).title("출발 위치").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        marker = googleMap.addMarker(new MarkerOptions().position(ARRIVElatlng).title("도착 위치").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

}
