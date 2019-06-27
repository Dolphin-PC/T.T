package app.taxi.newtaxi;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class main_simple extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private BackPressCloseHandler backPressCloseHandler;
    Button TabBarButton, webLinkButton, DirectionButton;
    TextView NameText, StartText,ArriveText;
    Spinner METERslider;

    String nickname,userid,profileURL,email,INDEX;
    boolean isPageOpen = false;
    //슬라이드 열기 애니메이션
    Animation translateLeftAnim;
    //슬라이드 닫기 애니메이션
    Animation translateRightAnim;
    //슬라이드 레이아웃
    LinearLayout slidingPage01;
    void init(){
        mDatabase = FirebaseDatabase.getInstance().getReference();

        TabBarButton = findViewById(R.id.TabBarButton);
        webLinkButton = findViewById(R.id.WebLinkButton);
        DirectionButton = findViewById(R.id.DirectionButton);
        NameText = findViewById(R.id.NameText);
        StartText = findViewById(R.id.StartText);
        ArriveText = findViewById(R.id.ArriveText);
        METERslider = findViewById(R.id.METERslider);

        SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
        nickname = positionDATA.getString("USERNAME", "");
        userid = positionDATA.getString("ID", "");
        profileURL = positionDATA.getString("PROFILE", "");
        INDEX = positionDATA.getString("INDEX","");

        NameText.setText(nickname);
    }

    void click(){
        TabBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        StartText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Map.class);
                startActivity(intent);
            }
        }); //TODO : 출발지/도착지 설정 Map으로 갈 때 들고가야 될것
        ArriveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Map.class);
                startActivity(intent);
            }
        });
        DirectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//TODO : Join

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
        setContentView(R.layout.activity_main);

        init();
        click();
        //TODO : 버튼 탭바 : https://ghj1001020.tistory.com/30





    }
}
//TODO : 즐겨찾기 기능