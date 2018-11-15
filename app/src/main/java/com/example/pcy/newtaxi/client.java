package com.example.pcy.newtaxi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class client extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference mDatabase;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView pointTextView;
    private Button addpostButton;
    private Button refreshButton;
    private Button joinButton;
    private ListView postList;
    private ArrayAdapter adapter;
    private static String index;
    private FirebaseAuth mAuth;
    private static int intentIndex;
    private DatabaseReference mUserInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.getHeaderView(0);

        postList = findViewById(R.id.postListView);
        nameTextView = view.findViewById(R.id.header_name_textView);
        emailTextView = view.findViewById(R.id.header_email_textView);
        pointTextView = view.findViewById(R.id.header_point_textView);
        addpostButton = findViewById(R.id.add_post_button);
        addpostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),post.class);
                intent.putExtra("userID", nameTextView.getText().toString());
                startActivity(intent);
            }
        });
        refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshPost();
            }
        });
        joinButton = findViewById(R.id.joinButton);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = Integer.parseInt(index.split("/")[0]);
                updateListView(i);
            }
        });

        postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String str = (String)adapterView.getAdapter().getItem(i);
                index = str.split("/")[0];
            }
        });

        emailTextView.setText(mAuth.getCurrentUser().getEmail());
        mUserInfo = databaseReference.child("user");
        Query query = mUserInfo.orderByChild("email").equalTo(mAuth.getCurrentUser().getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    User user = appleSnapshot.getValue(User.class);
                    nameTextView.setText(user.getUsername());
                    pointTextView.setText("Point : " + user.getPoint());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice , android.R.id.text1);
        postList.setAdapter(adapter);
        databaseReference.child("post").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PostData postData = dataSnapshot.getValue(PostData.class);
                adapter.add(postData.getIndex()+"/"+postData.getTitle() + ": " + postData.getStart() + "->" + postData.getArrive() + "(" + postData.getPerson() + ")명" + ", P("+postData.getPoint()+")");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                PostData postData = dataSnapshot.getValue(PostData.class);
                adapter.remove(postData.getIndex()+"/"+postData.getTitle() + ": " + postData.getStart() + "->" + postData.getArrive() + "(" + postData.getPerson() + ")명" + ", P("+postData.getPoint()+")");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void updateListView(final int i){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference reference = firebaseDatabase.getReference();
        Query query = reference.child("post").orderByChild("index").equalTo(i);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                PostData postData = nodeDataSnapshot.getValue(PostData.class);
                int person = postData.getPerson();
                if(person<4){
                    person++;
                    String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                    String path = "/" + dataSnapshot.getKey() + "/" + key;
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("person", person);
                    reference.child(path).updateChildren(result);
                    Intent intent = new Intent(getApplicationContext(),my_taxi.class);
                    intent.putExtra("Index", i);
                    intent.putExtra("person",person);
                    intent.putExtra("userID",nameTextView.getText().toString());
                    intent.putExtra("title",postData.getTitle());
                    intent.putExtra("start",postData.getStart());
                    intent.putExtra("arrive",postData.getArrive());
                    intent.putExtra("point",postData.getPoint());
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(),"인원 초과",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void RefreshPost(){
        databaseReference.child("post").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PostData postData = dataSnapshot.getValue(PostData.class);
                adapter.remove(postData.getIndex()+"/"+postData.getTitle() + ": " + postData.getStart() + "->" + postData.getArrive() + "(" + postData.getPerson() + ")명");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PostData postData = dataSnapshot.getValue(PostData.class);
                adapter.remove(postData.getIndex()+"/"+postData.getTitle() + ": " + postData.getStart() + "->" + postData.getArrive() + "(" + postData.getPerson() + ")명");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                PostData postData = dataSnapshot.getValue(PostData.class);
                adapter.remove(postData.getIndex()+"/"+postData.getTitle() + ": " + postData.getStart() + "->" + postData.getArrive() + "(" + postData.getPerson() + ")명");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        if(id == R.id.nav_logout) {
            mAuth.signOut();
            finish();
            Intent logoutIntent = new Intent(this, MainActivity.class);
            startActivity(logoutIntent);
        }else if(id == R.id.nav_manage){

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
