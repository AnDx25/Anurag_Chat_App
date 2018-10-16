package com.example.anurag.anurag_chat_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
 Toolbar mLoginToolbar;
 Button login;
 FirebaseAuth mAuth;
 TextInputLayout memail,mpassword;
 DatabaseReference mdatabaseref;
 private ProgressDialog mLoginProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginToolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mLoginToolbar);
        mAuth=FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("LOGIN");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        login=(Button)findViewById(R.id.Login_button);
        memail=(TextInputLayout)findViewById(R.id.Login_email);
        mpassword=(TextInputLayout)findViewById(R.id.Login_password);
        mdatabaseref= FirebaseDatabase.getInstance().getReference().child("users");
        mLoginProgressDialog=new ProgressDialog(this);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=memail.getEditText().getText().toString();
                String pass=mpassword.getEditText().getText().toString();
                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass))
                {
                    mLoginProgressDialog.setTitle("LOGGING IN");
                    mLoginProgressDialog.setMessage("Please wait while checkin credentials");
                    mLoginProgressDialog.setCanceledOnTouchOutside(false);
                    mLoginProgressDialog.show();
                    loginUser(email,pass);
                }
            }

            private void loginUser(String email, String pass) {
                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       // mLoginProgressDialog.dismiss();Error: Error reading rules file firestore.rules

                        if(task.isSuccessful())
                        {
                            String current_uid=mAuth.getCurrentUser().getUid();
                            //creating the token Id
                            String deviceToken= FirebaseInstanceId.getInstance().getToken();
                            //storing the tokenid
                            mdatabaseref.child(current_uid).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent i=new Intent(LoginActivity.this,MainActivity.class);
                                    //since when we were logged in to main activty then after pressing back button we were directing
                                    //to start activity so to close the application after pressing back button
                                    //we clearing all the previous intents
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    startActivity(i);
                                    finish();
                                }
                            });
                           // mLoginProgressDialog.dismiss();

                        }
                        else
                        {mLoginProgressDialog.hide();
                            Toast.makeText(LoginActivity.this, "LOGIN FAILED", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
