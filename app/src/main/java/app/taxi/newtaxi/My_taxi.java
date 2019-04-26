package app.taxi.newtaxi;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;
import kr.co.bootpay.listner.CancelListener;
import kr.co.bootpay.listner.CloseListener;
import kr.co.bootpay.listner.ConfirmListener;
import kr.co.bootpay.listner.DoneListener;
import kr.co.bootpay.listner.ErrorListener;
import kr.co.bootpay.listner.ReadyListener;
import kr.co.bootpay.model.BootUser;

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
    private String userID,title,start,arrive,driver,taxinumber,phonenumber;
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

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.MAPview);
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
        SharedPreferences.Editor editor = positionDATA.edit();

        Intent intent = getIntent();
        index = Integer.valueOf(intent.getExtras().getString("INDEX"));
        title = positionDATA.getString("TITLE","");
        arrive = positionDATA.getString("ARRIVE","");
        start = positionDATA.getString("START","");
        person = Integer.valueOf(positionDATA.getString("PERSON","1"));
        pay = point = Integer.valueOf(positionDATA.getString("POINT","1000"));
        userID = positionDATA.getString("USERNAME","");
        final int Max = Integer.valueOf(positionDATA.getString("MAX","3"));

        PayPerPerson = point / Max;
        INDEXtext.setText(String.valueOf(index) + "번");

        // Custom Adapter Instance 생성 및 ListView에 Adapter 지정
        final CustomAdapter adapter = new CustomAdapter();
        LISTview.setAdapter(adapter);

        mCommentsReference = mDatabase.child("post-members");
        mCommentsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Data_Members data_members = dataSnapshot.getValue(Data_Members.class);
                if (data_members.getINDEX() == index) {
                    adapter.addItem(data_members.getPROFILEURL(),data_members.getUSER1(),data_members.getGENDER());
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
        });     //사용자 정보 확인
        /*LISTview.addHeaderView();*/
        Query query = mDatabase.child("post-members").orderByChild("index").equalTo(index);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    Data_Members data_members = appleSnapshot.getValue(Data_Members.class);
                    adapter.addItem(data_members.getPROFILEURL(),data_members.getUSER1(),data_members.getGENDER());
                    return;
                }
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

    public void Addcmt(String str,int index){                 //시스템 댓글
        Data_Comment cmt = new Data_Comment(str,index);
        mCommentsReference.push().setValue(cmt);
    }
    public void AddComment(String userID,String comment,int index) {       //사용자 댓글
        Data_Comment dataComment = new Data_Comment(userID,comment,index);
        mCommentsReference.push().setValue(dataComment);
    }
    void PAY(int Price){
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
                        PAY2();
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
