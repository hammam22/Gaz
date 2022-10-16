package com.example.gazz;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.squareup.picasso.Picasso;


public class Register extends AppCompatActivity {

    Button btRegister;
    EditText EdRegisterPass,EdRegisterEmail,EdRegisterPassCheck,EdRegisterUserName;
    TextView tvToLogin;
    ImageView ivneibor, ivseller,ivclient,checkSeller,checkClient,checkNeighbor;
    LinearLayout LinearSeller,LinearClient,LinearNeighbor;
    int SELECTED=0;
    boolean isReady=false;
    String newcat="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register);

        attachingViews();
        emplyingViews();

    }

    private void attachingViews() {
        btRegister = (Button)findViewById(R.id.btRegister);
        EdRegisterEmail = (EditText)findViewById(R.id.EdRegisterEmail);
        EdRegisterPass = (EditText)findViewById(R.id.EdRegisterPass);
        EdRegisterPassCheck = (EditText)findViewById(R.id.EdRegisterPassCheck);
        EdRegisterUserName = (EditText)findViewById(R.id.EdRegisterUserName);
        tvToLogin = (TextView)findViewById(R.id.tvToLogin);
        ivclient = (ImageView) findViewById(R.id.ivclient);
        ivseller = (ImageView) findViewById(R.id.ivseller);
        ivneibor = (ImageView)findViewById(R.id.ivneighbor);
        checkClient = (ImageView)findViewById(R.id.checkClient);
        checkNeighbor = (ImageView)findViewById(R.id.checkNieghbor);
        checkSeller = (ImageView)findViewById(R.id.checkSeller);
        LinearClient = (LinearLayout)findViewById(R.id.LinearClient);
        LinearSeller = (LinearLayout)findViewById(R.id.LinearSeller);
        LinearNeighbor = (LinearLayout)findViewById(R.id.LinearNieghbor);
    }

    private void emplyingViews() {
        tvToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, myMain.class));
                finish();
                return;
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutFirst();
              if (isReady){
                String username, password, email;
                username = EdRegisterUserName.getText().toString();
                email = EdRegisterEmail.getText().toString();
                password = EdRegisterPass.getText().toString();
                if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                    //empty fields , please fill it
                    Toast.makeText(getApplicationContext(), "إملأ الخانات رجاءً", Toast.LENGTH_SHORT).show();
                } else {
                    // fields are filled , ready to go

                    if (password.length() < 6 || username.length() < 3) {
                        Toast.makeText(getApplicationContext(), "كلمة المرور او اسم المستخدم إقل من 6 خانات", Toast.LENGTH_SHORT).show();
                    } else {
                        //good password
                        if (SELECTED == 0) {
                            Toast.makeText(getApplicationContext(), "إختر فئة من الفئات في الأعلى", Toast.LENGTH_SHORT).show();
                        } else {
                            //type chosen , all good

                            //now register

                            ParseUser user = new ParseUser();
                            user.setUsername(username);
                            user.setEmail(email);
                            user.setPassword(password);
                            user.signUpInBackground(new SignUpCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.d("sign up", "done: ");
                                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putBoolean("islogin", true);
                                        if (SELECTED==1){
                                            editor.putString("cat", "nei");
                                            newcat = "nei";
                                        } else if (SELECTED==2){
                                            editor.putString("cat","cli" );
                                            newcat = "cli";
                                        } else if (SELECTED==3){
                                            editor.putString("cat", "sel");
                                            newcat= "sel";
                                        }
                                        editor.commit();

                                        //save data
                                        ParseUser user1 = ParseUser.getCurrentUser();


                                        //save data online
                                        ParseObject parseObject = new ParseObject("users");
                                        parseObject.put("myid",username);
                                        parseObject.put("name","-1");
                                        parseObject.put("email",email);
                                        parseObject.put("password",password);
                                        parseObject.put("phone","-1");
                                        parseObject.put("cat",newcat);
                                        parseObject.put("nei","-1");
                                        parseObject.put("state","0");
                                        parseObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {

                                                if (e==null){
                                                    //saved in db
                                                    //keep
                                                    //save data offline
                                                    MyUserInfo myUserInfo = new MyUserInfo(
                                                            user1.getObjectId(),
                                                            "-1",email,password,"-1",newcat,"-1","0");
                                                    myUserInfo.saveToSP(getApplicationContext());

                                                    //move to dashboard
                                                    if (SELECTED==1){
                                                        startActivity(new Intent(getApplicationContext(), act_Nei.class));
                                                    } else if (SELECTED==2){
                                                        startActivity(new Intent(getApplicationContext(), act_Cli.class)); }
                                                    else if (SELECTED==3){
                                                        startActivity(new Intent(getApplicationContext(), act_Sel.class));
                                                    }
                                                    finish();
                                                    //done

                                                } else {
                                                    //error saving
                                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });




                                    } else {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.d("TAG", "error: " + e.getMessage());
                                    }
                                }
                            });
                        }
                        //done

                    }
                }
            } else {
                  //not logged out
                  logOutFirst();
              }

            }
        });



        Picasso.with(this).load(R.drawable.cys).fit().into(ivseller);
        Picasso.with(this).load(R.drawable.client).fit().into(ivclient);
        Picasso.with(this).load(R.drawable.neighbor).fit().into(ivneibor);

        LinearNeighbor.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                LinearNeighbor.setBackground(getDrawable(R.drawable.item_corner_selected));
                checkNeighbor.setVisibility(View.VISIBLE);
                SELECTED=1;
                LinearClient.setBackground(getDrawable(R.drawable.item_corner));
                LinearSeller.setBackground(getDrawable(R.drawable.item_corner));
                checkClient.setVisibility(View.INVISIBLE);
                checkSeller.setVisibility(View.INVISIBLE);
            }
        });

        LinearClient.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                LinearClient.setBackground(getDrawable(R.drawable.item_corner_selected));
                checkClient.setVisibility(View.VISIBLE);
                SELECTED=2;
                LinearNeighbor.setBackground(getDrawable(R.drawable.item_corner));
                LinearSeller.setBackground(getDrawable(R.drawable.item_corner));
                checkNeighbor.setVisibility(View.INVISIBLE);
                checkSeller.setVisibility(View.INVISIBLE);
            }
        });

        LinearSeller.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                LinearSeller.setBackground(getDrawable(R.drawable.item_corner_selected));
                checkSeller.setVisibility(View.VISIBLE);
                SELECTED=3;
                LinearClient.setBackground(getDrawable(R.drawable.item_corner));
                LinearNeighbor.setBackground(getDrawable(R.drawable.item_corner));
                checkClient.setVisibility(View.INVISIBLE);
                checkNeighbor.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void addNeiList() {

        /*HashMap<String,Integer> map = new HashMap<>();

        database.getReference().child("neis").child("count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    count = (int) snapshot.getValue();
                } catch (Exception e) {
                    count=0;
                    map.put("count",0);
                    database.getReference().child("neis").setValue(map);
                }

                //send count and keep going
                map.put("count",count++);

                database.getReference().child("neis").setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
                //done the end
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/



        if (SELECTED==1){

                    /*goto dashboard
                    Intent intent = new Intent(getApplicationContext(),act_Nei.class);
                    startActivity(intent);
                    finish();
                    return;*/


        }else if (SELECTED==2){

                    /*goto dashboard
                    Intent intent = new Intent(getApplicationContext(),act_Cli.class);
                    startActivity(intent);
                    finish();
                    return;*/

        }
        else if (SELECTED==3){

                    /*goto dashboard
                    Intent intent = new Intent(getApplicationContext(),act_Sel.class);
                    startActivity(intent);
                    finish();
                    return;*/
        }


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
}