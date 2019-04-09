package app.taxi.newtaxi;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

public class Charge extends AppCompatActivity {

    Button B1,B2,B3,B4,B5;
    TextView point_textview;
    void init(){
        B1 = findViewById(R.id.B1);
        B2 = findViewById(R.id.B2);
        B3 = findViewById(R.id.B3);
        B4 = findViewById(R.id.B4);
        B5 = findViewById(R.id.B5);
        point_textview = findViewById(R.id.point_textview);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);
        init();
    }

    private void Dialog(String Point) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("결제 확인");
        builder.setMessage(Point);
        builder.setPositiveButton("결제",null); //보류

    }
}
