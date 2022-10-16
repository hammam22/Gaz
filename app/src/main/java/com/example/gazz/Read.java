package com.example.gazz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class Read extends AppCompatActivity {

    ListView lvread;
    ParseQuery<ParseObject> query;
    ArrayList<String> list;
    ArrayList<ParseObject> myobj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_read);

        lvread =(ListView)findViewById(R.id.lvread);

        query = new ParseQuery<ParseObject>("myClass");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    list = new ArrayList<String>();
                    myobj = new ArrayList<ParseObject>();
                    for (ParseObject object : objects){
                        list.add(object.get("note").toString());
                        myobj.add(object);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getApplicationContext()
                            , R.layout.simple_list_item_1
                            , list);
                    lvread.setAdapter(adapter);
                    lvread.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String objectid = myobj.get(i).getObjectId();
                            Log.d("LV", "onItemClick: no "+i);
                            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("myClass");
                            query.getInBackground(myobj.get(i).getObjectId() ,new GetCallback<ParseObject>(){
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    Log.d("LV", "done 1");

                                    object.put("note","- - - -");
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Log.d("LV", "done 2");

                                            Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                            
                        }
                    });


                    lvread.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                            String objectid= myobj.get(i).getObjectId();
                            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("myClass");
                            query.getInBackground(objectid, new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    object.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Toast.makeText(getApplicationContext(),"deleted",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                            return true;
                        }
                    });
                }
            }
        });


    }
}