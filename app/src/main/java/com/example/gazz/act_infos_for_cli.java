package com.example.gazz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class act_infos_for_cli extends AppCompatActivity {

    ArrayList<MyActivity> arrayList;
    ArrayList<MySub> sublist;
    Adapt_miniactivities Adapt_miniActivities;

    int count = 0;
    boolean isFirstAdapt=true;

    ListView lv_someacts;
    TextView tv_no_someacts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_infos_for_cli);

        lv_someacts = (ListView)findViewById(R.id.lv_someacts);
        tv_no_someacts = (TextView)findViewById(R.id.tv_no_someacts);


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

                            if (object.get("done").toString().equals("0")) {
                                sublist.add(new MySub(
                                        myUserInfo.nei, object.get("name").toString()
                                        , object.get("sel").toString()
                                        , myUserInfo.name
                                        , object.get("phone").toString()
                                        , object.get("done").toString()
                                ));
                            }
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
                                                , true
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

    private void updateLV() {
        
        getPureList();
        
        MyUserInfo myUserInfo = getUserInfoFromSP();
        if (arrayList.size()>0){
            Log.d("TAG", "updateLV: oooooooooooooooo");
            lv_someacts.setVisibility(View.VISIBLE);
            tv_no_someacts.setVisibility(View.INVISIBLE);

            if (isFirstAdapt){
                isFirstAdapt=false;
                
                    Adapt_miniActivities = new Adapt_miniactivities(this,arrayList,sublist,myUserInfo.cat);
                    lv_someacts.setAdapter(Adapt_miniActivities);
                
            }else {
                Adapt_miniActivities.notifyDataSetChanged();
            }


        } else {
            lv_someacts.setVisibility(View.INVISIBLE);
            tv_no_someacts.setVisibility(View.VISIBLE);
        }
    }

    private void getPureList() {
        if (sublist!=null){
            ArrayList<MyActivity> temp = new ArrayList<MyActivity>();
            for (int i=0; i<arrayList.size(); i++){
                for (int j=0;j<sublist.size(); j++){
                    if (sublist.get(j).name.equals(arrayList.get(i).name)
                            &&sublist.get(j).sel.equals(arrayList.get(i).sel)){
                        temp.add(arrayList.get(i));
                    }
                }
            }

            arrayList = temp;
        } else {
            arrayList.clear();
        }
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

    public void goInDeatails(int i){
        MyUserInfo myUserInfo = getUserInfoFromSP();
        Intent intent = new Intent(getApplicationContext(),act_activity_details.class);
        intent.putExtra("name",arrayList.get(i).name);
        intent.putExtra("sub",arrayList.get(i).isSubbed);
        intent.putExtra("cat",myUserInfo.cat);
        startActivity(intent);
    }
    
}