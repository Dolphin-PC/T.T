package app.taxi.newtaxi;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class My_taxi_simple extends AppCompatActivity {
    private DatabaseReference mDatabase;

    TextView PersonText,TimeText;
    ImageView QuitButton,InfoButton,ChattingButton;
    View View1,View2;

    String PERSON, TIME,INDEX;
    void init(){
        SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();

        INDEX = positionDATA.getString("INDEX","");

        PersonText = findViewById(R.id.PersonText);
        TimeText = findViewById(R.id.TimeText);
        QuitButton = findViewById(R.id.QuitButton);
        InfoButton = findViewById(R.id.InfoButton);
        ChattingButton = findViewById(R.id.ChattingButton);
        View1 = findViewById(R.id.View1);
        View2 = findViewById(R.id.View2);
        init_Database();

        View1.setBackgroundColor(Color.parseColor("#000000"));
        View2.setBackgroundColor(Color.parseColor("#000000"));
    }
    void init_Database(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = mDatabase.child("post").orderByChild("index").equalTo(INDEX);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Data_Post data_post = snapshot.getValue(Data_Post.class);
                    PersonText.setText(data_post.getPerson() + " / " + data_post.getMaxPerson());
                    TimeText.setText(data_post.getTime());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    void click(){
        QuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        InfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ChattingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_taxi_simple);
        init();
        click();
    }
}
