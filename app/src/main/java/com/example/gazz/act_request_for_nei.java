package com.example.gazz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import java.util.List;

public class act_request_for_nei extends AppCompatActivity {

    String nei_code;


    Button bt_apply,bt_cancel_member;
    EditText ed_req_new;
    TextView tv_waiting_nei_code,tv_req_satus;
    ConstraintLayout llGiveReq,llWaitReq;
    boolean isFound=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_request_for_nei);

        llGiveReq = (ConstraintLayout)findViewById(R.id.llGiveReq);
        llWaitReq = (ConstraintLayout)findViewById(R.id.llWaitReq);

        tv_waiting_nei_code = (TextView)findViewById(R.id.tv_waiting_nei_code);
        ed_req_new = (EditText)findViewById(R.id.ed_req_new);
        bt_apply = (Button)findViewById(R.id.bt_apply);
        bt_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = ed_req_new.getText().toString();
                if (txt.length()!=6){
                    Toast.makeText(getApplicationContext(),"يجب أن يكون الرمز من ستة خانات",Toast.LENGTH_SHORT).show();
                }else {
                    //length is good ,, keep
                    checkIfExist(txt);
                }
            }
        });
        bt_cancel_member= (Button)findViewById(R.id.bt_cancel_member);
        bt_cancel_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUserInfo userInfo = getUserInfoFromSP();

                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("users");
                query.whereEqualTo("email",userInfo.email);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e==null){
                            //found
                            for (ParseObject object : objects){
                                object.put("state","0");
                                object.put("nei","-1");
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e==null){
                                            //done
                                            userInfo.nei="-1";
                                            userInfo.state="0";
                                            userInfo.saveToSP(getApplicationContext());
                                            tv_req_satus.setText("تم إلغاء الطلب");
                                            tv_waiting_nei_code.setVisibility(View.INVISIBLE);
                                            bt_cancel_member.setVisibility(View.INVISIBLE);
                                        } else {
                                            //error
                                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            //error
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        tv_req_satus= (TextView)findViewById(R.id.tv_req_satus);

    }

    @Override
    protected void onResume() {
        super.onResume();

        MyUserInfo userInfo = getUserInfoFromSP();
        nei_code=userInfo.nei;
        if (userInfo.state.equals("2")){
            changeToWaitView(userInfo.nei);
        }
    }

    private void checkIfExist(String nei_code) {

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("users");
        query.whereEqualTo("state","9");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    //found
                    for (ParseObject object : objects){

                        if (object.get("nei").equals(nei_code)){
                            isFound = true;
                        }

                    }
                    //end of search

                    if (isFound){

                        isFound=false;
                        //yess keep working
                        MyUserInfo userInfo= getUserInfoFromSP();

                        userInfo.state="2";
                        userInfo.nei=nei_code;



                        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("users");
                        parseQuery.whereEqualTo("email",userInfo.email);

                        parseQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e==null){
                                    //save it online
                                    for (ParseObject object : objects){
                                        object.put("nei",nei_code);
                                        object.put("state","2");
                                        object.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e==null){
                                                    //save it offline
                                                    MyUserInfo userInfo= getUserInfoFromSP();

                                                    userInfo.state="2";
                                                    userInfo.nei=nei_code;

                                                    userInfo.saveToSP(getApplicationContext());

                                                    changeToWaitView(userInfo.nei);
                                                    // done
                                                } else {
                                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                                //end of find user to update
                            }
                        });



                    }else {
                        Toast.makeText(getApplicationContext(),"الرمز غير صحيح",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //error
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }

                //end of find

            }
        });


    }

    private void changeToWaitView(String nei_code) {
        llGiveReq.setVisibility(View.INVISIBLE);
        llWaitReq.setVisibility(View.VISIBLE);
        tv_waiting_nei_code.setText(nei_code);
    }



    public MyUserInfo getUserInfoFromSP(){
        SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);

        MyUserInfo userInfo = new MyUserInfo();

        userInfo.myid=sp.getString("myid","-1");
        userInfo.name=sp.getString("name","-1");
        userInfo.email=sp.getString("email","-1");
        userInfo.password=sp.getString("password","-1");
        userInfo.phone=sp.getString("phone","-1");
        userInfo.cat=sp.getString("cat","-1");
        userInfo.nei=sp.getString("nei","-1");
        userInfo.state=sp.getString("state","-1");

        return userInfo;
    }
}