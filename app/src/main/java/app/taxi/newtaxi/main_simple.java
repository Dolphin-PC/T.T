package app.taxi.newtaxi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import java.util.HashMap;

public class main_simple extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static main_simple main;
    private AdView mAdView, navigationAdView;

    private DatabaseReference mDatabase, rDatabase;
    Button TabBarButton, webLinkButton, DirectionButton;
    TextView NameText, StartText, ArriveText, Header_NameText, Header_EmailText;
    ImageView ProfileView;
    Spinner MeterSpinner;

    String nickname, ID, profileURL, email, INDEX, START, ARRIVE;
    String url = "https://web-taxi-1.firebaseapp.com";
    int METER = 500;
    AlertDialog.Builder alertDialogBuilder;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;

    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;

    Boolean Guide_show;

    void init() {
        SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
        final SharedPreferences.Editor editor = positionDATA.edit();

        main = main_simple.this;
        rDatabase = FirebaseDatabase.getInstance("https://taxitogether.firebaseio.com/").getReference();
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
        nickname = positionDATA.getString("USERNAME", "");
        ID = positionDATA.getString("ID", "");
        profileURL = positionDATA.getString("PROFILE", "");
        INDEX = positionDATA.getString("INDEX", "");
        START = positionDATA.getString("출발지", "출발지를 입력해주세요.");
        ARRIVE = positionDATA.getString("도착지", "도착지를 입력해주세요.");
        if (!positionDATA.getBoolean("Guide", false)) { // 가이드 확인 안했을때
            Guide_show = positionDATA.getBoolean("NEVER", false);
            if (Guide_show.equals(false))
                Guide();
        }

        mDatabase.child("user").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                editor.putInt("POINT", user.getPoint());
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "포인트 오류가 발생하였습니다.\n포인트를 확인해주세요.", Toast.LENGTH_SHORT).show();
                editor.putInt("POINT", 0);
                editor.apply();
            }
        });

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
        if (!profileURL.equals("")) {
            Glide.with(this)
                    .load(profileURL)
                    .into(ProfileView);
        } else {
            Glide.with(this)
                    .load(R.drawable.default_profile)
                    .into(ProfileView);
        }
        Header_NameText.setText(nickname);
        Header_EmailText.setText(ID);
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
                /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://web-taxi-1.firebaseapp.com"));
                startActivity(intent);*/
                Guide();
            }
        });

    }

    void check() {
        mDatabase.child("user").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getPenalty() >= 10){
                    Dialog("","서비스 이용 제한","누적된 신고로 인해 서비스 이용 제한 대상자입니다.\n(고객센터를 통해 제한 해제가 가능합니다)");
                }
                else if(user.getPenalty_point() <= user.getPoint()) {
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("point",user.getPoint() - user.getPenalty_point());
                    mDatabase.child("user").child(ID).updateChildren(map); // 페널티 포인트 차감
                    Toast.makeText(getApplicationContext(),"패널티 포인트가 차감되었습니다.\n서비스 이용이 가능합니다.",Toast.LENGTH_SHORT).show();
                }else if(user.getPoint() < 0 || user.getPenalty_point() >= 0){
                    Dialog("","포인트 부족","이탈 패널티로 인해 포인트가 부족합니다.\n(포인트 충전 후, 서비스 이용이 가능합니다)");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });  // penalty 포인트 check!
        mDatabase.child("post-members").child(INDEX).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Data_Members data_members = dataSnapshot.getValue(Data_Members.class);
                if(dataSnapshot.getChildren().iterator().hasNext()){
                    Dialog(data_members.getINDEX(),"노선 참가","참가 중인 노선이 있습니다.\n재입장 하시겠습니까?");            //참가 중인 노선 check
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); //참가 중인 노선 check!
    }

    private void Dialog(final String INDEX, String Title, String MSG) {
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false);
        if(Title.equals("노선 확인")) {
            alertDialogBuilder.setTitle(Title);
            alertDialogBuilder.setMessage(MSG);
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
                    Quit_Dialog();
                }
            }).show();
        }else if(Title.equals("포인트 부족")){
            alertDialogBuilder.setTitle(Title).setMessage(MSG).setPositiveButton("충전하기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(),Charge.class);
                    startActivity(intent);
                }
            }).show();
        }else if(Title.equals("서비스 이용 제한")){
            alertDialogBuilder.setTitle(Title).setMessage(MSG).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(),Login_simple.class);
                    startActivity(intent);
                }
            }).show();
        }
    }

    void Quit_Dialog() {
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("퇴장 확인").setMessage("참가중인 노선에서 퇴장하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        QUIT_PROCESS_reference();
                        QUIT_PROCESS_databaseDATA();
                        Intent intent = new Intent(getApplicationContext(), main_simple.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Dialog(INDEX,"노선 확인","참가 중인 노선이 있습니다.\n재입장 하시겠습니까?");
                alertDialogBuilder.show();
            }
        }).show();
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
                QUIT_PROCESS_reference();
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
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        mDatabase.child("taxi-call").child(INDEX).removeValue();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_simple_nav);
        BackPressCloseHandler backPressCloseHandler = new BackPressCloseHandler(this);
        init();
        initLayout(); // 사이드 메뉴바
        click();
        check();
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

    void Guide() {
        SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
        final SharedPreferences.Editor editor = positionDATA.edit();

        final Dialog Guide_Dialog = new Dialog(this);
        Guide_Dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Guide_Dialog.setContentView(R.layout.activity_guide);
        Guide adapter = new Guide(this);
        ViewPager pager = Guide_Dialog.findViewById(R.id.Viewpager);
        pager.setAdapter(adapter);
        Guide_Dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Guide_Dialog.show();

        TextView NeverText = Guide_Dialog.findViewById(R.id.NeverText);
        TextView ConfirmText = Guide_Dialog.findViewById(R.id.ConfirmText);
        NeverText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Guide_Dialog.dismiss(); // 다시 보지 않기
                editor.putBoolean("NEVER", true);
                editor.apply();
            }
        });
        ConfirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Guide_Dialog.dismiss(); // 가이드 확인
                editor.putBoolean("Guide", true);
                editor.apply();
            }
        });
    }
}
//TODO : 즐겨찾기 기능 추가

