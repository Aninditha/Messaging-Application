package com.example.messagingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements ChatFragment.OnChatFragmentInteractionListener,
ProfileFragment.OnFragmentInteractionListener, ContactsFragment.OnFragmentInteractionListener {

    ImageView chatScreen, profile, contacts;
    User present_user;
    private DatabaseReference mUsers;
    private String userId;
    private ArrayList<User> users_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle("Messages");

        chatScreen = (ImageView) findViewById(R.id.chats);
        profile = (ImageView) findViewById(R.id.profile);
        contacts = (ImageView) findViewById(R.id.contacts);

        getFragmentManager().beginTransaction()
                .add(R.id.container, new ChatFragment(), "chat")
                .commit();
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUsers = FirebaseDatabase.getInstance().getReference().child("users");

        mUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users_list= new ArrayList<>();
                for(DataSnapshot d: dataSnapshot.getChildren()) {
                    String id = d.getKey();
                    User user = d.getValue(User.class);
                    if(id.equals(userId)){
                        present_user= user;
                    }else{
                        users_list.add(user);
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        chatScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportActionBar().setTitle("Messages");
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new ChatFragment(), "chat")
                        .commit();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportActionBar().setTitle("My Profile");
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new ProfileFragment(), "profile")
                        .commit();
            }
        });

        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportActionBar().setTitle("My Contacts");
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new ContactsFragment(), "contacts")
                        .commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.message_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.logout:{
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
                break;
            }
            case R.id.new_message:{
                break;

            }
            default:{
                break;
            }
        }
        return true;
    }


    @Override
    public ArrayList<User> getUserList() {
       return users_list;
    }

    @Override
    public User getPresentUser() {
        return present_user;
    }
}
