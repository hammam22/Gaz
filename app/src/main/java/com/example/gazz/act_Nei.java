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
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class act_Nei extends AppCompatActivity {

    int request_profile=181,request_nei_code=182,requsts=0,sellers=0;

    ArrayList<ParseObject> wholeList;
    ArrayList<ParseObject> reqList;
    ArrayList<ParseObject> selList;
    LinearLayout ll_activities,ll_clients,ll_sellers,ll_requests,ll_nei_code;
    TextView tv_act_nei_code,tv_requests,tv_sellers,tv_clients,tv_acitivies,tv_act_nei_name,tv_act_nei_code_title,logoutNei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyUserInfo myUserInfo = getUserInfoFromSP();
        if (myUserInfo.phone.equals("-1")){
            startActivityForResult(new Intent(act_Nei.this,act_Edit_Profile.class),
                    request_profile);
        }

        setContentView(R.layout.act_nei);

    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareViews();

        bringRequests();
    }



    private void prepareViews() {
        ll_nei_code = (LinearLayout)findViewById(R.id.ll_nei_code);
        ll_requests = (LinearLayout)findViewById(R.id.ll_member);
        logoutNei=(TextView)findViewById(R.id.logoutNei);
        logoutNei.setOnClickListener(new View.OnClickListener() {
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
        ll_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(act_Nei.this,act_Request_manager.class);
                startActivity(intent);
            }
        });
        ll_sellers = (LinearLayout)findViewById(R.id.ll_gaz);
        ll_sellers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act_Nei.this,act_sellers_manager.class);
                startActivity(intent);
            }
        });
        ll_clients = (LinearLayout)findViewById(R.id.ll_myactivities);
        ll_activities = (LinearLayout)findViewById(R.id.ll_activities);

        tv_acitivies = (TextView)findViewById(R.id.tv_acitivies);
        tv_clients = (TextView)findViewById(R.id.tv_clients);
        tv_sellers = (TextView)findViewById(R.id.tv_seller_gaz);
        tv_requests = (TextView)findViewById(R.id.tv_requests);
        tv_act_nei_code = (TextView)findViewById(R.id.tv_act_nei_code);
        tv_act_nei_name = (TextView)findViewById(R.id.tv_act_nei_name);
        tv_act_nei_code_title = (TextView)findViewById(R.id.tv_act_nei_code_title);

    }


    private void bringRequests() {

        //bring data

        ParseUser user = ParseUser.getCurrentUser();
        MyUserInfo myUserInfo = getUserInfoFromSP();




        //name of user
        tv_act_nei_name.setText(myUserInfo.name);

        //neicode explain

        if (myUserInfo.state.equals(String.valueOf(9)) || myUserInfo.state.equals(String.valueOf(1))) {
            tv_act_nei_code.setText(myUserInfo.nei);
        } else if (myUserInfo.state.equals("0")) {
            tv_act_nei_code_title.setText("لست عضواً ضمن أيّ جمعيّة");
            tv_act_nei_code.setText("- - - - -");
        } else if (myUserInfo.state.equals("2")) {
            tv_act_nei_code_title.setText("بانتظار قبول طلبك للرمز");
            tv_act_nei_code.setText(myUserInfo.nei);
        }

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("users");
        query.whereEqualTo("nei",myUserInfo.nei);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    wholeList = new ArrayList<ParseObject>();
                    for (ParseObject object : objects){
                        wholeList.add(object);
                        processList();
                    }
                    //end of find
                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void processList() {
        findRequests();
        findSellers();
    }

    private void findSellers() {
        sellers=0;
        selList = new ArrayList<ParseObject>();
        for (int i=0; i<wholeList.size(); i++){
            if (wholeList.get(i).get("cat").equals("sel")&&wholeList.get(i).get("state").equals("1")){
                selList.add(wholeList.get(i));
                sellers++;
            }
        }

        tv_sellers.setText(String.valueOf(sellers));
        saveObjectsToSP("sel",selList);
    }

    private void findRequests() {
        requsts=0;
        reqList = new ArrayList<ParseObject>();
        for (int i=0; i<wholeList.size(); i++){
            if (wholeList.get(i).get("state").equals("2")){
                reqList.add(wholeList.get(i));
                requsts++;
            }
        }
        tv_requests.setText(String.valueOf(requsts));
        saveObjectsToSP("req",reqList);
    }

    private void saveObjectsToSP(String spName, ArrayList<ParseObject> list) {
        SharedPreferences sp = getSharedPreferences(spName,MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        for (int i=0; i<list.size(); i++){
            editor.putString("myid"+i,list.get(i).get("myid").toString());
            editor.putString("name"+i,list.get(i).get("name").toString());
            editor.putString("email"+i,list.get(i).get("email").toString());
            editor.putString("password"+i,list.get(i).get("password").toString());
            editor.putString("phone"+i,list.get(i).get("phone").toString());
            editor.putString("cat"+i,list.get(i).get("cat").toString());
            editor.putString("nei"+i,list.get(i).get("nei").toString());
            editor.putString("state"+i,list.get(i).get("state").toString());
        }
        editor.putInt("size",list.size());
        editor.commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==request_profile&&resultCode==RESULT_OK){
            MyUserInfo userInfo = getUserInfoFromSP();
            //check if first time to make nei code ..
            if (userInfo.nei.equals("-1")){
            startActivityForResult(new Intent(act_Nei.this,act_set_nei_code.class),request_nei_code);
            }
        }

        if (requestCode==request_nei_code&&resultCode==RESULT_OK){
            bringRequests();

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