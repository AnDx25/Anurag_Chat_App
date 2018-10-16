package com.example.anurag.anurag_chat_app;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    private DatabaseReference mFriendDatabse;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;
    RecyclerView mFriendList;
    private String current_userid;
    private View mMainView;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        mFriendList = (RecyclerView) mMainView.findViewById(R.id.friend_list);
        mAuth = FirebaseAuth.getInstance();
        current_userid = mAuth.getCurrentUser().getUid();
       mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("users");
      mFriendDatabse = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_userid);mFriendDatabse.keepSynced(true);
       mFriendList.setHasFixedSize(true);
        mUsersDatabase.keepSynced(true);

        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainView;
        //return inflater.inflate(R.layout.fragment_friends, container, false);
    }

        public void onStart ()
        {
            super.onStart();

                FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                        Friends.class,
                        R.layout.users_layout,
                        FriendsViewHolder.class,
                        mFriendDatabse
                ) {
                    @Override
                    protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends users, int position) {
                       // viewHolder.setName(users.getName());
                        //viewHolder.setUserStatus(users.getStatus());
                       // viewHolder.setUserImage(users.getThumb_image(),getContext());
                        viewHolder.setDate(users.getDate());
                        final String list_user_id=getRef(position).getKey();
                        try {
                            mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final String userName = dataSnapshot.child("name").getValue().toString();
                                    String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                                    if (dataSnapshot.hasChild("online")) {
                                        String useronline =  dataSnapshot.child("online").getValue().toString();
                                        viewHolder.setUserOnline(useronline);
                                    }
                                    viewHolder.setName(userName);
                                    viewHolder.setUserImage(thumb_image, getContext());
                                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            CharSequence options[]=new CharSequence[]{"Open Profile","Send Message"};
                                            AlertDialog.Builder builder =new AlertDialog.Builder(getContext());
                                            builder.setTitle("Select Options");
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int i) {

                                                    //click event for each item
                                                    if(i==0)
                                                    {
                                                        Intent profileintent=new Intent(getContext(),ProfileActivity.class);
                                                        profileintent.putExtra("from_user_id",list_user_id);
                                                        startActivity(profileintent);
                                                    }
                                                    if(i==1)
                                                    {
                                                        Intent sendmessageintent=new Intent(getContext(),ChatActivity.class);
                                                        sendmessageintent.putExtra("from_user_id",list_user_id);
                                                        sendmessageintent.putExtra("user_name",userName);
                                                        startActivity(sendmessageintent);
                                                    }
                                                }
                                            });
                                            builder.show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }catch (Exception e)
                        {
                            Log.e("Error",e.getMessage());
                        }

                    }
                };
                mFriendList.setAdapter(friendsRecyclerViewAdapter);


        }

        public static class FriendsViewHolder extends RecyclerView.ViewHolder {
            View mView;

            public FriendsViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
            }

            public void setDate(String date) {
                TextView userNameView = (TextView) mView.findViewById(R.id.user_single_statu);
                userNameView.setText(date);
            }
            public void setName(String name)
            {
               TextView userNameView=(TextView)mView.findViewById(R.id.user_single_name);
               userNameView.setText(name);
            }
            public void setUserImage(String thumb_image,Context ctx)
            {
                CircleImageView userImageView=(CircleImageView)mView.findViewById(R.id.user_single_imgeview);
                Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.index).fit().into(userImageView);

            }
            public void setUserOnline(String online_status)
            {
                ImageView  UserOnlineView=(ImageView)mView.findViewById(R.id.user_single_online_icon);
                if(online_status.equals("true"))
                {
                    UserOnlineView.setVisibility(View.VISIBLE);
                }
                else
                {
                    UserOnlineView.setVisibility(View.INVISIBLE);
                }
            }
        }

    }


