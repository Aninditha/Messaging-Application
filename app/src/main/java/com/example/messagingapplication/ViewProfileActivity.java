package com.example.messagingapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ViewProfileActivity extends AppCompatActivity {

    TextView mFname, mEmail,mGender;
    ImageView profilePic;
    Button close,sendmessage;

    User user,present_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);


        mFname = (TextView) findViewById(R.id.otherProfileName);
        mEmail = (TextView) findViewById(R.id.otherProfileEmail);
        mGender = (TextView) findViewById(R.id.otherProfileGender);
        profilePic = (ImageView) findViewById(R.id.otherProfileImage);



        close = (Button) findViewById(R.id.myProfileClose);
        sendmessage= (Button) findViewById(R.id.buttonsendmessage);


        Intent intent = getIntent();

        if(intent == null) {
            return;
        }

        Bundle bundle = intent.getExtras();

        if(intent.getExtras().containsKey("USER")){
            user = (User) bundle.getSerializable("USER");
            present_user= (User) bundle.getSerializable("PRESENT_USER");
        }


        mFname.setText(user.getfName()+" "+user.getlName());
        mEmail.setText(user.getEmail());
        mGender.setText(user.getGender());


        Picasso.with(ViewProfileActivity.this).load(user.getUrl()).into(profilePic);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProfileActivity.this,ChatActivity.class);
                intent.putExtra("RECEIVER",user);
                intent.putExtra("SENDER_NAME",present_user.getfName()+" "+present_user.getlName());
                startActivity(intent);
            }
        });





    }
}
