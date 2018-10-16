package com.example.anurag.anurag_chat_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Start_Activity extends AppCompatActivity {

  Button reg,malreadyAccountHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_);
        reg=(Button)findViewById(R.id.register);
        malreadyAccountHolder=(Button)findViewById(R.id.login);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Start_Activity.this,RegisterActivity.class);
                startActivity(i);
            }
        });
        malreadyAccountHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Start_Activity.this,LoginActivity.class);
                startActivity(i);
            }
        });
    }
}
