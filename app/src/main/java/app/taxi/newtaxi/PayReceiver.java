package app.taxi.newtaxi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static android.content.Context.MODE_PRIVATE;

public class PayReceiver extends BroadcastReceiver {
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
