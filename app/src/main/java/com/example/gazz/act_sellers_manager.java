package com.example.gazz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class act_sellers_manager extends AppCompatActivity {

    ListView lv_sellers;
    TextView tv_no_seller;
    ArrayList<MyUserInfo> arrayList;
    int request_details=189;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sellers_manager);

        lv_sellers = (ListView)findViewById(R.id.lv_sellers);
        tv_no_seller=(TextView)findViewById(R.id.tv_no_seller);

        arrayList = new ArrayList<MyUserInfo>();
        arrayList = getObjectsFromSP();
        update_adapt();


    }


    public ArrayList<MyUserInfo> getObjectsFromSP(){

        ArrayList<MyUserInfo> userInfoList = new ArrayList<MyUserInfo>();

        SharedPreferences sp = getSharedPreferences("sel",MODE_PRIVATE);
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

    private void update_adapt() {
        if (arrayList.size()>0){
            Adapt_people adapt = new Adapt_people(this,arrayList);
            lv_sellers.setVisibility(View.VISIBLE);
            tv_no_seller.setVisibility(View.INVISIBLE);
            lv_sellers.setAdapter(adapt);
            lv_sellers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getApplicationContext(),act_people_details.class);
                    intent.putExtra("pos",i);
                    intent.putExtra("name",arrayList.get(i).name);
                    intent.putExtra("phone",arrayList.get(i).phone);
                    startActivityForResult(intent,request_details);
                }
            });
        }else {
            //no requests
            lv_sellers.setVisibility(View.INVISIBLE);
            tv_no_seller.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==request_details&&resultCode==RESULT_OK){
            int pos = data.getIntExtra("pos",0);
            String email = arrayList.get(pos).email;
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("users");
            query.whereEqualTo("email",email);
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
                                    arrayList.remove(pos);
                                    update_adapt();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}