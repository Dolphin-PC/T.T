package app.taxi.newtaxi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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
    Button COMMENTbutton;
    String ID,INDEX,PROFILEURL;
    Date today = new Date();
    SimpleDateFormat timeNow = new SimpleDateFormat("a K:mm");
    String Time = timeNow.format(today);
    StringBuffer SB;
    void init(){
        SharedPreferences positionDATA = getSharedPreferences("positionDATA",MODE_PRIVATE);
        SharedPreferences.Editor editor = positionDATA.edit();
        mDatabaseMSG = FirebaseDatabase.getInstance().getReference("post-message");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        COMMENTbutton = findViewById(R.id.COMMENTbutton);
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
}
