package com.example.gazz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.File;

public class act_make_gaz_activity extends AppCompatActivity {

    String name,dis,path;

    ImageView iv_gazFornewAct;
    TextView tv_makeNew_name,tv_makeNew_dis;
    EditText ed_makeNew_price,ed_makeNew_amount,ed_makeNew_timePlace,ed_makeNew_notes;
    Button bt_makeNew_doit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_make_gaz_activity);

        Intent intent = getIntent();
        name=intent.getStringExtra("name");
        dis=intent.getStringExtra("dis");
        path=intent.getStringExtra("path");

        iv_gazFornewAct  = (ImageView)findViewById(R.id.iv_gazFornewAct);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        iv_gazFornewAct.setImageBitmap(bitmap);

        tv_makeNew_name = (TextView)findViewById(R.id.tv_makeNew_name);
        tv_makeNew_name.setText(name);
        tv_makeNew_dis= (TextView)findViewById(R.id.tv_makeNew_dis);
        tv_makeNew_dis.setText(dis);

        ed_makeNew_price= (EditText)findViewById(R.id.ed_makeNew_price);
        ed_makeNew_amount = (EditText)findViewById(R.id.ed_makeNew_amount);
        ed_makeNew_timePlace = (EditText)findViewById(R.id.ed_makeNew_timePlace);
        ed_makeNew_notes= (EditText)findViewById(R.id.ed_makeNew_notes);

        bt_makeNew_doit= (Button)findViewById(R.id.bt_makeNew_doit);
        bt_makeNew_doit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUserInfo myUserInfo = getUserInfoFromSP();
                int price = Integer.parseInt(ed_makeNew_price.getText().toString());
                int amount= Integer.parseInt(ed_makeNew_amount.getText().toString());
                String timeplace = ed_makeNew_timePlace.getText().toString();
                String notes = ed_makeNew_notes.getText().toString();

                ParseFile parseFile = new ParseFile(new File(path));

                ParseObject object = new ParseObject("activity");
                object.put("nei",myUserInfo.nei);
                object.put("sel",myUserInfo.name);
                object.put("img",parseFile);
                object.put("name",name);
                object.put("dis",dis);
                object.put("price",price);
                object.put("amount",amount);
                object.put("taken",0);
                object.put("timeplace",timeplace);
                object.put("notes",notes);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null){
                            Intent myintent = new Intent(getApplicationContext(),act_activity_details.class);
                            myintent.putExtra("name",name);
                            myintent.putExtra("cat",myUserInfo.cat);
                            startActivity(myintent);
                            finish();
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