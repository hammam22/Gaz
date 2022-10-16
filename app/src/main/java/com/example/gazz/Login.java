package com.example.gazz;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class Login extends AppCompatActivity {

    Button btLogin;
    EditText EdLoginPass,EdLoginUsername;
    TextView tvToRegister;
    boolean isReady=false;

    String myUsername="",myPass="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);



        /*
        * need to go to Dashboard if last time signed in
        * */
        //checklastlogin();
        attachingViews();
        emplyingViews();

    }

    private void logOutFirst() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    isReady=true;
                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*private void checklastlogin() {
        SharedPreferences sp = getSharedPreferences("lastlogin",MODE_PRIVATE);
        Boolean islog = sp.getBoolean("islog",false);

        if (islog){
            String cat = sp.getString("cat","");
            if (cat=="nei"){
                startActivity(new Intent(getApplicationContext(),act_Nei.class));
                finish();
                return;
            }else if (cat=="cli"){
                startActivity(new Intent(getApplicationContext(),act_Cli.class));
                finish();
                return;
            }
            else if (cat=="sel"){
                startActivity(new Intent(getApplicationContext(),act_Sel.class));
                finish();
                return;
            }else {
                Toast.makeText(this,"فشل في الحصول على نوع المستخدم",Toast.LENGTH_SHORT).show();
            }


        }
    }*/

    private void attachingViews() {
        btLogin = (Button)findViewById(R.id.btLogin);
        EdLoginUsername= (EditText)findViewById(R.id.EdLoginUsername);
        EdLoginPass = (EditText)findViewById(R.id.EdLoginPass);
        tvToRegister = (TextView)findViewById(R.id.tvToRegister);
    }

    private void emplyingViews() {
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginNow();
            }
        });

        tvToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

    private void loginNow(){
        myUsername = EdLoginUsername.getText().toString();
        myPass = EdLoginPass.getText().toString();

        logOutFirst();
        if (isReady){
            //ready to
        ParseUser.logInInBackground(myUsername, myPass, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e==null){
                    Log.d("from login", "To main");
                    SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("islogin",true);
                    editor.commit();

                    MyUserInfo myUserInfo = new MyUserInfo();
                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("users");
                    query.whereEqualTo("myid",myUsername);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e==null){
                                for (ParseObject object : objects){
                                    if (object.get("myid").equals(myUsername)){
                                        myUserInfo.myid=myUsername;
                                        myUserInfo.name=object.get("name").toString();
                                        myUserInfo.password=object.get("password").toString();
                                        myUserInfo.email=object.get("email").toString();
                                        myUserInfo.cat=object.get("cat").toString();
                                        myUserInfo.nei=object.get("nei").toString();
                                        myUserInfo.state=object.get("state").toString();
                                        myUserInfo.phone=object.get("phone").toString();
                                        myUserInfo.saveToSP(getApplicationContext());
                                    }




                                }

                                if (myUserInfo.cat.equals("nei")){
                                    startActivity(new Intent(getApplicationContext(),act_Nei.class));
                                    finish();
                                } else if (myUserInfo.cat.equals("sel")){
                                    startActivity(new Intent(getApplicationContext(),act_Sel.class));
                                    finish();
                                } else if (myUserInfo.cat.equals("cli")){
                                    startActivity(new Intent(getApplicationContext(),act_Cli.class));
                                    finish();
                                }
                                return;
                                //end of find
                            } else {
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        });
        }else {
            //logout first
            logOutFirst();
        }

        //later should determine the kind of user to navigate to dashboard

        

    }
}