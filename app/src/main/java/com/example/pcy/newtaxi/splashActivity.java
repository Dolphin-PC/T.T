package com.example.pcy.newtaxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
// TODO : 스플래쉬 이미지 바꾸는 법 알아보기, 현재 디자인 꾸며져있는 방식 / 방법 알아보기
public class splashActivity extends Activity {
    public void onCreate(Bundle savedInstanceState){
        super .onCreate(savedInstanceState);

        try{
            Thread.sleep(4000);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this,Login.class));
        finish();
    }
}
