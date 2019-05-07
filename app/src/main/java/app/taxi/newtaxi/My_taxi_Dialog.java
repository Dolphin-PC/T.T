package app.taxi.newtaxi;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

public class My_taxi_Dialog  extends DialogFragment {
    DatabaseReference mDatabase;
    private SupportMapFragment fragment;

    private Context context;
    ListView MYDIALOGlist;
    TextView TIMEtext,PRICEtext,DISTANCEtext,PERSONtext;
    Button OUTbutton,PAYbutton;
    String USERNAME,USERID,INDEX,URL;

    public My_taxi_Dialog(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.my_taxi_dialog, container, false);

        TIMEtext = view.findViewById(R.id.TIMEtext);
        PRICEtext = view.findViewById(R.id.PRICEtext);
        DISTANCEtext = view.findViewById(R.id.DISTANCEtext);
        PERSONtext = view.findViewById(R.id.PERSONtext);
        OUTbutton = view.findViewById(R.id.OUTbutton);
        PAYbutton = view.findViewById(R.id.PAYbutton);

        return view;
    }
}