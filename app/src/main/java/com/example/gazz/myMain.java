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

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class myMain extends AppCompatActivity {

    TextView tvmain,tvlogout;
    EditText edAddToDB;
    Button btAdToDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvmain = (TextView)findViewById(R.id.tvmain);
        btAdToDB=(Button)findViewById(R.id.btAdToDB);
        tvlogout= (TextView)findViewById(R.id.tvlogout);
        edAddToDB=(EditText)findViewById(R.id.edAddToDB);
        /*ParseObject firstObject = new ParseObject("FirstClass");
        firstObject.put("message","Hey ! First message from android. Parse is now connected");
        firstObject.saveInBackground(e -> {
            if (e != null){
                Log.e("MainActivity", e.getLocalizedMessage());
            }else{
                Log.d("MainActivity","Object saved.");
            }
        });

        ParseInstallation.getCurrentInstallation().saveInBackground();*/

        ParseUser user = ParseUser.getCurrentUser();
        tvmain = (TextView)findViewById(R.id.tvmain);
        tvmain.setText(user.getEmail());

        tvlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("islogin",false);
                editor.commit();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });

        btAdToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseObject object = new ParseObject("myClass");
                object.put("note",edAddToDB.getText().toString());
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null){
                            Toast.makeText(getApplicationContext(),"saved",Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



    }
}