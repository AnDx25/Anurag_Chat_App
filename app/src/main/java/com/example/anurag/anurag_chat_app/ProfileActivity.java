package com.example.anurag.anurag_chat_app;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
TextView mProfilename,mProfileStatus,mFriendCount;
ImageView mProfileimage;
Button mProfileSendReqBtn,mDeclineBtn;
DatabaseReference mdatabaseref;
DatabaseReference mNotification;
ProgressDialog mProgressDialog;
private String mCurrentState;
DatabaseReference SendFriendReqDatabase;
DatabaseReference mFriendDatabase;
FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String uid= getIntent().getStringExtra("from_user_id");
        Toast.makeText(this, uid, Toast.LENGTH_SHORT).show();
        mdatabaseref= FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        SendFriendReqDatabase=FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotification=FirebaseDatabase.getInstance().getReference().child("Notifications");
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        mProfilename=(TextView)findViewById(R.id.profile_display_name);
        mProfileStatus=(TextView)findViewById(R.id.profile_status);
        mFriendCount=(TextView)findViewById(R.id.user_count);
        mProfileimage=(ImageView)findViewById(R.id.profile_imageview);
        mProfileSendReqBtn=(Button)findViewById(R.id.friend_re_btn);
        mDeclineBtn=(Button)findViewById(R.id.declinebtn);
        mCurrentState="not friends";
        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setEnabled(false);
        //mProfilename.setText(uid);
        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("please wait while we load the user data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        mdatabaseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //this method is to retrive the database
                String displayname=dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                mProfilename.setText(displayname);
                mProfileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.placeholder_default_avatar).fit().into(mProfileimage);

                //---Friend List/Request feature
                SendFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(uid)) {
                            String req_type = dataSnapshot.child(uid).child("request_type").getValue().toString();
                            if (req_type.equals("received")) {
                                mCurrentState = "req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request");
                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(true);
                            } else if (req_type.equals("sent"))
                            {
                                mCurrentState="req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Requset");
                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);
                            }
                        }
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mProgressDialog.dismiss();
                    }
                });


                mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(uid))
                        {
                            mCurrentState="friends";
                            mProfileSendReqBtn.setText("Unfriend This Person");
                            mDeclineBtn.setVisibility(View.INVISIBLE);
                            mDeclineBtn.setEnabled(false);
                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mProgressDialog.dismiss();
                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(mCurrentUser.getUid().equals(uid))
        {
            mProfileSendReqBtn.setEnabled(false);
            mProfileSendReqBtn.setText(" ");
        }
        //SEND FRIEND REQUEST
        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProfileSendReqBtn.setEnabled(false);
                //now going to check the current state if it is not friend then going to send
                //friend request to current opened user
                if(mCurrentState.equals("not friends"))
                {
                   SendFriendReqDatabase.child(mCurrentUser.getUid()).child(uid).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful())
                           {
                               SendFriendReqDatabase.child(uid).child(mCurrentUser.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                       HashMap<String,String> notificationdata=new HashMap<>();
                                       notificationdata.put("from",mCurrentUser.getUid());
                                       notificationdata.put("type","request");
                                       mNotification.child(uid).push().setValue(notificationdata).addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
                                               Toast.makeText(ProfileActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                                               mProfileSendReqBtn.setEnabled(true);
                                               mCurrentState="req_sent";
                                               mProfileSendReqBtn.setText("Cancel Friend Request");
                                               mDeclineBtn.setVisibility(View.INVISIBLE);
                                               mDeclineBtn.setEnabled(false);
                                               /*Databse Structure for Notification
                                               *
                                               *
                                               * Notification
                                               *   ->UserId who have received notification
                                               *      ->Key of Notification
                                               *          ->Notifiaction from
                                               *          ->notification type
                                               *          now to enable send request feature
                                               *          use FireBase Functions and to use them First Install Node.js*/

                                           }
                                       });

                                   }
                               });
                           }
                           else
                           {
                               Toast.makeText(ProfileActivity.this, "Failed Sending Request", Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
                }
                //CANCEL FRIEND REQUEST
                if(mCurrentState.equals("req_sent"))
                {
                    SendFriendReqDatabase.child(mCurrentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                         SendFriendReqDatabase.child(uid).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void aVoid) {
                                 mProfileSendReqBtn.setEnabled(true);
                                 mCurrentState="not_friends";
                                 mProfileSendReqBtn.setText("Send Friend Request");
                                 mDeclineBtn.setVisibility(View.INVISIBLE);
                                 mDeclineBtn.setEnabled(false);
                             }
                         });
                        }
                    });

                    mNotification.child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileActivity.this, "Request cancelled", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                //accept friend request
                if(mCurrentState.equals("req_received"))
                {
                   final String currentDate= DateFormat.getDateInstance().format(new Date());
                   mFriendDatabase.child(mCurrentUser.getUid()).child(uid).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           mFriendDatabase.child(uid).child(mCurrentUser.getUid()).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   SendFriendReqDatabase.child(mCurrentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {
                                           SendFriendReqDatabase.child(uid).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                               @Override
                                               public void onSuccess(Void aVoid) {
                                                   mProfileSendReqBtn.setEnabled(true);
                                                   mCurrentState="friends";
                                                   mProfileSendReqBtn.setText("Unfriend This Person");
                                                   mDeclineBtn.setVisibility(View.INVISIBLE);
                                                   mDeclineBtn.setEnabled(false);
                                               }
                                           });
                                       }
                                   });
                               }
                           });
                       }
                   });
                }
                if(mCurrentState.equals("friends"))
                {
                    mFriendDatabase.child(mCurrentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                          mFriendDatabase.child(uid).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void aVoid) {
                                 mProfileSendReqBtn.setEnabled(true);
                                 mCurrentState="not friends";
                                 mProfileSendReqBtn.setText("Send Friend Request");
                              }
                          });
                        }
                    });
                }

            }
        });
    }
}
