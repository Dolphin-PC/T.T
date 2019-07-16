package app.taxi.newtaxi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class main_simple extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static main_simple main;
    private AdView mAdView, navigationAdView;

    private DatabaseReference mDatabase;
    Button TabBarButton, webLinkButton, DirectionButton;
    TextView NameText, StartText, ArriveText, Header_NameText, Header_EmailText;
    ImageView ProfileView;
    Spinner MeterSpinner;

    String nickname, userid, profileURL, email, INDEX, START, ARRIVE;
    String url = "https://web-taxi-1.firebaseapp.com";
    int METER = 500;
    AlertDialog.Builder alertDialogBuilder;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;

    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;

    void init() {
        main = main_simple.this;

        mDatabase = FirebaseDatabase.getInstance().getReference();

        webLinkButton = findViewById(R.id.WebLinkButton);
        DirectionButton = findViewById(R.id.DirectionButton);
        NameText = findViewById(R.id.NameText);
        StartText = findViewById(R.id.StartText);
        ArriveText = findViewById(R.id.ArriveText);
        MeterSpinner = findViewById(R.id.MeterSpinner);

        mAdView = findViewById(R.id.navigationAdView);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        arrayList = new ArrayList<>();
        arrayList.add("100m");
        arrayList.add("300m");
        arrayList.add("500m");
        arrayList.add("700m");
        arrayList.add("1,000m");

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayList);
        MeterSpinner.setAdapter(arrayAdapter);
        MeterSpinner.setSelection(2);
        MeterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MeterSpinner.setSelection(i);
                switch (i) {
                    case 0:
                        METER = 100;
                        break;
                    case 1:
                        METER = 300;
                        break;
                    case 2:
                        METER = 500;
                        break;
                    case 3:
                        METER = 700;
                        break;
                    case 4:
                        METER = 1000;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
        nickname = positionDATA.getString("USERNAME", "");
        userid = positionDATA.getString("ID", "");
        profileURL = positionDATA.getString("PROFILE", "");
        INDEX = positionDATA.getString("INDEX", "");
        START = positionDATA.getString("출발지", "출발지를 입력해주세요.");
        ARRIVE = positionDATA.getString("도착지", "도착지를 입력해주세요.");

        NameText.setText(nickname);
        StartText.setText(START);
        ArriveText.setText(ARRIVE);

    }

    void initLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.dl_main_drawer_root);
        navigationView = (NavigationView) findViewById(R.id.nv_main_navigation_root);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );

        drawerLayout.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.getHeaderView(0);

        ProfileView = view.findViewById(R.id.ProfileView);
        Header_NameText = view.findViewById(R.id.Header_NameText);
        Header_EmailText = view.findViewById(R.id.Header_EmailText);
        Glide.with(this)
                .load(profileURL)
                .into(ProfileView);
        Header_NameText.setText(nickname);
        Header_EmailText.setText(userid);
    }

    void click() {
        StartText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Map.class);
                intent.putExtra("POSITION", "출발");
                startActivity(intent);
            }
        });
        ArriveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Map.class);
                intent.putExtra("POSITION", "도착");
                startActivity(intent);
            }
        });
        DirectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//TODO : Join
                SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
                SharedPreferences.Editor editor = positionDATA.edit();
                String Start = positionDATA.getString("출발", "없음");
                String Arrive = positionDATA.getString("도착", "없음");

                if (Start.equals("없음") || Arrive.equals("없음")) {
                    Toast.makeText(getApplicationContext(), "출발지와 도착지를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), Join.class);
                    editor.putString("출발지", StartText.getText().toString());
                    editor.putString("도착지", ArriveText.getText().toString());
                    editor.apply();
                    intent.putExtra("METER", METER);
                    startActivity(intent);
                }
            }
        });
        webLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://web-taxi-1.firebaseapp.com"));
                startActivity(intent);
            }
        });

    }

    void check() {
        if(!INDEX.equals("")){
            Dialog(INDEX);
            alertDialogBuilder.show();
        }
        /*Query query = mDatabase.child("post-members").orderByChild("index").equalTo(INDEX);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data_Members data_members = snapshot.getValue(Data_Members.class);
                    if (data_members.getINDEX().equals(INDEX)) {
                        Dialog(INDEX);
                        alertDialogBuilder.show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    private void Dialog(final String INDEX) {
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("노선 확인");
        alertDialogBuilder.setMessage("참가 중인 노선이 있습니다.\n재입장 하시겠습니까?");
        alertDialogBuilder.setPositiveButton("재입장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), My_taxi_simple.class);
                intent.putExtra("INDEX", INDEX);
                startActivity(intent);
                finish();
            }
        }).setNegativeButton("나가기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_simple_nav);
        BackPressCloseHandler backPressCloseHandler = new BackPressCloseHandler(this);
        init();
        initLayout(); // 사이드 메뉴바
        click();

        try {
            Thread.sleep(1000); //나가자마자 재입장 알림 뜨는 것을 방지하기 위한
            check(); //참가하고 있는 노선이 있는지 확인.
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), advertisement.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // 탭 바 메뉴
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.My_Point) {
            Intent intent = new Intent(getApplicationContext(), Charge_simple.class);
            startActivity(intent);
        }
        if (id == R.id.My_Grade) {
            Toast.makeText(getApplicationContext(), "준비 중에 있습니다.", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.Notice) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
        if (id == R.id.Event) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        return true;
    }
}
//TODO : 즐겨찾기 기능