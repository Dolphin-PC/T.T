package app.taxi.newtaxi;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MAP_Dialog extends DialogFragment implements OnMapReadyCallback {
    DatabaseReference mDatabase;
    private SupportMapFragment fragment;

    private Context context;
    GoogleMap googleMap;
    TextView TIMEtext,PRICEtext,DISTANCEtext,OUTtext;
    Button JOINbutton;
    String USERNAME,USERID,INDEX,URL;
    LatLng ARRIVElatlng;
    private static int DEFAULT_ZOOM = 15;

    public MAP_Dialog(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.map_dialog, container, false);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.MAP_Dialog, fragment).commit();

        TIMEtext = view.findViewById(R.id.TIMEtext);
        PRICEtext = view.findViewById(R.id.PRICEtext);
        DISTANCEtext = view.findViewById(R.id.DISTANCEtext);
        OUTtext = view.findViewById(R.id.OUTtext);
        JOINbutton = view.findViewById(R.id.JOINbutton);

        LATLNG();
        JOINbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = mDatabase.child("post").orderByChild("index").equalTo(INDEX);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            Data_Post data_post = appleSnapshot.getValue(Data_Post.class);
                            if(data_post.getPerson() < data_post.getMaxPerson()) {
                                Data_Members data_members = new Data_Members(USERNAME,INDEX,URL,"남",USERID,false);
                                mDatabase.child("post-members").push().setValue(data_members);
                            }
                            else{
                                Toast.makeText(context,"인원이 초과되었습니다.",Toast.LENGTH_SHORT);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });
        return view;
    }

    void LATLNG(){
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Query query = mDatabase.child("post").orderByChild("index").equalTo(INDEX);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    Data_Post data_post = appleSnapshot.getValue(Data_Post.class);
                    TIMEtext.setText(data_post.getTime());
                    PRICEtext.setText(data_post.getPrice() + "P");
                    DISTANCEtext.setText(data_post.getDistance().split(":")[1]);
                    ARRIVElatlng = new LatLng(37.566643, 126.978279);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ARRIVElatlng,DEFAULT_ZOOM));
        googleMap.addMarker(new MarkerOptions().position(ARRIVElatlng).title("도착 위치").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }
}
