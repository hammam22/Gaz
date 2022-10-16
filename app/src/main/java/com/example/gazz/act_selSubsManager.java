package com.example.gazz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class act_selSubsManager extends AppCompatActivity {

    String name;
    MyUserInfo myUserInfo;
    ArrayList<MySub> arrayList;
    Adapt_SelSub adapt_selSub;
    ListView lv_SelSub;
    TextView tv_no_SelSub;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sel_subs_manager);

        context=this;

        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        myUserInfo = getUserInfoFromSP();

        lv_SelSub = (ListView)findViewById(R.id.lv_SelSub);
        tv_no_SelSub = (TextView)findViewById(R.id.tv_no_SelSub);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("sub");
        query.whereEqualTo("name",name);
        query.whereEqualTo("sel",myUserInfo.name);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    arrayList = new ArrayList<MySub>();
                    for (ParseObject object : objects){

                        arrayList.add(new MySub(
                                myUserInfo.nei,name
                                ,object.get("sel").toString()
                                ,object.get("cli").toString()
                                ,object.get("phone").toString()
                                ,object.get("done").toString()
                        ));

                    }

                    if (arrayList.size()>0){
                        lv_SelSub.setVisibility(View.VISIBLE);
                        tv_no_SelSub.setVisibility(View.INVISIBLE);
                        adapt_selSub = new Adapt_SelSub(context,arrayList);
                        lv_SelSub.setAdapter(adapt_selSub);
                    }else {
                        lv_SelSub.setVisibility(View.INVISIBLE);
                        tv_no_SelSub.setVisibility(View.VISIBLE);
                    }

                    //end of find
                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void istalam(int i){

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("sub");
        query.whereEqualTo("phone",arrayList.get(i).phone);
        query.whereEqualTo("name",name);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    //end of find
                    for (ParseObject object : objects){
                        object.put("done","1");
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e==null){
                                    arrayList.get(i).done="1";
                                    adapt_selSub.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void maIstalam(int i) {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("sub");
        query.whereEqualTo("phone",arrayList.get(i).phone);
        query.whereEqualTo("name",name);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    //end of find
                    for (ParseObject object : objects){
                        object.put("done","0");
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e==null){
                                    arrayList.get(i).done="0";
                                    adapt_selSub.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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