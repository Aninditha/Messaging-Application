package com.example.messagingapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private OnChatFragmentInteractionListener mListener;
    private TextView tv;

    private ListView listView;
    private ArrayList<SendersList> senders_list;
    private ChatAdapter sendersAdapter;

    private DatabaseReference mDatabase;


    private User present_user;

    private String userId;
    private LastMessage lastMessage= new LastMessage();


    public ChatFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChatFragmentInteractionListener) {
            mListener = (OnChatFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnExpenceFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        tv= (TextView) getView().findViewById(R.id.textViewNoChats);
        listView = (ListView) getView().findViewById(R.id.chatList);

        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("user_inbox").child(userId);


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                senders_list = new ArrayList<>();

                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    String sender = d.getKey();
                    lastMessage=d.child("last_message").getValue(LastMessage.class);


                    SendersList newSender = new SendersList();
                    newSender.setUid(sender.split(",")[0]);
                    newSender.setName(sender.split(",")[1]);
                    senders_list.add(newSender);
                }

                if (senders_list.size() == 0) {

                    tv.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.INVISIBLE);

                }else{

                    tv.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    sendersAdapter= new ChatAdapter(getView().getContext(),R.layout.row_message_item,senders_list,lastMessage);
                    listView.setAdapter(sendersAdapter);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent= new Intent(getView().getContext(),ChatActivity.class);
                intent.putExtra("SENDER_RECEIVER",senders_list.get(position));
                intent.putExtra("LAST_MESSAGE",lastMessage);
                intent.putExtra("SENDER_NAME",present_user.getfName()+" "+present_user.getlName());
                startActivity(intent);


            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnChatFragmentInteractionListener {

    }
}
