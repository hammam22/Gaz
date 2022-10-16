package com.example.gazz;

import androidx.appcompat.app.AppCompatActivity;
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

public class act_Request_manager extends AppCompatActivity {

    ListView lv_requsts;
    TextView tv_no_requests;

    ArrayList<MyUserInfo> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__request_manager);



        tv_no_requests = (TextView)findViewById(R.id.tv_no_requests);
        lv_requsts = (ListView)findViewById(R.id.lv_requsts);

        arrayList = new ArrayList<MyUserInfo>();
        arrayList = getObjectsFromSP();
        update_adapt();

    }



    public ArrayList<MyUserInfo> getObjectsFromSP(){

        ArrayList<MyUserInfo> userInfoList = new ArrayList<MyUserInfo>();

        SharedPreferences sp = getSharedPreferences("req",MODE_PRIVATE);
        int size = sp.getInt("size",0);
        for (int i=0; i<size; i++){
            MyUserInfo userInf = new MyUserInfo(
                    sp.getString("myid"+i,"")
                    ,sp.getString("name"+i,"")
                    ,sp.getString("email"+i,"")
                    ,sp.getString("password"+i,"")
                    ,sp.getString("phone"+i,"")
                    ,sp.getString("cat"+i,"")
                    ,sp.getString("nei"+i,"")
                    ,sp.getString("state"+i,"")
            );
            userInfoList.add(userInf);
        }


        return userInfoList;
    }

    public void processReq(int pos,boolean isAccepted){
        //delete the request from firebase using id of req
        MyUserInfo userInfo = arrayList.get(pos);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("users");
        query.whereEqualTo("email",userInfo.email);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    for (ParseObject object : objects){
                        if (isAccepted){
                            object.put("state","1");
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e==null){
                                        arrayList.remove(pos);
                                        update_adapt();
                                    } else {
                                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            //rejected
                            object.put("state","0");
                            object.put("nei","-1");
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e==null){
                                        arrayList.remove(pos);
                                        //must to make wait for previose result before click again
                                        update_adapt();
                                    } else {
                                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
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
        //if rejected just update adapter and done
        //i think the source of arraylist able to update the list auto
        //no need to remove in the code

    }



    private void update_adapt() {
        if (arrayList.size()>0){
            Adapt_req adapt = new Adapt_req(this,arrayList);
            lv_requsts.setVisibility(View.VISIBLE);
            tv_no_requests.setVisibility(View.INVISIBLE);
            lv_requsts.setAdapter(adapt);
        }else {
            //no requests
            lv_requsts.setVisibility(View.INVISIBLE);
            tv_no_requests.setVisibility(View.VISIBLE);
        }
    }


}