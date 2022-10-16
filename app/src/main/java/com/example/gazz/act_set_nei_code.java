package com.example.gazz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class act_set_nei_code extends AppCompatActivity {

    EditText ed_set_nei;
    Button bt_set_nei_save;
    String nei_code,temp;
    boolean isUsed=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_set_nei_code);

        ed_set_nei = (EditText)findViewById(R.id.ed_set_nei);
        bt_set_nei_save = (Button)findViewById(R.id.bt_set_nei_save);
        bt_set_nei_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nei_code = ed_set_nei.getText().toString();
                if (nei_code.length()!=6){
                    //wrong nei_code
                    Toast.makeText(getApplicationContext(),"رمز خاطئ، راجع من التعليمات رجاءً",Toast.LENGTH_SHORT).show();
                }else {
                    //right length
                    //check if it's unique

                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("users");
                    query.whereEqualTo("state",9);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            ArrayList<String> list = new ArrayList<String>();
                            for (ParseObject object : objects){
                                list.add(object.get("nei").toString());
                            }
                            //old names collected .. now compare
                            isUsed=false;
                            for (int i=0; i<list.size(); i++){
                                if (nei_code.equals(list.get(i))){
                                    isUsed=true;
                                }
                            }
                            //check results
                            if (isUsed){
                                isUsed=false;
                                Toast.makeText(getApplicationContext(),"هذا الرمز موجود مسبقاً، اختر غيره",Toast.LENGTH_SHORT).show();
                            }else {
                                //it's not used ,, good  ,, save data
                                //add to profile and finish
                                MyUserInfo myUserInfo = getUserInfoFromSP();
                                myUserInfo.nei=nei_code;
                                myUserInfo.state="9";
                                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("users");
                                query.whereEqualTo("email",myUserInfo.email);
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        for (ParseObject object : objects){
                                            object.put("state",myUserInfo.state);
                                            object.put("nei",nei_code);
                                            object.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e==null){
                                                        //save it offline too
                                                        myUserInfo.saveToSP(getApplicationContext());
                                                        //done >> to dashbaoard
                                                        Intent intent= new Intent();
                                                        setResult(RESULT_OK,intent);
                                                        finish();
                                                    } else {

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });

                                //done
                            }
                        }
                    });

                    ////
                }
            };
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

