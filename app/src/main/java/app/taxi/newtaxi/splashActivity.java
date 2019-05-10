package app.taxi.newtaxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class splashActivity extends Activity {
    public void onCreate(Bundle savedInstanceState){
        super .onCreate(savedInstanceState);

        try{
            Thread.sleep(2000);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this,Login.class));
        finish();
    }
}
