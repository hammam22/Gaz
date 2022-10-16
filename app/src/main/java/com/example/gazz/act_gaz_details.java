package com.example.gazz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.List;

public class act_gaz_details extends AppCompatActivity {

    Button bt_delete_gaz,bt_make;
    TextView tv_gaz_details_name,tv_gaz_details_dis;
    ImageView iv_gaz_details;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_gaz_details);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String dis = intent.getStringExtra("dis");
        String path = intent.getStringExtra("path");

        tv_gaz_details_name = (TextView)findViewById(R.id.tv_gaz_details_name);
        tv_gaz_details_name.setText(name);

        tv_gaz_details_dis = (TextView)findViewById(R.id.tv_gaz_details_dis);
        tv_gaz_details_dis.setText(dis);

        Bitmap bitmap = BitmapFactory.decodeFile(path);
        iv_gaz_details = (ImageView)findViewById(R.id.iv_gaz_details);
        iv_gaz_details.setImageBitmap(bitmap);

        bt_delete_gaz = (Button)findViewById(R.id.bt_delete_gaz);
        bt_delete_gaz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("gaz");
                query.whereEqualTo("name",name);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e==null){
                            //end of find
                            for (ParseObject object : objects){
                                object.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e==null){
                                            Toast.makeText(getApplicationContext(),"حذف بنجاح",Toast.LENGTH_SHORT).show();
                                            finish();
                                        }else {
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

        bt_make=(Button)findViewById(R.id.bt_make);
        bt_make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUserInfo myUserInfo = getUserInfoFromSP();


                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("activity");
                query.whereEqualTo("sel",myUserInfo.name);
                query.whereEqualTo("name",name);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e==null){
                            //end of find
                            for (ParseObject object : objects){
                                count++;
                            }
                            if (count==0){
                                Intent myintent = new Intent(getApplicationContext(),act_make_gaz_activity.class);
                                myintent.putExtra("name",name);
                                myintent.putExtra("dis",dis);
                                myintent.putExtra("path",path);
                                startActivity(myintent);
                            }else {
                                Toast.makeText(getApplicationContext(),"أكمل التسليم السابق لنفس الانبوبة",Toast.LENGTH_SHORT).show();
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