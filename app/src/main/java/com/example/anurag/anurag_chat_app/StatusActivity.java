package com.example.anurag.anurag_chat_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
private android.support.v7.widget.Toolbar mToolbar;
private TextInputLayout mStatus;
private Button mSaveStatus;
private DatabaseReference mDatabase;
private FirebaseUser mCurrentUser;
private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        String s= getIntent().getStringExtra("status");
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=mCurrentUser.getUid();
        mProgressDialog=new ProgressDialog(this);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);
        mToolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.status_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ACCOUNT TITLE");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mStatus=(TextInputLayout)findViewById(R.id.Status_Input_Layout);
        mStatus.getEditText().setText(s);
        mSaveStatus=(Button)findViewById(R.id.save_status_button);
        mSaveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog.setTitle("Status");
                mProgressDialog.setMessage("Saving Your Status");
                mProgressDialog.show();
                String status1=mStatus.getEditText().getText().toString();
                mDatabase.child("status").setValue(status1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            mProgressDialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(StatusActivity.this, "error occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        });
    }
}
