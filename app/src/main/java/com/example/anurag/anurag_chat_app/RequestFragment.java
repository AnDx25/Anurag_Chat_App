package com.example.anurag.anurag_chat_app;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
public class RequestFragment extends Fragment {
    private RecyclerView mRequestRecyclerView;

private RecyclerView mFriendrequest;
private FirebaseAuth mAuth;
private String mCurrent_user_id;
TextView mChild_Uid;
private View mMainView;
private Button mRequestButton;
private DatabaseReference mFriendRequestDatabase;
private DatabaseReference mUsersDatabase;
private DatabaseReference mFrienRequestDarabase1;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView= inflater.inflate(R.layout.fragment_request, container, false);
       mFriendrequest=(RecyclerView)mMainView.findViewById(R.id.request_recyclerview) ;
       mAuth=FirebaseAuth.getInstance();
       mCurrent_user_id=mAuth.getCurrentUser().getUid();

      mFriendRequestDatabase=FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
      mFriendrequest.setHasFixedSize(true);
      mFriendrequest.setLayoutManager(new LinearLayoutManager(getContext()));
     mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("users");

       /* mFriendRequestDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String uid=dataSnapshot.getKey();
                String request_type=dataSnapshot.child("request_type").getValue().toString();
                if(request_type.equals("received")) {
                    Toast.makeText(getContext(), uid, Toast.LENGTH_SHORT).show();
                    mFrienRequestDarabase1 = mFriendRequestDatabase.child(uid).child("request type");
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                String uid = dataSnapshot.getKey();


                   // Toast.makeText(getContext(),uid , Toast.LENGTH_SHORT).show();
                   // mChild_Uid.setText(uid);
                    Log.v("child_uid",uid);
                }
               // Log.d("child id=", uid);
                // String name = childSnapshot.getValue(String.class);
                //Toast.makeText(getContext(), "hey wassup", Toast.LENGTH_SHORT).show();


                //String uid=dataSnapshot.getChildren().iterator().toString();





            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        return mMainView;
    }

   @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Request,RequestViewHolder>  requestRecyclerView=new FirebaseRecyclerAdapter<Request, RequestViewHolder>(
                Request.class,
                R.layout.users_layout,
                RequestViewHolder.class,
                mFriendRequestDatabase
        ) {
            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, Request model, int position) {
               // Toast.makeText(getContext(), model.getRequest_type(), Toast.LENGTH_SHORT).show();

viewHolder.setRequest_Type(model.getRequest_type());
                final String list_user_id=getRef(position).getKey();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent=new Intent(getContext(),ProfileActivity.class);
                        profileIntent.putExtra("from_user_id",list_user_id);
                        startActivity(profileIntent);
                    }
                });
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                        viewHolder.setName(userName);
                        viewHolder.setUserImage(thumb_image, getContext());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mFriendrequest.setAdapter(requestRecyclerView);
    }
    public static  class RequestViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public RequestViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setRequest_Type(String request_type)
        {
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_statu);
            userNameView.setText(request_type);
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
    }
}
