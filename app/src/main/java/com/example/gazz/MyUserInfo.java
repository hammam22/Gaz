package com.example.gazz;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MyUserInfo {

    String myid,name,email,password,phone,cat,nei,state,error;
    boolean general=false;

    MyUserInfo(){}

    public MyUserInfo(String myid,String name,String email,String password,String phone, String cat, String nei,String state){
        this.myid=myid;
        this.name=name;
        this.email=email;
        this.password=password;
        this.phone=phone;
        this.cat=cat;
        this.nei=nei;
        this.state=state;
    }

    public HashMap<String,String> getInnerData(){
        HashMap<String,String> map = new HashMap<>();
        map.put("email",this.email);
        map.put("name",this.name);
        map.put("password",this.password);
        map.put("phone",this.phone);
        map.put("cat",this.cat);
        map.put("nei",this.nei);
        map.put("state",String.valueOf(state));

        return map;
    }

    public HashMap<String,Object> getInnerObjectData(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("email",this.email);
        map.put("name",this.name);
        map.put("password",this.password);
        map.put("phone",this.phone);
        map.put("cat",this.cat);
        map.put("nei",this.nei);

        return map;
    }

    public void saveToSP(Context context){
        SharedPreferences sp = context.getSharedPreferences("user",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("myid",myid);
        editor.putString("name",name);
        editor.putString("email",email);
        editor.putString("password",password);
        editor.putString("phone",phone);
        editor.putString("cat",cat);
        editor.putString("nei",nei);
        editor.putString("state",state);

        editor.commit();
    }

    public boolean saveOnline(){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("users");
        query.whereEqualTo("email",this.email);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject object : objects){
                    object.put("myid",myid);
                    object.put("name",name);
                    object.put("email",email);
                    //object.put("password",password);
                    object.put("phone",phone);
                    object.put("cat",cat);
                    object.put("nei",nei);
                    object.put("state",state);
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e==null){
                                general=true;
                            } else {
                                general=false;
                                error=e.getMessage();
                            }
                        }
                    });
                }
            }
        });

        return general;
    }




}
