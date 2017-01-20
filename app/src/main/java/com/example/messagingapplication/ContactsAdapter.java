package com.example.messagingapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

public class ContactsAdapter extends ArrayAdapter<User> {

    private Context mContext;
    private List<User> mList;
    private int mResource;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;

    public ContactsAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        mContext = context;
        mList = objects;
        mResource = resource;

        storageRef = storage.getReferenceFromUrl("gs://homeworks-fcb2f.appspot.com");
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ContactsAdapter.viewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ContactsAdapter.viewHolder();
            holder.contact_image = (ImageView) convertView.findViewById(R.id.contactImage);
            holder.contact_name = (TextView) convertView.findViewById(R.id.contactName);

            convertView.setTag(holder);
        }

        User user= mList.get(position);
        holder = (ContactsAdapter.viewHolder) convertView.getTag();
        TextView cn = holder.contact_name;
        ImageView ci = holder.contact_image;


        cn.setText(user.getfName()+" "+user.getlName());
        Picasso.with(mContext).load(user.getUrl()).into(ci);
        return convertView;
    }

    private static class viewHolder {
        ImageView contact_image;
        TextView contact_name;
    }
}
