package com.example.anurag.anurag_chat_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Chat_Blog extends AppCompatActivity {
    private android.support.v7.widget.Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__blog);
        mToolbar= (android.support.v7.widget.Toolbar) findViewById(R.id.blog_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Blog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.add_post)
        {
            Intent i=new Intent(Chat_Blog.this,PostActivity.class);
            startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }
}
