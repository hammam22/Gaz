package com.example.gazz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class act_people_details extends AppCompatActivity {

    TextView tv_people_name,tv_people_phone;
    Button bt_remove_people;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_people_details);

        Intent intent = getIntent();
        int pos = intent.getIntExtra("pos",0);
        String name = intent.getStringExtra("name");
        String phone = intent.getStringExtra("phone");
        bt_remove_people = (Button)findViewById(R.id.bt_remove_people);
        bt_remove_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent();
                intent.putExtra("pos",pos);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        tv_people_name=(TextView)findViewById(R.id.tv_people_name);
        tv_people_name.setText(name);
        tv_people_phone=(TextView)findViewById(R.id.tv_people_phone);
        tv_people_phone.setText(phone);
    }
}