package app.taxi.newtaxi;

import android.app.Dialog;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
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

public class Post_Call extends AppCompatActivity {
    ArrayList<Data_message> list = new ArrayList<>();
    DatabaseReference mDatabaseMSG,mDatabase;
    ListView COMMENTlist;
    EditText COMMENTedit;
    String ID,INDEX,PROFILEURL;
    Date today = new Date();
    SimpleDateFormat timeNow = new SimpleDateFormat("a K:mm");
    String Time = timeNow.format(today);
    StringBuffer SB;
    Dialog dialog;
    MapView mMapView;
    TextView TIMEtext,PRICEtext,DISTANCEtext;
    Button JOINbutton,MAPbutton,COMMENTbutton;
    String SELECT_latitude = "37.566643",SELECT_longitude = "126.978279";
    void init(){
        SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();
        mDatabaseMSG = FirebaseDatabase.getInstance().getReference("post-message");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        COMMENTbutton = findViewById(R.id.COMMENTbutton);
        MAPbutton = findViewById(R.id.MAPbutton);
        COMMENTedit = findViewById(R.id.COMMENTedit);
        COMMENTlist = findViewById(R.id.MESSAGElist);

        ID = positionDATA.getString("ID","");
        INDEX = positionDATA.getString("INDEX","");
        PROFILEURL = positionDATA.getString("PROFILE","");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post__call);
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
                    mDatabaseMSG.push().setValue(new Data_message(INDEX,PROFILEURL, ID, SB.toString(), Time));
                    COMMENTedit.setText("");
                }
            }
        });
        Query query = mDatabaseMSG.orderByChild("index").equalTo(INDEX);
        Log.e("INDEX",INDEX);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Data_message data_message = dataSnapshot.getValue(Data_message.class);
                    list.add(data_message);
                    adapter.notifyDataSetChanged();
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
    }
    void DIALOG(){
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.show();
        mMapView = dialog.findViewById(R.id.MAP_Dialog);
        TIMEtext = dialog.findViewById(R.id.TIMEtext);
        PRICEtext = dialog.findViewById(R.id.PRICEtext);
        DISTANCEtext = dialog.findViewById(R.id.DISTANCEtext);
        JOINbutton = dialog.findViewById(R.id.JOINbutton);
        JOINbutton.setVisibility(View.INVISIBLE);

        Query query1 = mDatabase.child("post").orderByChild("index").equalTo(INDEX);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    Data_Post data_post = appleSnapshot.getValue(Data_Post.class);
                    TIMEtext.setText(data_post.getTime());
                    PRICEtext.setText(data_post.getPoint());
                    DISTANCEtext.setText(data_post.getDistance().split(":")[1]);
                    SELECT_latitude = data_post.getStart_Latitude();
                    SELECT_longitude = data_post.getStart_Longitude();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
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
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
        });
    }
}
