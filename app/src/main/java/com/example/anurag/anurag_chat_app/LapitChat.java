package com.example.anurag.anurag_chat_app;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by anurag on 24/3/18.
 */

public class LapitChat extends Application {
    //@Override
    private DatabaseReference mUserDtabaseRefrence;
    private FirebaseAuth mAuth;
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        Picasso.setSingletonInstance(built);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mUserDtabaseRefrence = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
            mUserDtabaseRefrence.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    mUserDtabaseRefrence.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                    //  mUserDtabaseRefrence.child("Last Seen").setValue(ServerValue.TIMESTAMP);
                    //mUserDtabaseRefrence.child("online").setValue(true);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }
}
