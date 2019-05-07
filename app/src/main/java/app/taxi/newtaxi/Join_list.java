package app.taxi.newtaxi;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Join_list extends AppCompatActivity {
    Join joinActivity = (Join)Join.JoinActivity;
    DatabaseReference mDatabase;
    ArrayList<String> MARKERlist = new ArrayList<String>();
    String MARKER_COUNT;
    ListView LIST;
    ArrayAdapter adapter;
    Dialog dialog;

    MapView mMapView;
    TextView TIMEtext,PRICEtext,DISTANCEtext;
    Button JOINbutton;
    String USERNAME,URL,GENDER,USERID;
    String SELECT_latitude = "37.566643",SELECT_longitude = "126.978279";
    void init(){
        SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();

        Intent intent = getIntent();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        MARKERlist = intent.getExtras().getStringArrayList("MARKER");
        MARKER_COUNT = intent.getExtras().getString("MARKER_COUNT");
        USERNAME = positionDATA.getString("USERNAME","");
        URL = positionDATA.getString("PROFILE","");
        USERID=positionDATA.getString("ID","");
        LIST = findViewById(R.id.MARKERlist);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_single_choice);
        LIST.setAdapter(adapter);
    }
    void click(){
        LIST.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DIALOG(i);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_list);
        init();
        click();
        Log.e("MARKERCOUNT",MARKERlist.size() + "," + MARKER_COUNT);

        for(int i=0; i<MARKERlist.size();i++){
            Query query = mDatabase.child("post").orderByChild("index").equalTo(MARKERlist.get(i));
            Log.e("MARKER",MARKERlist.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Data_Post data_post = snapshot.getValue(Data_Post.class);
                        adapter.add(data_post.getStart() + "->\n" + data_post.getArrive());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    void DIALOG(int i){
        final int index = i;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.map_dialog);
        dialog.show();
        mMapView = dialog.findViewById(R.id.MAP_Dialog);
        TIMEtext = dialog.findViewById(R.id.TIMEtext);
        PRICEtext = dialog.findViewById(R.id.PRICEtext);
        DISTANCEtext = dialog.findViewById(R.id.DISTANCEtext);
        JOINbutton = dialog.findViewById(R.id.JOINbutton);
        JOINbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Query query = mDatabase.child("post").orderByChild("index").equalTo(MARKERlist.get(index));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            Data_Post data_post = appleSnapshot.getValue(Data_Post.class);
                            SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
                            SharedPreferences.Editor editor = positionDATA.edit();
                            if(data_post.getPerson() < data_post.getMaxPerson()) {
                                String path = "/" + dataSnapshot.getKey() + "/" + appleSnapshot.getKey();
                                Map<String,Object> taskMap = new HashMap<String,Object>();
                                taskMap.put("person",data_post.getPerson()+1);
                                mDatabase.child(path).updateChildren(taskMap);
                                Data_Members data_members = new Data_Members(USERNAME,MARKERlist.get(index),URL,"남",USERID,false);
                                mDatabase.child("post-members").push().setValue(data_members);
                                editor.putString("INDEX",String.valueOf(index));
                                editor.putString("??",String.valueOf(index));
                                editor.apply();
                                Toast.makeText(getApplicationContext(),"참가 신청 완료!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),My_taxi.class);
                                intent.putExtra("INDEX",String.valueOf(index));
                                startActivity(intent);
                                joinActivity.finish();
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"인원이 초과되었습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        Query query1 = mDatabase.child("post").orderByChild("index").equalTo(MARKERlist.get(index));
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    Data_Post data_post = appleSnapshot.getValue(Data_Post.class);
                    TIMEtext.setText(data_post.getTime());
                    PRICEtext.setText(data_post.getPoint());
                    DISTANCEtext.setText(data_post.getDistance().split(":")[1]);
                    SELECT_latitude = data_post.getArrive_Latitude();
                    SELECT_longitude = data_post.getArrive_Longitude();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        try{
            Thread.sleep(1000);     // DB에서 받아오는 시간 지연 -> 로딩(원돌아가는거)로 변경하기
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
                        .title("도착 위치"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
        });
    }
}
