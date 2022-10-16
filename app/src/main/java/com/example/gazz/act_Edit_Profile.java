package com.example.gazz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class act_Edit_Profile extends AppCompatActivity {

    EditText ed_edit_pro_name,ed_edit_pro_phone;
    Button bt_edit_pro_save;
    String name="",num="";
    MyUserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_edit_profile);

        userInfo = getUserInfoFromSP();

        ed_edit_pro_name = (EditText)findViewById(R.id.ed_edit_pro_name);
        ed_edit_pro_phone= (EditText)findViewById(R.id.ed_edit_pro_phone);

        bt_edit_pro_save = (Button)findViewById(R.id.bt_edit_pro_save);
        bt_edit_pro_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed_edit_pro_name.getText().toString().isEmpty()||ed_edit_pro_phone.getText().toString().isEmpty()){
                    //empty fields
                    Toast.makeText(getApplicationContext(),"يرجى ملأ الحقول",Toast.LENGTH_SHORT).show();
                }else {
                    //fields not empty
                    //good
                    if (ed_edit_pro_phone.getText().toString().length()!=10){
                        //wrong phone num
                        Toast.makeText(getApplicationContext(),"رقم الهاتف خطائ",Toast.LENGTH_SHORT).show();
                    } else {
                        //valid num
                        //good
                        name = ed_edit_pro_name.getText().toString();
                        num = ed_edit_pro_phone.getText().toString();
                        userInfo.name=name;
                        userInfo.phone=num;

                        //save online

                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("users");
                        query.whereEqualTo("email",userInfo.email);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                for (ParseObject object : objects){

                                    object.put("name",name);
                                    object.put("phone",num);
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e==null){
                                                //no error
                                                //save local
                                                userInfo.saveToSP(getApplicationContext());

                                                //go back
                                                Intent intent = getIntent();
                                                setResult(RESULT_OK,intent);
                                                finish();
                                            } else {
                                                //connection error
                                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                    //first end
                                }


                            }
                        });

                        //end


                    }
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