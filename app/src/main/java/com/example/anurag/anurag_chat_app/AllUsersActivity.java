package com.example.anurag.anurag_chat_app;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;


import com.coremedia.iso.boxes.ItemLocationBox;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity{
Toolbar mToolbar;
RecyclerView mUsersList;
FirebaseAuth mAuth;
private DatabaseReference mUserDatabase;
Users users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        mToolbar=(Toolbar)findViewById(R.id.users_appbar);
        mUsersList=(RecyclerView)findViewById(R.id.users_list);
        setSupportActionBar(mToolbar);
        users=new Users();
        getSupportActionBar().setTitle("ALL USERS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //to display the users make a layout resource file as users_single_layout
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));
        mAuth=FirebaseAuth.getInstance();
        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("users");

//now cretae a model class as users
    }

    @Override
    protected void onStart() {
        //now create firebase recycler to retrive the data
        super.onStart();
        //in this we have to pass a model class and a view holder so now create a view holder
        FirebaseRecyclerAdapter<Users,UsersViewHolder> mfirebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, UsersViewHolder>(

                Users.class,
                R.layout.users_layout,
                UsersViewHolder.class,
                mUserDatabase

        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users users, int position) {
    //this function will be responsible to set the values to our recycler view
                viewHolder.setName(users.getName());
                viewHolder.setUserStatus(users.getStatus());
                viewHolder.setUserImage(users.getThumb_image(),getApplicationContext());
                //to send to the clicked users profile we need to first get the position of that user and through get the key
                final String user_id=getRef(position).getKey();
                //here mView provides the view for  a particular user
                //through this we are sending the activity to the selected user profile
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    Intent profileIntent=new Intent(AllUsersActivity.this,ProfileActivity.class);
                    profileIntent.putExtra("from_user_id",user_id);
                    startActivity(profileIntent);
                    }
                });
            }
        };

        mUsersList.setAdapter(mfirebaseRecyclerAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!searchView.isIconified())
                {
                    searchView.setIconified(true);

                }
                return  false;

            }


            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.app_bar_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder {
//now create a view for the firebase adapter
        View mView;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setName(String name)
        {
            TextView userNameView=(TextView)mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
        public void setUserStatus(String status)
        {
           TextView userStatusview=(TextView)mView.findViewById(R.id.user_single_statu);
           userStatusview.setText(status);
        }
        public void setUserImage(String thumb_image,Context ctx)
        {
            CircleImageView  userImageView=(CircleImageView)mView.findViewById(R.id.user_single_imgeview);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.index).fit().into(userImageView);

        }
    }
}
