package app.taxi.newtaxi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class main_simple extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DatabaseReference mDatabase;
    private BackPressCloseHandler backPressCloseHandler;
    Button TabBarButton, webLinkButton, DirectionButton;
    TextView NameText, StartText,ArriveText,Header_NameText,Header_EmailText;
    ImageView ProfileView;
    Spinner MeterSpinner;

    String nickname,userid,profileURL,email,INDEX,START,ARRIVE;
    int METER = 500;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;

    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    void init(){
        mDatabase = FirebaseDatabase.getInstance().getReference();

        webLinkButton = findViewById(R.id.WebLinkButton);
        DirectionButton = findViewById(R.id.DirectionButton);
        NameText = findViewById(R.id.NameText);
        StartText = findViewById(R.id.StartText);
        ArriveText = findViewById(R.id.ArriveText);
        MeterSpinner = findViewById(R.id.MeterSpinner);

        arrayList = new ArrayList<>();
        arrayList.add("100m");
        arrayList.add("300m");
        arrayList.add("500m");
        arrayList.add("700m");
        arrayList.add("1,000m");

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,arrayList);
        MeterSpinner.setAdapter(arrayAdapter);
        MeterSpinner.setSelection(2);
        MeterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MeterSpinner.setSelection(i);
                switch(i) {
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
        INDEX = positionDATA.getString("INDEX","");
        START = positionDATA.getString("출발지","출발지를 입력해주세요.");
        ARRIVE = positionDATA.getString("도착지","도착지를 입력해주세요.");

        NameText.setText(nickname);
        StartText.setText(START);
        ArriveText.setText(ARRIVE);

    }
    void initLayout(){
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
    void click(){
        StartText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Map.class);
                intent.putExtra("POSITION","출발");
                startActivity(intent);
            }
        });
        ArriveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Map.class);
                intent.putExtra("POSITION","도착");
                startActivity(intent);
            }
        });
        DirectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//TODO : Join
                SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
                SharedPreferences.Editor editor = positionDATA.edit();

                Intent intent = new Intent(getApplicationContext(),Join.class);
                editor.putString("출발지",StartText.getText().toString());
                editor.putString("도착지",ArriveText.getText().toString());
                editor.apply();
                intent.putExtra("METER",METER);
                startActivity(intent);
            }
        });
        webLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//TODO : 웹 사이트 링크

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_simple_nav);

        init();
        initLayout();
        click();


        //TODO : 버튼 탭바 : https://ghj1001020.tistory.com/30
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

        if(id == R.id.My_Point){
            Intent intent = new Intent(getApplicationContext(),My.class);
            startActivity(intent);
        }
        if(id == R.id.My_Grade){

        }
        if(id == R.id.Notice){
            //TODO : 웹 링크 연결
        }
        if(id == R.id.Event){
            //TODO : 웹 링크 연결
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
//TODO : 즐겨찾기 기능