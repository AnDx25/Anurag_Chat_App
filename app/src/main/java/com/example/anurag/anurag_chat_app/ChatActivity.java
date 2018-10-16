package com.example.anurag.anurag_chat_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.anurag.anurag_chat_app.SettingsActivity.PICK_IMAGE;

public class ChatActivity extends AppCompatActivity {
private String mChatUser;
private String musername;
private Toolbar mChatToolbar;
private DatabaseReference muserdatabase;
private TextView mUserTitle;
private TextView mLastSeen;
private CircleImageView mUserImage;
private FirebaseAuth mAuth;
private String mcurrentuser;
private ImageButton mChataddbtn,mchatsendbtn;
private EditText mChatView;
RecyclerView mMessageList;
private SwipeRefreshLayout mRefreshLayout;
private List<Messages> mmessagelist=new ArrayList<>();
private LinearLayoutManager mLinearLayout;
private MessageAdapter mAdapter;
private DatabaseReference mMessageRef;
private static final int Total_items_to_load=10;
private int mCurrentPage =1;
private int itempos=0;
private String mLastKey="";
private String mPrevKey="";
private StorageReference mImageStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        muserdatabase=FirebaseDatabase.getInstance().getReference();
        mChatToolbar=(Toolbar)findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);
        mImageStorage= FirebaseStorage.getInstance().getReference();

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        mChatUser=getIntent().getStringExtra("user_id");
        musername=getIntent().getStringExtra("user_name");
        mAuth=FirebaseAuth.getInstance();
        mcurrentuser=mAuth.getCurrentUser().getUid();
        //mMessageRef=FirebaseDatabase.getInstance().getReference();
       // getSupportActionBar().setTitle(musername);

//now since in chat activity with the name of users we also want to display their
        //image so make another layout file named chat_custom_bar;

        LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view=inflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);
        mUserImage=(CircleImageView)findViewById(R.id.custom_bar_image);
        mUserTitle=(TextView)findViewById(R.id.custom_bar_title);
        mLastSeen=(TextView)findViewById(R.id.custom_bar_seen);
        mChataddbtn=(ImageButton)findViewById(R.id.chat_add_btn);
        mchatsendbtn=(ImageButton)findViewById(R.id.chat_mesg_send);
        mChatView=(EditText)findViewById(R.id.chat_message_view);
        mMessageList=(RecyclerView)findViewById(R.id.messgaes_list);
        mRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.messages_swipe_layout);
     mAdapter= new MessageAdapter(mmessagelist);
 mMessageList=(RecyclerView)findViewById(R.id.messgaes_list);
 mLinearLayout=new LinearLayoutManager(this);
 mMessageList.setHasFixedSize(true);
 mMessageList.setLayoutManager(mLinearLayout);

mMessageList.setAdapter(mAdapter);
loadMessages();
        mUserTitle.setText(musername);
        muserdatabase.child("users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online=dataSnapshot.child("online").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                if(online.equals("true"))
                {
                    mLastSeen.setText("online");
                }
                else
                {
                    GetTimeAgo getTimeAgo=new GetTimeAgo();
                    long lastTime=Long.parseLong(online);
                    String lastSeenTime=getTimeAgo.getTimeAgo(lastTime,getApplicationContext());
                    mLastSeen.setText(lastSeenTime);
                }

                Picasso.with(ChatActivity.this).load(image).placeholder(R.drawable.index).fit().into(mUserImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        muserdatabase.child("Chat").child(mcurrentuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              if(!dataSnapshot.hasChild(mChatUser))
              {
                  Map chatAdddMap=new HashMap();
                  chatAdddMap.put("seen",false);
                  chatAdddMap.put("timestamp", ServerValue.TIMESTAMP);

                  Map chatUserMap=new HashMap();
                  chatUserMap.put("Chat/"+mcurrentuser+"/"+mChatUser,chatAdddMap);
                  chatUserMap.put("Chat/"+mChatUser+"/"+mcurrentuser,chatAdddMap);
                  muserdatabase.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                      @Override
                      public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                          if(databaseError!=null)
                          {
                            Log.d("Chat_Log",databaseError.getMessage());
                          }
                      }
                  });

              }else
              {

              }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mchatsendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        mChataddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              mCurrentPage++;
              itempos=0;
              //since when it refreshes for previous messages then it adds another page of 10 messages along withprevious one
                //so it will display 20 messages thats why clear the list then load the messages;
              //mmessagelist.clear();
              loadMoreMessages();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
            Uri uri=data.getData();
            final String current_user_ref="messages/"+mcurrentuser+ "/"+mChatUser;
            final String chat_user_ref="messages/"+mChatUser+"/"+mcurrentuser;
        DatabaseReference user_message_push=muserdatabase.child("messages").child(mcurrentuser).child(mChatUser).push();

        final String push_id=user_message_push.getKey();
            StorageReference filepath=mImageStorage.child("message_image").child(push_id+ ".jpg");
            filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                   if(task.isSuccessful())
                   {
                       String download_url=task.getResult().getDownloadUrl().toString();
                       Map messageMap=new HashMap();
                       messageMap.put("message",download_url);
                       messageMap.put("seen",false);
                       messageMap.put("type","image");
                       messageMap.put("time",ServerValue.TIMESTAMP);
                       messageMap.put("from",mcurrentuser);
                       Map messageUserMap=new HashMap();
                       messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
                       messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);
                       muserdatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                           @Override
                           public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                           }
                       });
                   }
                }
            });
        }
    }

    private void loadMoreMessages() {
        DatabaseReference messageRef=muserdatabase.child("messages").child(mcurrentuser).child(mChatUser);
        Query messageQuery=messageRef.orderByKey().endAt(mLastKey).limitToLast(10);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages=dataSnapshot.getValue(Messages.class);

                String message_key=dataSnapshot.getKey();
                if (itempos == 1)
                {

                    mLastKey=message_key;
                }
                if(mPrevKey.equals(message_key))
                {
                    mmessagelist.add(itempos++,messages);
                }else {
                    mPrevKey=mLastKey;
                }
               // Log.d("TOTALKEYS","Las")
               // mmessagelist.add(itempos++,messages);
                mAdapter.notifyDataSetChanged();
                //mMessageList.scrollToposition();
                //this line always sends our recycler view to bottom of page
                mRefreshLayout.setRefreshing(false);
            mLinearLayout.scrollToPositionWithOffset(10,0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadMessages() {
        DatabaseReference messageRef=muserdatabase.child("messages").child(mcurrentuser).child(mChatUser);
        //this is to display only 5 messages on main screen
        Query messageQuery=messageRef.limitToLast(Total_items_to_load);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages messages = dataSnapshot.getValue(Messages.class);
                itempos++;
                if (itempos == 1)
                {
                  String message_key=dataSnapshot.getKey();
                  mLastKey=message_key;
                  mPrevKey=message_key;
                }
                mmessagelist.add(messages);
                mAdapter.notifyDataSetChanged();
                //mMessageList.scrollToposition();
                //this line always sends our recycler view to bottom of page
                mMessageList.scrollToPosition(mmessagelist.size()-1);
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String message = mChatView.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            String mcurrent_user_ref = "messages/" + mcurrentuser + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mcurrentuser;
            DatabaseReference user_message_push=muserdatabase.child("messages").child(mcurrentuser).child(mChatUser).push();
            String push_id=user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put( "message", message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",mcurrentuser);

            Map messageUserMap=new HashMap();
            messageUserMap.put(mcurrent_user_ref+ "/"+push_id,messageMap);
            messageUserMap.put(chat_user_ref+ "/"+push_id,messageMap);
           mChatView.setText("");
            muserdatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {

                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                 // Log.d("CHAT_LOG",databaseError.getMessage().toString());
                }
            });
        }
        //to retrive message make another layout resource file to add the basic layout
        //to show our message named message_single_layout
        //then create a layout file inside the drawable to set the message textview background
        //then create a java model class called Message
        //then create an adapter class called MessageAdapter
    }
}
