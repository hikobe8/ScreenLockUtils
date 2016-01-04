package com.hikobe8.lockviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    @Bind(R.id.btn_set_pwd)
    Button btnSetPwd;
    @Bind(R.id.btn_test_pwd)
    Button btnTestPwd;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btnSetPwd.setOnClickListener(this);
        btnTestPwd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        intent = new Intent(MainActivity.this, PwdActivity.class);
        switch (view.getId()) {
            case R.id.btn_set_pwd:
                setPwd();
                break;
            case R.id.btn_test_pwd:
                testPwd();
                break;
        }
    }

    private void testPwd() {
        String pwd = getSharedPreferences("position",MODE_PRIVATE).getString("pwd","");
        if(TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "请先设置图案密码", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra("flag", PwdActivity.TESTPWD);
        startActivity(intent);
    }

    private void setPwd() {
        intent.putExtra("flag", PwdActivity.SETPWD);
        startActivity(intent);
    }
}
