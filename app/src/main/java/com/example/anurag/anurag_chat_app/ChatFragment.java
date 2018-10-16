package com.example.anurag.anurag_chat_app;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private RecyclerView mConvList;
    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabse;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mMainView = inflater.inflate(R.layout.fragment_chat, container, false);
        mConvList = (RecyclerView) mMainView.findViewById(R.id.conv_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);
        mConvDatabase.keepSynced(true);
        mUsersDatabse = FirebaseDatabase.getInstance().getReference().child("users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
        mUsersDatabse.keepSynced(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //arranging the list according to last  message sent
        Query conversationQuery = mConvDatabase.orderByChild("timestamp");
        FirebaseRecyclerAdapter<Conv, ConvViewHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(
                Conv.class,
                R.layout.users_layout,
                ConvViewHolder.class,
                conversationQuery
        ) {
            @Override
            protected void populateViewHolder(final ConvViewHolder viewHolder, final Conv model, int position) {
                final String list_user_id = getRef(position).getKey();

                if (list_user_id.equals(null)) {


                }
                //Toast.makeText(getContext(), "hey wassup", Toast.LENGTH_SHORT).show();
                // Log.d("list_user_id",list_user_id);

                    Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);
                    lastMessageQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            String data = dataSnapshot.child("message").getValue().toString();
                            String type = dataSnapshot.child("type").getValue().toString();
                            //this line simply sends the data and also that message is seen or not
                            viewHolder.setMessage(data, model.isSeen(), type);
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
                    mUsersDatabse.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String username = dataSnapshot.child("name").getValue().toString();
                            String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                            if (dataSnapshot.hasChild("online")) {
                                String userOnline = dataSnapshot.child("online").getValue().toString();
                                viewHolder.setUserOnline(userOnline);
                            }
                            viewHolder.setName(username);
                            viewHolder.setUserImage(userThumb, getContext());

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                    chatIntent.putExtra("user_id", list_user_id);
                                    chatIntent.putExtra("user_name", username);
                                    startActivity(chatIntent);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            ;
        mConvList.setAdapter(firebaseConvAdapter);
        }
        public static class ConvViewHolder extends RecyclerView.ViewHolder {
            View mView;

            public ConvViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
            }

            public void setMessage(String message, boolean isSeen, String type) {
                TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_statu);
                if (type.equals("text")) {
                    userStatusView.setText(message);
                }

                if (type.equals("image")) {
                    userStatusView.setText("image");
                }
                if (!isSeen) {
                    userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);

                } else {
                    userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
                }
            }

            public void setName(String name) {
                TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
                userNameView.setText(name);
            }

            public void setUserImage(String thumb_image, Context ctx) {
                CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_imgeview);
                Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.index).into(userImageView);

            }

            public void setUserOnline(String online_status) {
                ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);
                if (online_status.equals("true")) {
                    userOnlineView.setVisibility(View.VISIBLE);
                } else {
                    userOnlineView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

