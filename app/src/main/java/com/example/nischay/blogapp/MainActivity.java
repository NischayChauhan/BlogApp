package com.example.nischay.blogapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;

    private ViewPager mPager;
    private PagerAdapter madapter;
    private int count = 0;
    ArrayList<Blog> blog_post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        blog_post = new ArrayList<>();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mPager = (ViewPager) findViewById(R.id.pager);
        madapter  = new MainActivity_Adapter(getSupportFragmentManager());
        mPager.setAdapter(madapter);
//        mPager.setPageTransformer(true,new viewpagerActivity_transition());

        blog_post.add(new Blog());
        madapter.notifyDataSetChanged();

        final int[] count = {0};
        Log.e("MainActivity","Started calling firebase listener");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot postSnapshot, String s) {
                Log.e("Main Activity", "New Child Added:: " + (count[0]++))
                ;
                if(postSnapshot.getChildrenCount()!=0){
                    Blog new_entry = new Blog();
                    new_entry.setUser_id(postSnapshot.child("user_id").getValue().toString());
                    new_entry.setStory(postSnapshot.child("story").getValue().toString());
                    new_entry.setHeading(postSnapshot.child("heading").getValue().toString());
                    new_entry.setImage_url(postSnapshot.child("image_url").getValue().toString());
                    blog_post.add(new_entry);
                    notify_blog_post_adapter();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot postSnapshot) {
                Log.e("Main Activity", "Child Removed:: " + postSnapshot.getChildrenCount() + " " + postSnapshot.getKey());
                if(postSnapshot.getChildrenCount()!=0){
                    String uid = postSnapshot.child("user_id").getValue().toString();
                    for (int i = 0;i<blog_post.size();i++){
                        if(blog_post.get(i).getUser_id()==uid){
                            blog_post.remove(i);
                            madapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        Log.e("MainActivity","Out calling firebase listener");


        // Yahan par saare blog post aa rhe hongey sab user ke..
//        Log.e("Main Activity","So far so good");
        FloatingActionButton m_write_blog = (FloatingActionButton) findViewById(R.id.write_blog);
        m_write_blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),WriteBlogActivity.class));
            }
        });
//        Log.e("Main Activity","Back on it.");

    }

    private void notify_blog_post_adapter() {
        madapter.notifyDataSetChanged();
    }

    private class MainActivity_Adapter extends FragmentStatePagerAdapter {
        public MainActivity_Adapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle data = new Bundle();
            data.putString("story",blog_post.get(position).getStory());
            data.putString("heading",blog_post.get(position).getHeading());
            data.putString("image",blog_post.get(position).getImage_url());

//            Log.e("ITEM  ", String.valueOf(mPager.getCurrentItem()));
            Fragment vw = new MainActivity_Fragment();
            vw.setArguments(data);
            Log.e("Main Activity element"," --> "+blog_post.size());
            return vw;
        }

        @Override
        public int getCount() {
            return blog_post.size();
        }

//        @Override
//        public int getItemPosition(Object object){
//            return PagerAdapter.POSITION_NONE;
//        }
    }








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
//        menu.add();

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
