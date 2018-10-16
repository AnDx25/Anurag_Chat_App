package com.example.anurag.anurag_chat_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
public class RegisterActivity extends AppCompatActivity {
   TextInputLayout mname,memail,mpassword;
   Button b1;
   FirebaseAuth mAuth;
   private ProgressDialog mRegProgressDialog;
   private DatabaseReference mFirebaseDatabase;
    android.support.v7.widget.Toolbar mtoolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mname=(TextInputLayout)findViewById(R.id.reg_display_name);
        memail=(TextInputLayout)findViewById(R.id.reg_email);
       mpassword=(TextInputLayout)findViewById(R.id.reg_password);
       mtoolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.register_toolbar);
       mRegProgressDialog=new ProgressDialog(this);
       setSupportActionBar(mtoolbar);
       getSupportActionBar().setTitle("CREATE ACCOUNT");
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

               mAuth=FirebaseAuth.getInstance();
        b1=(Button)findViewById(R.id.reg_button);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String name=mname.getEditText().getText().toString().trim();
               String email=memail.getEditText().getText().toString().trim();
               String password=mpassword.getEditText().getText().toString().trim();
               register_user(name,email,password);
               //this part of code will run when the fields are not empty
               if(!TextUtils.isEmpty(name)|| !TextUtils.isEmpty(email)|| !TextUtils.isEmpty(password))
               {
                   mRegProgressDialog.setTitle("Registering user");
                   mRegProgressDialog.setMessage("please wait while we are registering You");
                   mRegProgressDialog.setCanceledOnTouchOutside(true);
                   mRegProgressDialog.show();
               }
            }
        });
    }
    private  void register_user(final String name,String email,String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String deviceToken= FirebaseInstanceId.getInstance().getToken();
                            FirebaseUser currentuser=FirebaseAuth.getInstance().getCurrentUser();
                            String uid=currentuser.getUid();
                            mFirebaseDatabase=FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                            HashMap<String,String> userMap=new HashMap<>();
                            userMap.put("name",name);
                            userMap.put("status","hi there I m using chatapp");
                            userMap.put("image","default");
                            userMap.put("thumb_image","default");
                            userMap.put("device_token",deviceToken);
                            mFirebaseDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        mRegProgressDialog.dismiss();
                                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });

                            // Sign in success, update UI with the signed-in user's information
                            /**/
                        }
                        // If sign in fails, display a message to the user.
                        else
                        {
                            mRegProgressDialog.hide();
                            Toast.makeText(RegisterActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
