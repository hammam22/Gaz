package com.example.gazz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class act_sellers_list extends AppCompatActivity {

    ListView lv_sellersList;

    ArrayList<MyUserInfo> sellers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sellers_list);

        MyUserInfo myUserInfo = getUserInfoFromSP();

        lv_sellersList = (ListView)findViewById(R.id.lv_sellersList);

        sellers = new ArrayList<MyUserInfo>();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("users");
        query.whereEqualTo("nei",myUserInfo.nei);
        query.whereEqualTo("cat","sel");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    //end of find
                    for (ParseObject object : objects){
                        sellers.add(new MyUserInfo(
                                "",object.get("name").toString()
                                ,"","",
                                object.get("phone").toString()
                                ,"","",""
                        ));
                    }

                    Adapt_sellers adapt_sellers = new Adapt_sellers(getApplicationContext(),sellers);
                    lv_sellersList.setAdapter(adapt_sellers);
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