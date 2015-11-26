package com.proj.hwanik.water;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class Login extends AppCompatActivity {

    private TextView userEmail;
    private TextView userPwd;
    private TextView findName;
    private ImageView btnLogin;
    private ImageView goToSignUp;
    MaterialDialog.Builder dialogBuilder;
    MaterialDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail=(TextView)findViewById(R.id.userEmail);
        userPwd=(TextView)findViewById(R.id.userPwd);

        btnLogin=(ImageView)findViewById(R.id.btnLogin);
        goToSignUp=(ImageView)findViewById(R.id.goToSignUp);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder = new MaterialDialog.Builder(Login.this);
                dialogBuilder.title("계정 확인중..")
                        .content("잠시만 기다려주세요")
                        .progress(true, 0);
                mDialog = dialogBuilder.build();
                mDialog.show();

                ParseUser.logInInBackground(userEmail.getText().toString(), userPwd.getText().toString(), new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            Toast.makeText(Login.this,"로그인 성공",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(Login.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Login.this,"아이디와 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                        }
                        mDialog.dismiss();
                    }
                });
            }
        });

        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login.this,SignUp.class);
                startActivity(intent);
            }
        });

        findName=(TextView)findViewById(R.id.findMyName);
        findName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findMyName();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void findMyName() {
        boolean wrapInScrollView = true;

        final EditText email;

        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(this);
        dialogBuilder.title("닉네임 찾기")
                .customView(R.layout.custom_dialog_find_name, wrapInScrollView)
                .positiveText("확인")
                .negativeText("취소");
        MaterialDialog mDialog = dialogBuilder.build();

        email=(EditText)mDialog.findViewById(R.id.email);

        dialogBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("email", email.getText().toString());
                query.findInBackground(new FindCallback<ParseUser>() {
                    public void done(List<ParseUser> list, ParseException e) {
                        if (list.size() != 0) {
                            Toast.makeText(Login.this, "당신의 닉네임은 " + list.get(0).get("username") + "입니다.", Toast.LENGTH_SHORT).show();
                            userEmail.setText(String.valueOf(list.get(0).get("username")));
                        } else {
                            Toast.makeText(Login.this, "등록된 이메일이 아닙니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        mDialog.show();
    }
}
