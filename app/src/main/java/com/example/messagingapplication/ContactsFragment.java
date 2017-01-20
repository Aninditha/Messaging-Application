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

import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private TextView tv;
    private ListView listView;
    private ArrayList<User> users_list;
    private ContactsAdapter adapter;

    private String userId;


    private User present_user;


    public ContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ContactsFragment.OnFragmentInteractionListener) {
            mListener = (ContactsFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnExpenceFragmentInteractionListener");
        }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        users_list=mListener.getUserList();
        present_user= mListener.getPresentUser();
        listView= (ListView) getView().findViewById(R.id.contactsList);
        adapter= new ContactsAdapter(getView().getContext(),R.layout.row_contact_item,users_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent= new Intent(getView().getContext(),ViewProfileActivity.class);
                intent.putExtra("USER",users_list.get(position));
                intent.putExtra("PRESENT_USER",present_user);
                startActivity(intent);

            }
        });



    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        ArrayList<User> getUserList();
        User getPresentUser();
    }
}