package app.taxi.newtaxi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.security.MessageDigest;

public class Login extends AppCompatActivity {
    private BackPressCloseHandler backPressCloseHandler;
    private FirebaseAuth mAuth;
    private EditText emailText;
    private EditText pwText;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private LoginButton KakaoLoginbtn;
    SessionCallback callback;
    private DatabaseReference mDatabase;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mUserInfo;
    ConstraintLayout LAY1;
    Button emailLogin;
    CheckBox IDcheck;
    TextView registerButton;

    void init() {
        getAppKeyHash();
        emailText = findViewById(R.id.emailText);
        pwText = findViewById(R.id.pwText);
        LAY1 = findViewById(R.id.LAY1);
        emailLogin = findViewById(R.id.emailButton);
        KakaoLoginbtn = findViewById(R.id.btn_kakao_login);
        registerButton = findViewById(R.id.registerButton);
        IDcheck = findViewById(R.id.IDcheck);

    }

    void Auth() {
        mAuth = FirebaseAuth.getInstance();
    }

    void click() {
        LAY1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(emailText.getWindowToken(), 0);
            }
        });

        KakaoLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback = new SessionCallback();
                Session.getCurrentSession().addCallback(callback);
                /*Session session = Session.getCurrentSession();
                session.addCallback(new SessionCallback());
                session.open(AuthType.KAKAO_LOGIN_ALL, Login.this);*/
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), register.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();
        Auth();
        click();

        backPressCloseHandler = new BackPressCloseHandler(this);
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                //카카오톡 로그아웃 성공 후 하고싶은 내용 코딩 ~
            }
        });

        SharedPreferences positionDATA = getSharedPreferences("positionDATA", MODE_PRIVATE);
        final SharedPreferences.Editor editor = positionDATA.edit();
        if (!positionDATA.getString("LOGIN_EMAIL", "").equals("")) {
            emailText.setText(positionDATA.getString("LOGIN_EMAIL", ""));
            IDcheck.setChecked(true);
        }
        emailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailText.getText().toString().equals("") || pwText.getText().toString().equals("")) {
                    Toast.makeText(Login.this, "ID와 PW를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    if (IDcheck.isChecked()) {
                        editor.putString("LOGIN_EMAIL", emailText.getText().toString());
                        editor.apply();
                    } else {
                        editor.remove("LOGIN_EMAIL");
                        editor.apply();
                    }
                    loginUser(emailText.getText().toString(), pwText.getText().toString());
                }
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed in
                    Intent intent = new Intent(getApplicationContext(), main.class);
                    intent.putExtra("Nickname", "");
                    intent.putExtra("ID", "");
                    intent.putExtra("Profile", "");
                    intent.putExtra("Email", mAuth.getCurrentUser());
                    intent.putExtra("MESSAGE", "로그인 성공");
                    startActivity(intent);
                    finish();
                }
            }
        };

    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "로그인 실패\nID와 PW를 확인 후, 재시도해주세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "로그인 성공!", Toast.LENGTH_SHORT).show();  //이메일,패스워드 입력 로그인
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            redirectSignupActivity();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
            }
            setContentView(R.layout.login);
        }
    }

    protected void redirectSignupActivity() {       //세션 연결 성공 시 SignupActivity로 넘김
        final Intent intent = new Intent(this, KakaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }
    }
}

