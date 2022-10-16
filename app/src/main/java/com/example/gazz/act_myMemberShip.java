package com.example.gazz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class act_myMemberShip extends AppCompatActivity {

    TextView tv_neiManagerName,tv_neiManagerPhone;
    Button bt_sellersList;
    String name,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_member_ship);

        tv_neiManagerName = (TextView)findViewById(R.id.tv_neiManagerName);
        tv_neiManagerPhone = (TextView)findViewById(R.id.tv_neiManagerPhone);
        tv_neiManagerPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+phone));
                startActivity(intent);

            }
        });

        bt_sellersList = (Button)findViewById(R.id.bt_sellersList);
        bt_sellersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),act_sellers_list.class));
            }
        });


        MyUserInfo myUserInfo = getUserInfoFromSP();

        //get manager name and phone

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("users");
        query.whereEqualTo("state","9");
        query.whereEqualTo("nei",myUserInfo.nei);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    //end of find
                    for (ParseObject object : objects){
                        name = object.get("name").toString();
                        phone = object.get("phone").toString();
                        tv_neiManagerName.setText(name);
                        tv_neiManagerPhone.setText(phone);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });



        TextView tv_mymrmbrt_code = (TextView)findViewById(R.id.tv_mymrmbrt_code);
        tv_mymrmbrt_code.setText(myUserInfo.nei);
        Button bt_mmember_cancel = (Button)findViewById(R.id.bt_mmember_cancel);
        bt_mmember_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("users");
                query.whereEqualTo("email",myUserInfo.email);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e==null){
                            for (ParseObject object : objects){
                                object.put("nei","-1");
                                object.put("state","0");
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e==null){
                                            myUserInfo.state="0";
                                            myUserInfo.nei="-1";
                                            myUserInfo.saveToSP(getApplicationContext());
                                            finish();
                                            //done
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