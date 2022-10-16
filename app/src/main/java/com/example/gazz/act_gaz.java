package com.example.gazz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
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

public class act_gaz extends AppCompatActivity {

    ListView lv_gaz;
    TextView tv_noGaz;
    boolean isNotEqual=false;
    int count = 0;
    ArrayList<MyGaz> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_gaz);

    }

    @Override
    protected void onResume() {
        super.onResume();

        lv_gaz = (ListView)findViewById(R.id.lv_gaz);
        tv_noGaz = (TextView)findViewById(R.id.tv_noGaz);

        Button bt_add_gaz = (Button)findViewById(R.id.bt_add_gaz);
        bt_add_gaz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),act_add_gaz.class));
            }
        });

        lv_gaz.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),act_gaz_details.class);
                intent.putExtra("name",arrayList.get(i).name);
                intent.putExtra("dis",arrayList.get(i).dis);
                intent.putExtra("path",arrayList.get(i).path);
                startActivity(intent);
            }
        });

        //get gaz list
        //check if online is equal to offline
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("gaz");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){

                    //load local
                    SharedPreferences sp = getSharedPreferences("gaz",MODE_PRIVATE);
                    ArrayList<ParseObject> parseObjects = new ArrayList<ParseObject>();
                    for (ParseObject object : objects){
                        parseObjects.add(object);
                    }
                    ArrayList<MyGaz> list = new ArrayList<MyGaz>();
                    int count = sp.getInt("count",0);
                    for (int i=1; i<=count; i++){
                        list.add(new MyGaz(
                                sp.getString("gaz"+i,"")
                                ,sp.getString("dis"+i,"")
                                ,sp.getString(sp.getString("gaz"+i,""),"")
                        ));
                    }

                    Log.d("TAG", "local ="+list.size());
                    Log.d("TAG", "online ="+parseObjects.size());
                    //compare to online version
                    if (parseObjects.size()==list.size()){
                        //check if items are the same
                        for (int j=0; j<list.size(); j++){
                            if (list.get(j).name.equals(parseObjects.get(j).get("name").toString())){
                                isNotEqual=true;
                            }
                        }

                        if (isNotEqual){
                            Log.d("TAG", "not equal");
                            //not the same
                            //replace with the new One
                            updateGaz();
                        } else {
                            Log.d("TAG", "equal");
                            //same .. nothing to do
                        }
                    } else {
                        //not the same
                        //replace with the new One
                        Log.d("TAG", "not equal");
                        updateGaz();
                    }
                    //end of find
                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        //get gaz list
        arrayList = getGazList();

        updateAdapter();

        Log.d("TAG", "onCreate: done every thing");
    }

    private void updateAdapter() {
        if (arrayList.size()==0){
            tv_noGaz.setVisibility(View.VISIBLE);
            lv_gaz.setVisibility(View.INVISIBLE);
        } else {
            lv_gaz.setVisibility(View.VISIBLE);
            tv_noGaz.setVisibility(View.INVISIBLE);

            Adapt_gaz adapt_gaz = new Adapt_gaz(this,arrayList);
            lv_gaz.setAdapter(adapt_gaz);
        }

    }

    private ArrayList<MyGaz> getGazList() {
        ArrayList<MyGaz> arrayList = new ArrayList<MyGaz>();
        SharedPreferences sp = getSharedPreferences("gaz",MODE_PRIVATE);

        int mycount = sp.getInt("count",0);

        for (int i=1; i<=mycount; i++){
            arrayList.add(new MyGaz(
                    sp.getString("gaz"+i,"")
                    ,sp.getString("dis"+i,"")
                    ,sp.getString(sp.getString("gaz"+i,""),"")
            ));
        }

        return arrayList;
    }

    private void updateGaz() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("gaz");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){

                    SharedPreferences sp = getSharedPreferences("gaz",MODE_PRIVATE);
                    SharedPreferences.Editor ed = sp.edit();
                    ed.clear();
                    ed.commit();
                    String dstpath=getCacheDir().getAbsolutePath()+"/";
                    count=0;

                    for (ParseObject object : objects){

                        Log.d("TAG", "count = "+count);
                        object.getParseFile("img").getFileInBackground(new GetFileCallback() {
                            @Override
                            public void done(File file, ParseException e) {
                                count++;
                                if (e==null){
                                    File dstFile = new File(dstpath+object.get("name").toString()+".jpg");
                                    if (dstFile.exists()){
                                        dstFile.delete();
                                    }

                                    try {
                                    FileInputStream inputStream = new FileInputStream(file);
                                    FileOutputStream outputStream = new FileOutputStream(dstFile);
                                    FileChannel inChannel = inputStream.getChannel();
                                    FileChannel outChannel = outputStream.getChannel();
                                    inChannel.transferTo(0,inChannel.size(),outChannel);
                                    inChannel.close();
                                    outChannel.close();
                                    inputStream.close();
                                    outputStream.close();
                                    } catch (FileNotFoundException fileNotFoundException) {
                                        fileNotFoundException.printStackTrace();
                                    } catch (IOException ioException) {
                                        ioException.printStackTrace();
                                    }
                                    Log.d("TAG", "retrieved name = "+object.get("name").toString());
                                    ed.putString("gaz"+count,object.get("name").toString());
                                    ed.putString("dis"+count,object.get("dis").toString());
                                    ed.putString(object.get("name").toString(),dstFile.getAbsolutePath());
                                    ed.putInt("count",count);
                                    ed.commit();
                                    //end of download
                                    if (count==objects.size()){
                                        updateAdapter();
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    //all objects are processed

                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}