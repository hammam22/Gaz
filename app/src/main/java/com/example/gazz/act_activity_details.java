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
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class act_activity_details extends AppCompatActivity {

    TextView tv_activity_name , tv_activity_dis,ed_activity_price
            ,ed_activity_amount,ed_activity_timePlace,ed_activity_notes,ed_activity_remains;
    ImageView iv_gazActdetails;
    Button bt_cancelAct,bt_showSubs;
    String name,cat,sel,done;
    int remain;
    boolean isSubbed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_activity_details);

        //need to update remains every time


        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        isSubbed = intent.getBooleanExtra("sub",false);
        cat = intent.getStringExtra("cat");
        done = intent.getStringExtra("done");
        remain = intent.getIntExtra("remain",0);

        tv_activity_name = (TextView)findViewById(R.id.tv_activity_name);
        tv_activity_dis  = (TextView)findViewById(R.id.tv_activity_dis);

        ed_activity_price = (TextView)findViewById(R.id.ed_activity_price);
        ed_activity_amount = (TextView)findViewById(R.id.ed_activity_amount);
        ed_activity_timePlace = (TextView)findViewById(R.id.ed_activity_timePlace);
        ed_activity_notes = (TextView)findViewById(R.id.ed_activity_notes);
        ed_activity_remains=(TextView)findViewById(R.id.ed_activity_remains);

        bt_showSubs=(Button)findViewById(R.id.bt_showSubs);
        bt_cancelAct = (Button)findViewById(R.id.bt_cancelAct);

        if (cat.equals("cli")){
            bt_showSubs.setVisibility(View.INVISIBLE);
            if (isSubbed){
                bt_cancelAct.setText("إلغاء الحجز");
            }else {
                bt_cancelAct.setText("حجز أنبوبة");
            }
        }else {
            bt_showSubs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
        bt_cancelAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cat.equals("sel")){
                    MyUserInfo myUserInfo = getUserInfoFromSP();
                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("activity");
                    query.whereEqualTo("name",name);
                    query.whereEqualTo("nei",myUserInfo.nei);
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
                                                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("sub");
                                                query.whereEqualTo("sel",myUserInfo.name);
                                                query.whereEqualTo("name",name);
                                                query.findInBackground(new FindCallback<ParseObject>() {
                                                    @Override
                                                    public void done(List<ParseObject> objects, ParseException e) {
                                                        if (e==null){
                                                            for (ParseObject ob : objects){
                                                                ob.deleteInBackground(new DeleteCallback() {
                                                                    @Override
                                                                    public void done(ParseException e) {
                                                                        if (e == null) {
                                                                            Toast.makeText(getApplicationContext(),"حذف بنجاح",Toast.LENGTH_SHORT).show();
                                                                            startActivity(new Intent(getApplicationContext(),act_activity_manager.class));
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

                                                //end
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
                    //end of sel
                } else if (cat.equals("cli")){
                    if (done.equals("0")||(done.equals(null)&&remain>0)){
                        if (isSubbed){
                            unSub();
                        } else{
                            sub();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"لا يمكن",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        iv_gazActdetails = (ImageView)findViewById(R.id.iv_gazActdetails);

        //retrieve from db
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("activity");
        query.whereEqualTo("name",name);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    //end of find
                    for (ParseObject object : objects){


                        object.getParseFile("img").getFileInBackground(new GetFileCallback() {
                            @Override
                            public void done(File file, ParseException e) {
                                if (e==null){
                                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                    String dis = object.get("dis").toString();
                                    int price = Integer.parseInt(object.get("price").toString());
                                    int amount = Integer.parseInt(object.get("amount").toString());
                                    String timeplace = object.get("timeplace").toString();
                                    String notes = object.get("notes").toString();
                                    sel = object.get("sel").toString();
                                    int taken = (int) object.get("taken");

                                    tv_activity_name.setText(name);
                                    tv_activity_dis.setText(dis);

                                    ed_activity_price.setText(String.valueOf(price));
                                    ed_activity_amount.setText(String.valueOf(amount));
                                    ed_activity_timePlace.setText(timeplace);
                                    ed_activity_notes.setText(notes);

                                    iv_gazActdetails.setImageBitmap(bitmap);
                                    ed_activity_remains.setText(String.valueOf(amount-taken));
                                    //end of find
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

        bt_showSubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myintent = new Intent(getApplicationContext(),act_selSubsManager.class);
                myintent.putExtra("name",name);
                startActivity(myintent);
            }
        });
    }

    private void sub() {
        MyUserInfo myUserInfo = getUserInfoFromSP();
        ParseObject object = new ParseObject("sub");
        object.put("nei",myUserInfo.nei);
        object.put("sel",sel);
        object.put("name",name);
        object.put("cli",myUserInfo.name);
        object.put("phone",myUserInfo.phone);
        object.put("done","0");
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e== null){
                    //now sub remains and add taken
                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("activity");
                    query.whereEqualTo("name",name);
                    query.whereEqualTo("sel",sel);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e==null){
                                //end of find
                                for (ParseObject object : objects){
                                    int taken = (int) object.get("taken");
                                    object.put("taken",taken+1);
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e==null){
                                                Toast.makeText(getApplicationContext(),"اشترك بنجاح",Toast.LENGTH_SHORT).show();
                                                finish();
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
                    //

                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void unSub() {

        MyUserInfo myUserInfo = getUserInfoFromSP();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("sub");
        query.whereEqualTo("phone",myUserInfo.phone);
        query.whereEqualTo("name",name);
        query.whereEqualTo("sel",sel);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    //check if it's realy equal
                    //end of find
                    for (ParseObject object : objects){
                        object.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e==null){
                                    //sub taken and provide one
                                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("activity");
                                    query.whereEqualTo("name",name);
                                    query.whereEqualTo("sel",sel);
                                    query.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            if (e==null){
                                                //end of find
                                                for (ParseObject object : objects){
                                                    int taken = (int) object.get("taken");
                                                    object.put("taken",taken-1);
                                                    object.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e==null){
                                                                Toast.makeText(getApplicationContext(),"ألغي الحجز",Toast.LENGTH_SHORT).show();
                                                                finish();

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
                                    //

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