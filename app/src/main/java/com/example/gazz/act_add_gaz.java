package com.example.gazz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

public class act_add_gaz extends AppCompatActivity {

    Button bt_new_gaz_save,bt_new_gaz_chooseImg;
    EditText ed_new_gaz_name,ed_new_gaz_price;
    ImageView iv_new_gaz;
    Uri uri;
    boolean isFound=false;
    int requestForImage=190;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_add_gaz);

        iv_new_gaz=(ImageView)findViewById(R.id.iv_new_gaz);

        ed_new_gaz_name=(EditText)findViewById(R.id.ed_new_gaz_name);
        ed_new_gaz_price=(EditText)findViewById(R.id.ed_new_gaz_price);

        bt_new_gaz_save=(Button)findViewById(R.id.bt_new_gaz_save);
        bt_new_gaz_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String add2 = ed_new_gaz_name.getText().toString()+".jpg";


                File dstfile = new File(getCacheDir().getAbsolutePath(),add2);
                File srcfile = new File(getImageFilePath(uri));


                //must check the permission if it's  granted or not

                try {
                    FileInputStream inputStream = new FileInputStream(srcfile);
                    FileOutputStream outputStream = new FileOutputStream(dstfile);
                    FileChannel inChannel = inputStream.getChannel();
                    FileChannel outChannel = outputStream.getChannel();
                    inChannel.transferTo(0,inChannel.size(),outChannel);
                    inChannel.close();
                    outChannel.close();


                    /*File disc = new File(getCacheDir().getAbsolutePath());
                    File[] files = disc.listFiles();

                    String temp="";
                    for (int i=0; i<files.length; i++){
                        temp=temp+files[i].getName()+"\n";
                    }

                    Toast.makeText(getApplicationContext(),temp,Toast.LENGTH_LONG).show();
                    Log.d(" files", temp);*/

                    //check if it's added before
                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("gaz");
                    query.whereEqualTo("name",ed_new_gaz_name.getText().toString());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e==null){
                                for (ParseObject object : objects){
                                    if (object.get("name").equals(ed_new_gaz_name.getText().toString())){
                                        isFound=true;
                                    }
                                }

                                if (isFound){
                                    isFound=false;
                                    Toast.makeText(getApplicationContext(),"موجود مسبقاً",Toast.LENGTH_SHORT).show();
                                } else {
                                    //uniqe name ,, add it
                                    ParseObject object = new ParseObject("gaz");
                                    ParseFile parseFile = new ParseFile(dstfile);
                                    object.put("name",ed_new_gaz_name.getText().toString());
                                    object.put("dis",ed_new_gaz_price.getText().toString());
                                    object.put("img",parseFile);
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e==null){
                                                //save offline
                                                SharedPreferences sp = getSharedPreferences("gaz",MODE_PRIVATE);
                                                SharedPreferences.Editor ed = sp.edit();
                                                int count = sp.getInt("count",0);
                                                count++;
                                                ed.putString("gaz"+count,ed_new_gaz_name.getText().toString());
                                                ed.putString("dis"+count,ed_new_gaz_price.getText().toString());
                                                ed.putString(ed_new_gaz_name.getText().toString()
                                                        ,getCacheDir().getAbsolutePath()+add2);
                                                ed.putInt("count",count);
                                                ed.commit();
                                                finish();
                                                //end of save
                                            } else {
                                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                //end of find
                            } else {
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.d("TAG1", e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("TAG2", e.getMessage());
                }

            }
        });

        bt_new_gaz_chooseImg=(Button)findViewById(R.id.bt_new_gaz_chooseImg);
        bt_new_gaz_chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"إختر صورة"),requestForImage);
            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==requestForImage&&resultCode==RESULT_OK&&data!=null){
            uri = data.getData();
            iv_new_gaz.setImageURI(uri);

        }
    }

    public String getImageFilePath(Uri uri) {
        File file = new File(uri.getPath());
        String[] filePath = file.getPath().split(":");
        String image_id = filePath[filePath.length - 1];
        Cursor cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , null, MediaStore.Images.Media._ID + " = ? "
                , new String[]{image_id}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            @SuppressLint("Range") String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
            return imagePath;
        }
        return null;
    }
}