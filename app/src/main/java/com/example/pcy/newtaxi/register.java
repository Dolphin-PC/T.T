package com.example.pcy.newtaxi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText name;
    private EditText password;
    private EditText email;
    private EditText phone;
    private Button register;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        name = findViewById(R.id.nameText);
        password = findViewById(R.id.passwordText);
        email = findViewById(R.id.emailText);
        phone = findViewById(R.id.phoneText);
        register = findViewById(R.id.regisButton);

        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser(email.getText().toString(),password.getText().toString());
            }
        });
        ConstraintLayout LAY1 = findViewById(R.id.LAY2);
        LAY1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(name.getWindowToken(),0);
            }
        });
    }
    private void submitUser(){
        final String text1 = name.getText().toString();
        final String text2 = password.getText().toString();
        final String text3 = email.getText().toString();
        final String text4 = phone.getText().toString();

        if (TextUtils.isEmpty(text1)) {
            name.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(text2)) {
            password.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(text3)) {
            email.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(text4)) {
            phone.setError("Required");
            return;
        }
        User userData = new User(text1,text2,text3,text4,10000);
        mDatabase.child("user").push().setValue(userData);

    }

    public void createUser(final String email, final String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"가입 실패!",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "가입 성공!", Toast.LENGTH_SHORT).show();  //이메일 회원가입
                            submitUser();
                            Intent intent = new Intent(getApplicationContext(),Login.class);
                            startActivity(intent);
                            finish();
                        }

                        // ...
                    }
                });
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
