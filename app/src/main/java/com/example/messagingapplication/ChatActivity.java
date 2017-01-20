package com.example.messagingapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anindithamadishetty on 11/22/16.
 */

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {


    String suserId,sName;
    String ruserID,rName;
    String msg="",key;;

    User receiver;
    SendersList send_receiver;

    Message message;
    LastMessage lastMessage;

    ArrayList<Message> messageList;
    MessageAdapter messageAdapter;

    ImageView gallery,send;
    EditText chatMessage;

    ListView listView;

    DatabaseReference mDatabase;
    FirebaseStorage storage =  FirebaseStorage.getInstance();
    StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        suserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        listView = (ListView) findViewById(R.id.messageList);
        chatMessage= (EditText) findViewById(R.id.sendMessage);
        gallery= (ImageView) findViewById(R.id.galleryImage);
        send = (ImageView) findViewById(R.id.sendImage);

        storageRef = storage.getReferenceFromUrl("gs://homeworks-fcb2f.appspot.com");

        Intent intent = getIntent();

        if(intent == null) {
            return;
        }

        Bundle bundle = intent.getExtras();

        if(intent.getExtras().containsKey("RECEIVER")){
            receiver = (User) bundle.getSerializable("RECEIVER");
            lastMessage= new LastMessage();
            sName = bundle.getString("SENDER_NAME");
            ruserID = receiver.getuId();
            rName = receiver.getfName()+" "+receiver.getlName();

        }else if (intent.getExtras().containsKey("SENDER_RECEIVER")){

            send_receiver = (SendersList) bundle.getSerializable("SENDER_RECEIVER");
            sName=bundle.getString("SENDER_NAME");
            lastMessage= (LastMessage) bundle.getSerializable("LAST_MESSAGE");
            ruserID = send_receiver.getUid();
            rName = send_receiver.getName();

        }




        mDatabase = FirebaseDatabase.getInstance().getReference().child("user_inbox");


        mDatabase.child(suserId).child(ruserID+","+rName).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                messageList= new ArrayList<Message>();
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    message= d.getValue(Message.class);
                    messageList.add(message);

                    if(!message.isRead()){
                        message.setRead(true);
                        mDatabase.child(suserId).child(ruserID+","+rName).child("messages").child(d.getKey()).setValue(message);
                    }
                }
                if(messageList.size()!=0){
                    msg = messageList.get(messageList.size()-1).getText();

                }


                sortList(messageList);
                messageAdapter= new MessageAdapter(ChatActivity.this,R.layout.row_message_item,messageList);
                listView.setAdapter(messageAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(this);
        gallery.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){

            case R.id.sendImage:{
                key =  mDatabase.child(suserId).child(ruserID+","+rName).child("messages").push().getKey();

                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                Message newmsg = new Message();
                Message newmsg2 = new Message();

                LastMessage lstmsg = new LastMessage();
                LastMessage lstmsg1 = new LastMessage();


                newmsg.setMessage_url("null");
                newmsg.setType("TEXT");
                newmsg.setText(chatMessage.getText().toString());
                newmsg.setTime(date);
                newmsg.setRead(true);
                newmsg.setuId(suserId+key);

                Map<String, Object> msgs = newmsg.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(suserId+key, msgs);
                mDatabase.child(suserId).child(ruserID+","+rName).child("messages").updateChildren(childUpdates);

                newmsg2.setMessage_url("null");
                newmsg2.setType("TEXT");
                newmsg2.setText(chatMessage.getText().toString());
                newmsg2.setTime(date);
                newmsg2.setRead(false);
                newmsg2.setuId(suserId+key);

                Map<String, Object> msgs1 = newmsg2.toMap();
                Map<String, Object> childUpdates1 = new HashMap<>();
                childUpdates1.put(suserId+key, msgs1);
                mDatabase.child(ruserID).child(suserId+","+sName).child("messages").updateChildren(childUpdates1);




                lstmsg.setRead(true);
                lstmsg.setMessage(chatMessage.getText().toString());

                lastMessage= new LastMessage();
                lastMessage.setMessage(chatMessage.getText().toString());
                lastMessage.setRead(true);

                mDatabase.child(suserId).child(ruserID+","+rName).child("last_message").setValue(lstmsg1);

                lstmsg1.setRead(false);
                lstmsg1.setMessage(chatMessage.getText().toString());
                mDatabase.child(ruserID).child(suserId+","+sName).child("last_message").setValue(lstmsg1);






                break;
            }
            case R.id.galleryImage:{
                Intent pictureIntent = new Intent(Intent.ACTION_PICK);
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();
                Uri data = Uri.parse(pictureDirectoryPath);
                pictureIntent.setDataAndType(data, "image*//*");
                startActivityForResult(pictureIntent,20);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 20){
                Uri imageURI = data.getData();

                key =  mDatabase.child(suserId).child(ruserID+","+rName).child("messages").push().getKey();
                String path = "images/" + suserId+key + ".jpeg";
                StorageReference imageRef = storageRef.child(path);

                imageRef.putFile(imageURI)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri url = taskSnapshot.getDownloadUrl();

                                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                Message newmsg = new Message();
                                LastMessage lstmsg = new LastMessage();
                                LastMessage lstmsg1 = new LastMessage();

                                newmsg.setMessage_url(url.toString());
                                newmsg.setType("IMAGE");
                                newmsg.setText(chatMessage.getText().toString());
                                newmsg.setTime(date);
                                newmsg.setuId(suserId+key);

                                Map<String, Object> msgs = newmsg.toMap();
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put(suserId+key, msgs);

                                lstmsg.setRead(true);
                                lstmsg.setMessage("");

                                lastMessage= new LastMessage();
                                lastMessage.setMessage("");
                                lastMessage.setRead(true);

                                mDatabase.child(suserId).child(ruserID+","+rName).child("last_message").setValue(lstmsg1);

                                lstmsg1.setRead(false);
                                lstmsg1.setMessage("");
                                mDatabase.child(ruserID).child(suserId+","+sName).child("last_message").setValue(lstmsg1);

                                mDatabase.child(suserId).child(ruserID+","+rName).child("messages").child(key).updateChildren(childUpdates);
                                mDatabase.child(ruserID).child(suserId+","+sName).child("messages").child(key).updateChildren(childUpdates);
                            }
                        });
            }

        }
    }

    @Override
    public void onBackPressed() {

        if(!lastMessage.isRead()){
            lastMessage.setRead(true);
            lastMessage.setMessage(msg);
        }
        mDatabase.child(suserId).child(ruserID+","+rName).child("last_message").setValue(lastMessage);
        finish();
    }


    private void sortList(ArrayList<Message> storiesList) {
        Collections.sort(storiesList, new Comparator<Message>() {
            @Override
            public int compare(Message lhs, Message rhs) {
                try {
                    Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse( lhs.getTime());
                    Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse( rhs.getTime());
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }


        });

    }


}



