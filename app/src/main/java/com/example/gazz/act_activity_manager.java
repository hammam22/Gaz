package com.example.gazz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class act_activity_manager extends AppCompatActivity {

    ListView lv_acts;
    TextView tv_no_acts;
    ArrayList<MyActivity> arrayList;
    ArrayList<MyActivity> doneArrayList;
    ArrayList<MySub> sublist;
    Adapt_activities adapt_activities;
    int count = 0;
    boolean isToSub = false,isFirstAdapt=true;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_activity_manager);

        context=this;

        lv_acts=(ListView)findViewById(R.id.lv_acts);
        tv_no_acts=(TextView)findViewById(R.id.tv_no_acts);

    }

    @Override
    protected void onResume() {
        super.onResume();

        getMyActivities();


    }

    private void getMyActivities() {
        MyUserInfo myUserInfo = getUserInfoFromSP();

        //get sublist for a cli
        if (myUserInfo.cat.equals("cli")){
            sublist = new ArrayList<MySub>();

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("sub");
            query.whereEqualTo("phone",myUserInfo.phone);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e==null){
                        //end of find
                        for (ParseObject object : objects){

                            Log.d("rooooooo", "done: "+object.get("done").toString());


                                sublist.add(new MySub(
                                        myUserInfo.nei, object.get("name").toString()
                                        , object.get("sel").toString()
                                        , myUserInfo.name
                                        , object.get("phone").toString()
                                        , object.get("done").toString()
                                ));

                        }
                    } else {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //search for items
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("activity");
        query.whereEqualTo("nei",myUserInfo.nei);
        if (myUserInfo.cat.equals("sel")){
            query.whereEqualTo("sel",myUserInfo.name);
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    arrayList = new ArrayList<MyActivity>();

                    for (ParseObject object : objects) {
                        if (object != null){
                            object.getParseFile("img").getFileInBackground(new GetFileCallback() {
                                @Override
                                public void done(File file, ParseException e) {
                                    if (e == null) {

                                        File dstFile = new File(getCacheDir().getAbsolutePath()
                                                + "/" + object.get("name").toString() + ".jpg");
                                        if (dstFile.exists()) {
                                            dstFile.delete();
                                        }

                                        try {
                                            FileInputStream inputStream = new FileInputStream(file);
                                            FileOutputStream outputStream = new FileOutputStream(dstFile);
                                            FileChannel inChannel = inputStream.getChannel();
                                            FileChannel outChannel = outputStream.getChannel();
                                            inChannel.transferTo(0, inChannel.size(), outChannel);
                                            inChannel.close();
                                            outChannel.close();
                                            inputStream.close();
                                            outputStream.close();
                                        } catch (FileNotFoundException fileNotFoundException) {
                                            fileNotFoundException.printStackTrace();
                                        } catch (IOException ioException) {
                                            ioException.printStackTrace();
                                        }
                                        //   //
                                        arrayList.add(new MyActivity(
                                                object.get("sel").toString()
                                                , object.get("name").toString()
                                                , object.get("dis").toString()
                                                , object.get("timeplace").toString()
                                                , object.get("notes").toString()
                                                , object.get("nei").toString()
                                                , Integer.parseInt(object.get("price").toString())
                                                , Integer.parseInt(object.get("amount").toString())
                                                , (int) object.get("taken")
                                                , dstFile.getAbsolutePath()
                                                , getisSubbed(object.get("name").toString(),sublist)
                                        ));
                                        Log.d("TAG", "name: " + object.get("name").toString());
                                        count++;
                                        if (objects.size() == count) {
                                            updateLV();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    }

                    }


                    //end of find
                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean getisSubbed(String name, ArrayList<MySub> sublist) {
        boolean isSubbed = false;

        if (sublist!=null){
            for (int i=0; i<sublist.size(); i++){
                if (sublist.get(i).name.equals(name)){
                    isSubbed = true;
                    Log.d("TAG", "in sublist = : "+sublist.get(i).done);
                }
            }

        }else {
            Log.d("TAG", "nooot in sublist = : ");

        }

        return isSubbed;
    }

    private void updateLV() {



        MyUserInfo myUserInfo = getUserInfoFromSP();
        if (arrayList.size()>0){
            Log.d("TAG", "updateLV: oooooooooooooooo");
            lv_acts.setVisibility(View.VISIBLE);
            tv_no_acts.setVisibility(View.INVISIBLE);

          if (isFirstAdapt){
                isFirstAdapt=false;
            if (myUserInfo.cat.equals("cli")){
                adapt_activities = new Adapt_activities(this,arrayList,sublist,myUserInfo.cat);
                lv_acts.setAdapter(adapt_activities);
            }else{
                adapt_activities = new Adapt_activities(this,arrayList,myUserInfo.cat);
                lv_acts.setAdapter(adapt_activities);
            }
        }else {
                adapt_activities.notifyDataSetChanged();
            }


        } else {
            lv_acts.setVisibility(View.INVISIBLE);
            tv_no_acts.setVisibility(View.VISIBLE);
        }
    }




    public boolean SubOrUnSub(String name){
        isToSub=false;
        MyUserInfo myUserInfo = getUserInfoFromSP();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("sub");
        query.whereEqualTo("phone",myUserInfo.phone);
        query.whereEqualTo("name",name);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    //check if it's realy equal
                    //end of find
                    for (ParseObject object : objects){
                        if (objects.size()>0){
                            if (object.get("done").toString().equals("0")){
                                isToSub=false;
                            }
                        }else {
                            isToSub=true;
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        return isToSub;
    }

    public void unsubNow(int i,String name,String sel) {
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
                                                                arrayList.get(i).isSubbed=false;
                                                                arrayList.get(i).taken=-1;
                                                                if (sublist.indexOf(arrayList.get(i).name)>0){
                                                                sublist.remove(sublist.indexOf(arrayList.get(i).name));
                                                                }
                                                                adapt_activities = new Adapt_activities(context,arrayList,sublist,myUserInfo.cat);
                                                                lv_acts.setAdapter(adapt_activities);

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

    public void subNow(int i,String name,String sel) {
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
                                                arrayList.get(i).isSubbed=true;
                                                arrayList.get(i).taken+=1;

                                                sublist.add(new MySub(
                                                        myUserInfo.nei,arrayList.get(i).name,sel,
                                                        myUserInfo.name,myUserInfo.phone,"0"
                                                ));
                                                //adapt_activities = new Adapt_activities(context,arrayList,sublist,myUserInfo.cat);
                                                //lv_acts.setAdapter(adapt_activities);
                                                adapt_activities.notifyDataSetChanged();
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

    public void goInDeatails(int i,String done,int remain){
        MyUserInfo myUserInfo = getUserInfoFromSP();
        Intent intent = new Intent(getApplicationContext(),act_activity_details.class);
        intent.putExtra("name",arrayList.get(i).name);
        intent.putExtra("sub",arrayList.get(i).isSubbed);
        intent.putExtra("done",done);
        intent.putExtra("remain",remain);
        intent.putExtra("cat",myUserInfo.cat);
        startActivity(intent);
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