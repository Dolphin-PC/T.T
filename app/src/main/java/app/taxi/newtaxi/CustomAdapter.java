package app.taxi.newtaxi;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private ArrayList<Data_Listview> listViewItemList = new ArrayList<Data_Listview>() ;
    DatabaseReference mDatabase;

    public CustomAdapter() {
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_listview, null);
        }

        ImageView IMAGEview = convertView.findViewById(R.id.IMAGEview);
        TextView NAMEtext = convertView.findViewById(R.id.NAMEtext);
        TextView GENDERtext = convertView.findViewById(R.id.GENDERtext);
        Button INFObutton = convertView.findViewById(R.id.INFObutton);
        if(NAMEtext.getText().toString().equals(""))
            INFObutton.setVisibility(View.INVISIBLE);

        Data_Listview data_listview = listViewItemList.get(position);

        String PROFILE_URL = data_listview.getPROFILE();
        NAMEtext.setText(data_listview.getNAME());
        GENDERtext.setText(data_listview.getGENDER());

        Glide.with(convertView)
                .load(PROFILE_URL)
                .into(IMAGEview);
        return convertView;
    }
    public void addItem(String PROFILE,String NAME, String GENDER) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Data_Listview item = new Data_Listview();

        item.setPROFILE(PROFILE);
        item.setNAME(NAME);
        item.setGENDER(GENDER);

    }
}