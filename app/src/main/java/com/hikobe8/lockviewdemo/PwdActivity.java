package com.hikobe8.lockviewdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PwdActivity extends ActionBarActivity {
    public static final int SETPWD = 1;
    public static final int TESTPWD = 2;
    @Bind(R.id.lockView_pwd)
    LockView lockViewPwd;
    private int flag;
    private String pwd;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        flag = getIntent().getIntExtra("flag", -1);

        lockViewPwd.setOnDrawFinishedListener(new LockView.OnDrawFinishListener() {
            @Override
            public int onDrawFinished(List<Integer> passPoints) {
                int result = 2;
                if (flag == 1) {
                    //设置密码
                    StringBuilder string = new StringBuilder();
                    for(Integer i : passPoints) {
                        string.append(i);
                    }
                    pwd = new String(string);
                    sp = getSharedPreferences("position", MODE_PRIVATE);
                    sp.edit().putString("pwd", pwd).commit();
                    result =LockView.OnDrawFinishListener.PWD_SET;
                } else {
                    //测试密码
                    sp = getSharedPreferences("position", MODE_PRIVATE);
                    pwd = sp.getString("pwd","");
                    if(!TextUtils.isEmpty(pwd)) {
                        StringBuilder string = new StringBuilder();
                        for(Integer i : passPoints) {
                            string.append(i);
                        }
                        String inputPwd = new String(string);
                        Log.e("fuckyou","input: " + inputPwd);
                        Log.e("fuckyou","pwd: " + pwd);
                        if(inputPwd.equals(pwd)){
                            result = LockView.OnDrawFinishListener.PWD_CORRECT;
                        } else {
                            result = LockView.OnDrawFinishListener.PWD_WRONG;
                        }
                    }
                }
                return result;
            }

            @Override
            public void onCheckSuccess() {
                Toast.makeText(PwdActivity.this, "密码测试正确!", Toast.LENGTH_SHORT).show();
                lockViewPwd.resetPoints();
            }

            @Override
            public void onSetPwdSuccess() {
                Toast.makeText(PwdActivity.this, "密码设置成功!", Toast.LENGTH_SHORT).show();
                lockViewPwd.resetPoints();
            }

            @Override
            public void onCheckPwdFailed() {
                Toast.makeText(PwdActivity.this, "密码测试错误!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pwd, menu);
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
}
