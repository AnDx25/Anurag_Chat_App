package com.example.anurag.anurag_chat_app;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mToolbar;
    private ViewPager mViewpager;
    private SectionPageAdapter mSectionPageAdapter;
    TabLayout mtablayout;
    private DatabaseReference mUserRef;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mToolbar= (android.support.v7.widget.Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("CHAT APP");

        if (mAuth.getCurrentUser() != null) { mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()); }
        //mUserRef= FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        mViewpager=(ViewPager)findViewById(R.id.main_tabPager);
        mtablayout= (TabLayout) findViewById(R.id.main_tab);
        mSectionPageAdapter=new SectionPageAdapter(getSupportFragmentManager());
        mSectionPageAdapter.addFragments(new ChatFragment(),"Chats");
        mSectionPageAdapter.addFragments(new FriendsFragment(),"Friends");
        mSectionPageAdapter.addFragments(new RequestFragment(),"Requests");
        mViewpager.setAdapter(mSectionPageAdapter);
        mtablayout.setupWithViewPager(mViewpager);
        currentUser  = mAuth.getCurrentUser();



    }
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        if(currentUser==null)
        {
            Intent i=new Intent(MainActivity.this,Start_Activity.class);
            startActivity(i);
            finish();
        }else
        {
            mUserRef.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(currentUser!=null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
           // mUserRef.child("Last Seen").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.main_logout)
        {
          FirebaseAuth.getInstance().signOut();
            Intent i=new Intent(MainActivity.this,Start_Activity.class);
            startActivity(i);
            finish();
        }
        if(item.getItemId()==R.id.main_settings)
        {
            Intent i=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(i);
        }
        if (item.getItemId()==R.id.menu_users)
        {
            Intent i=new Intent(MainActivity.this,AllUsersActivity.class);
            startActivity(i);
        }
        return true;
    }
}
