package com.example.gazz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class act_Sel extends AppCompatActivity {


    int request_profile=181,requset_membership=184,gazes=0,activities=0;
    TextView tv_act_sel_code_title,tv_act_sel_code,tv_act_sel_name,tvlogoutSel,tv_seller_gaz,tv_clients;
    LinearLayout ll_member,ll_gaz,ll_myactivities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyUserInfo myUserInfo = getUserInfoFromSP();
        if (myUserInfo.phone.equals("-1")){
            startActivityForResult(new Intent(act_Sel.this,act_Edit_Profile.class),
                    request_profile);
        }

        setContentView(R.layout.act_sel);


    }

    @Override
    protected void onResume() {
        super.onResume();

        prepareViews();

        bringRequests();

    }

    private void prepareViews() {
        tv_act_sel_code_title = (TextView)findViewById(R.id.tv_act_sel_code_title);
        tv_act_sel_code = (TextView)findViewById(R.id.tv_act_sel_code);
        tv_act_sel_name=(TextView)findViewById(R.id.tv_act_sel_name);
        tv_seller_gaz=(TextView)findViewById(R.id.tv_seller_gaz);
        tv_clients = (TextView)findViewById(R.id.tv_clients);
        tvlogoutSel=(TextView)findViewById(R.id.tvlogoutSel);
        tvlogoutSel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("islogin",false);
                editor.commit();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });
        ll_member = (LinearLayout)findViewById(R.id.ll_member);
        ll_gaz=(LinearLayout)findViewById(R.id.ll_gaz);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==request_profile&&resultCode==RESULT_OK){
            bringRequests();
        }

    }

    private void bringRequests() {
        MyUserInfo myUserInfo = getUserInfoFromSP();

        //name of user
        tv_act_sel_name.setText(myUserInfo.name);

        //neicode explain
        if (myUserInfo.state.equals(String.valueOf(9))) {
            tv_act_sel_code.setText(myUserInfo.nei);
        } else if (myUserInfo.state.equals("0")) {
            tv_act_sel_code_title.setText("لست عضواً ضمن أيّ جمعيّة");
            tv_act_sel_code.setText("- - - - -");
        } else if (myUserInfo.state.equals("2")) {
            tv_act_sel_code_title.setText("بانتظار قبول طلبك للرمز");
            tv_act_sel_code.setText(myUserInfo.nei);
        } else if (myUserInfo.state.equals("1")){
            tv_act_sel_code_title.setText("أنت بائع معتمد لدى الحيّ");
            tv_act_sel_code.setText(myUserInfo.nei);
        }

        //goto membership
        ll_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myUserInfo.state.equals("1")){
                    startActivity(new Intent(getApplicationContext(),act_myMemberShip.class));
                } else {
                    startActivity(new Intent(getApplicationContext(),act_request_for_nei.class));
                }
            }
        });

        ll_gaz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),act_gaz.class));
            }
        });

        ll_myactivities = (LinearLayout)findViewById(R.id.ll_myactivities);
        ll_myactivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),act_activity_manager.class));
            }
        });

        bringGazes();
        bringActs();
    }

    private void bringActs() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("activity");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    activities=0;
                    for (ParseObject object : objects){
                        activities++;
                    }
                    tv_clients.setText(String.valueOf(activities));
                    //end of find
                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void bringGazes() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("gaz");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    gazes=0;
                    for (ParseObject object : objects){
                        gazes++;
                    }
                    tv_seller_gaz.setText(String.valueOf(gazes));
                    //end of find
                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
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