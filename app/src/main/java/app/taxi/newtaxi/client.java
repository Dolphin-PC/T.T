package app.taxi.newtaxi;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
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
    static private String indexS;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserInfo;
    private int indexcount=1;
    static private String arrive,start,title,userID,point;
    static private int person,index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_tabbar);
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
                intent.putExtra("index",indexcount);
                indexcount=1;
                startActivity(intent);
            }
        });
        joinButton = findViewById(R.id.joinButton);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = Integer.parseInt(indexS.split("/")[0]);
                index = i;
                updateListView(i);
            }
        });

        postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String str = (String)adapterView.getAdapter().getItem(i);
                indexS = str.split("/")[0];
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
                Data_Post dataPost = dataSnapshot.getValue(Data_Post.class);
                adapter.add(dataPost.getIndex()+"/"+ dataPost.getTitle() + ": " + dataPost.getStart() + "->" + dataPost.getArrive() + "(" + dataPost.getPerson() + ")명" + ", P("+ dataPost.getPoint()+")");
                indexcount++;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Data_Post dataPost = dataSnapshot.getValue(Data_Post.class);
                adapter.remove(dataPost.getIndex()+"/"+ dataPost.getTitle() + ": " + dataPost.getStart() + "->" + dataPost.getArrive() + "(" + dataPost.getPerson() + ")명" + ", P("+ dataPost.getPoint()+")");
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
                Data_Post dataPost = nodeDataSnapshot.getValue(Data_Post.class);
                person = dataPost.getPerson();
                if(person<4){
                    person++;
                    String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                    String path = "/" + dataSnapshot.getKey() + "/" + key;
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("person", person);
                    reference.child(path).updateChildren(result);
                    Intent intent = new Intent(getApplicationContext(),My_taxi.class);
                    userID = nameTextView.getText().toString();
                    title = dataPost.getTitle();
                    start = dataPost.getStart();
                    arrive = dataPost.getArrive();
                    point = dataPost.getPoint();
                    intent.putExtra("Index", i);
                    intent.putExtra("person",person);
                    intent.putExtra("userID",userID);
                    intent.putExtra("title",title);
                    intent.putExtra("start",start);
                    intent.putExtra("arrive",arrive);
                    intent.putExtra("point",point);
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
            Intent logoutIntent = new Intent(this, Login.class);
            startActivity(logoutIntent);
        }
        else if (id==R.id.nav_reentry){
            Intent intent = new Intent(getApplicationContext(),My_taxi.class);
            intent.putExtra("Index", index);
            intent.putExtra("person",person);
            intent.putExtra("userID",userID);
            intent.putExtra("title",title);
            intent.putExtra("start",start);
            intent.putExtra("arrive",arrive);
            intent.putExtra("point",point);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}

