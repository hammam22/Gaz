package com.example.gazz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main2);

        SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
        boolean islogin = sp.getBoolean("islogin",false);

        if (islogin){
            Log.d("from start", "To main");

            MyUserInfo myUserInfo = getUserInfoFromSP();
            if (myUserInfo.cat.equals("nei")){
                startActivity(new Intent(getApplicationContext(),act_Nei.class));
                finish();
            } else if (myUserInfo.cat.equals("sel")){
                startActivity(new Intent(getApplicationContext(),act_Sel.class));
                finish();
            } else if (myUserInfo.cat.equals("cli")){
                startActivity(new Intent(getApplicationContext(),act_Cli.class));
                finish();
            }
            return;

        } else {
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        }



    }
    public MyUserInfo getUserInfoFromSP(){
        SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);

        MyUserInfo userInfo = new MyUserInfo();

        userInfo.myid=sp.getString("myid","-1");
        userInfo.name=sp.getString("name","-1");
        userInfo.email=sp.getString("email","-1");
        userInfo.phone=sp.getString("phone","-1");
        userInfo.cat=sp.getString("cat","-1");
        userInfo.nei=sp.getString("nei","-1");
        userInfo.state=sp.getString("state","0");

        return userInfo;
    }
}