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

import java.util.ArrayList;
import java.util.List;

public class act_Cli extends AppCompatActivity {

    int request_profile=181,count1=0;
    int mygazcount=0;
    TextView tv_act_cli_name,tv_act_cli_code_title,tv_act_cli_code,tv_cli_gaz,tv_subs,tvlogoutCli;
    LinearLayout ll_memberCli,ll_gaz_for_cli,ll_mySub;
    ArrayList<MyActivity> activityArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyUserInfo myUserInfo = getUserInfoFromSP();
        if (myUserInfo.phone.equals("-1")){
            startActivityForResult(new Intent(act_Cli.this,act_Edit_Profile.class),
                    request_profile);
        }

        setContentView(R.layout.act_cli);

        tv_act_cli_name = (TextView)findViewById(R.id.tv_act_cli_name);
        tv_act_cli_code_title = (TextView)findViewById(R.id.tv_act_cli_code_title);
        tv_act_cli_code = (TextView)findViewById(R.id.tv_act_cli_code);
        tv_cli_gaz = (TextView)findViewById(R.id.tv_cli_gaz);
        tv_subs = (TextView)findViewById(R.id.tv_subs);

        ll_memberCli = (LinearLayout)findViewById(R.id.ll_memberCli);
        ll_memberCli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myUserInfo.state.equals("1")){
                    startActivity(new Intent(getApplicationContext(),act_myMemberShip.class));
                } else {
                    startActivity(new Intent(getApplicationContext(),act_request_for_nei.class));
                }
            }
        });
        ll_gaz_for_cli = (LinearLayout)findViewById(R.id.ll_gaz_for_cli);
        ll_gaz_for_cli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),act_activity_manager.class));
            }
        });
        ll_mySub = (LinearLayout)findViewById(R.id.ll_mySub);
        ll_mySub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),act_infos_for_cli.class));
            }
        });

        tvlogoutCli=(TextView)findViewById(R.id.tvlogoutCli);
        tvlogoutCli.setOnClickListener(new View.OnClickListener() {
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

    }

    @Override
    protected void onResume() {
        super.onResume();

        MyUserInfo myUserInfo = getUserInfoFromSP();

        //name of user
        tv_act_cli_name.setText(myUserInfo.name);

        //neicode explain
        if (myUserInfo.state.equals("0")) {
            tv_act_cli_code_title.setText("لست عضواً ضمن أيّ جمعيّة");
            tv_act_cli_code.setText("- - - - -");
        } else if (myUserInfo.state.equals("2")) {
            tv_act_cli_code_title.setText("بانتظار قبول طلبك للرمز");
            tv_act_cli_code.setText(myUserInfo.nei);
        } else if (myUserInfo.state.equals("1")){
            tv_act_cli_code_title.setText("أنت مشترك لدى الحيّ");
            tv_act_cli_code.setText(myUserInfo.nei);
        }

        bringAvailableGaz();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==request_profile&&resultCode==RESULT_OK){
            bringAvailableGaz();
        }

    }

    private void bringAvailableGaz() {
        getGazActivities();
        getWaitingGaz();

    }

    private void getWaitingGaz() {

        mygazcount=0;
        MyUserInfo myUserInfo = getUserInfoFromSP();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("sub");
        query.whereEqualTo("phone",myUserInfo.phone);
        query.whereEqualTo("done","0");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    //end of find
                    for (ParseObject object : objects){
                        mygazcount++;
                        tv_subs.setText(String.valueOf(mygazcount));
                    }
                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getGazActivities() {
        MyUserInfo myUserInfo = getUserInfoFromSP();
        activityArrayList = new ArrayList<MyActivity>();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("activity");
        query.whereEqualTo("nei",myUserInfo.nei);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    //end of find
                    for (ParseObject object : objects){
                        count1++;
                        if (count1==objects.size()){
                            tv_cli_gaz.setText(String.valueOf(count1));
                        }
                        //end of for of objects
                    }
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