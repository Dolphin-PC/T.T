package app.taxi.newtaxi;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class taxi_driver extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    static private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ListView postList;
    private Button callButton;
    private ArrayAdapter adapter;
    private TextView emailText;
    private TextView pointText;
    private String index;
    private String driver;
    private String phonenumber;
    private String taxinumber;
    String plus_point;
    private int point;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.taxi_driver_tabbar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);

        postList = findViewById(R.id.postListView);
        callButton = findViewById(R.id.joinButton);
        emailText = view.findViewById(R.id.header_taxi_name);
        pointText = view.findViewById(R.id.header_taxi_point);
        DatabaseReference mDriver = databaseReference.child("taxi-info");
        Query query = mDriver.orderByChild("email").equalTo(mAuth.getCurrentUser().getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    Data_Taxi dataTaxi = appleSnapshot.getValue(Data_Taxi.class);
                    emailText.setText("Driver :" +  dataTaxi.getDriver());
                    pointText.setText("Point : " + dataTaxi.getPay());
                    point = dataTaxi.getPay();
                    driver = dataTaxi.getDriver();
                    phonenumber = dataTaxi.getTaxiPhonenumber();
                    taxinumber = dataTaxi.getTaxinumber();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
    });
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = firebaseDatabase.getReference();
                Query query1 = reference.child("post").orderByChild("index").equalTo(index);
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                        Data_Post dataPost = nodeDataSnapshot.getValue(Data_Post.class);
                        plus_point = dataPost.getPrice();
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("driver", driver);
                        result.put("taxinumber",taxinumber);
                        result.put("phonenumber",phonenumber);
                        reference.child(path).updateChildren(result);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                Query query3 = reference.child("taxi-info").orderByChild("driver").equalTo(driver);
                query3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("point", point + plus_point);
                        reference.child(path).updateChildren(result);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice , android.R.id.text1);
        postList.setAdapter(adapter);
        postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {     //게시물 선택 시 이벤트
                String str = (String)adapterView.getAdapter().getItem(i);
                index = str.split("/")[0];
            }
        });
        databaseReference.child("call-taxi").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Data_Post dataPost = dataSnapshot.getValue(Data_Post.class);
                index = dataPost.getIndex();
                adapter.add(dataPost.getIndex()+"/"+ dataPost.getTitle() + ": " + dataPost.getStart() + "->" + dataPost.getArrive() + "(" + dataPost.getPerson() + ")명" + ", P("+ dataPost.getPrice()+")");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Data_Post dataPost = dataSnapshot.getValue(Data_Post.class);
                adapter.remove(dataPost.getIndex()+"/"+ dataPost.getTitle() + ": " + dataPost.getStart() + "->" + dataPost.getArrive() + "(" + dataPost.getPerson() + ")명" + ", P("+ dataPost.getPrice()+")");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); //게시물 띄우기

    }
    public void update(int i){



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
        getMenuInflater().inflate(R.menu.taxi_client, menu);
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
        if (id == R.id.nav_logout) {
            mAuth.signOut();
            finish();
            Intent logoutIntent = new Intent(this, Login.class);
            startActivity(logoutIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
