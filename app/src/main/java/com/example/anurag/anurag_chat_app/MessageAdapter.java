package com.example.anurag.anurag_chat_app;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by anurag on 6/4/18.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
   private List<Messages> mMessageList;
private    FirebaseAuth mAuth;
private DatabaseReference mRootref;
private Context context;


   public MessageAdapter(List<Messages> mMessageList)
   {
       this.mMessageList=mMessageList;

   }
    private MessageAdapter() {
        this.context = /* some magic here */context.getApplicationContext();
    }
   public MessageViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
   {
       View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);
       return  new MessageViewHolder(v);

   }
   public class MessageViewHolder extends RecyclerView.ViewHolder{
       public TextView messageText;
       public CircleImageView profileImage;
       public TextView message_time;
       public  ImageView mMessageImageView;


       public MessageViewHolder(View itemView) {
           super(itemView);
           messageText=(TextView)itemView.findViewById(R.id.message_single_text);
           mMessageImageView=(ImageView)itemView.findViewById(R.id.message_image_view );
           message_time=(TextView)itemView.findViewById(R.id.message_single_time);
           profileImage=(CircleImageView)itemView.findViewById(R.id.message_single_profile_image);

           mRootref= FirebaseDatabase.getInstance().getReference().child("users");
       }
   }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
       mAuth=FirebaseAuth.getInstance();
       if(mAuth.getCurrentUser()!=null) {
           String current_user_id = mAuth.getCurrentUser().getUid();

           Messages  c = mMessageList.get(position);

           String from_user = c.getFrom();
           String message_type=c.getType();
           if (from_user.equals(current_user_id)) {
               holder.messageText.setBackgroundResource(R.drawable.message_text_background);
               holder.messageText.setBackgroundColor(Color.WHITE);
               holder.messageText.setTextColor(Color.BLACK);


               mRootref.child(from_user).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                        String user_image=dataSnapshot.child("thumb_image").getValue().toString();
                        Picasso.with(context).load(user_image).placeholder(R.drawable.index).into(holder.profileImage);

                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });
               //holder.messageText.setGravity();
           } else {
               mRootref.child(from_user).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       String user_image=dataSnapshot.child("thumb_image").getValue().toString();
                       Picasso.with(context).load(user_image).placeholder(R.drawable.index).into(holder.profileImage);
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });
               holder.messageText.setBackgroundResource(R.drawable.message_text_background);
               holder.messageText.setTextColor(Color.WHITE);
               holder.messageText.setBackgroundColor(Color.BLUE);
           }
           if(message_type.equals("text")) {
               long time=c.getTime();

               GetTimeAgo getTimeAgo=new GetTimeAgo();

               String lastSeenTime=getTimeAgo.getTimeAgo(time,context);


              // String time_string=Long.toString(time);
               holder.messageText.setText(c.getMessage());
               holder.message_time.setText(lastSeenTime);
               holder.mMessageImageView.setVisibility(View.INVISIBLE);
           }else
           {
               long time=c.getTime();

               GetTimeAgo getTimeAgo=new GetTimeAgo();

               String lastSeenTime=getTimeAgo.getTimeAgo(time,context);



               holder.message_time.setText(lastSeenTime);
               holder.messageText.setVisibility(View.INVISIBLE);
               Picasso.with(holder.profileImage.getContext()).load(c.getMessage()).placeholder(R.drawable.index).into(holder.mMessageImageView);
           }
       }
    }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


  }


