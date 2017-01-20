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

public class ChatAdapter extends ArrayAdapter<SendersList> {

    private Context mContext;
    private List<SendersList> mList;
    private LastMessage lstmsgs;

    private int mResource;
    private FirebaseStorage storage =  FirebaseStorage.getInstance();
    private StorageReference storageRef;

    public ChatAdapter(Context context, int resource, List<SendersList> objects,LastMessage lstmsgs ){
        super(context, resource, objects);
        mContext = context;
        mList = objects;
        mResource = resource;
        this.lstmsgs=lstmsgs;

        storageRef = storage.getReferenceFromUrl("gs://homeworks-fcb2f.appspot.com");
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        viewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new viewHolder();
            holder.status = (ImageView) convertView.findViewById(R.id.unreadImage);
            holder.sender_image = (ImageView) convertView.findViewById(R.id.chatImage);
            holder.sender_name = (TextView) convertView.findViewById(R.id.chatName);
            holder.sender_time = (TextView) convertView.findViewById(R.id.chatTime);
            holder.sender_msg = (TextView) convertView.findViewById(R.id.chatMessage);

            convertView.setTag(holder);
        }

        SendersList sender = mList.get(position);
        String path = "images/profilepic"+sender.getUid()+".jpeg";
        StorageReference imageRef = storageRef.child(path);
        holder = (viewHolder) convertView.getTag();
        TextView sn = holder.sender_name;
        ImageView iv = holder.sender_image;
        ImageView iv2 = holder.status;
        TextView st = holder.sender_time;
        TextView sm = holder.sender_msg;

        if (lstmsgs.isRead()){
            iv2.setVisibility(View.GONE);
        }else{
            iv2.setVisibility(View.VISIBLE);
        }

        sn.setText(sender.getName());
        sm.setText(lstmsgs.getMessage());
        st.setText(new SimpleDateFormat("mm/DD/yyyy").format(lstmsgs.getTime()));
        Glide.with(mContext).using(new FirebaseImageLoader())
                .load(imageRef).into(iv);
        return convertView;
    }

    private static class viewHolder {
        ImageView sender_image,status;
        TextView sender_name, sender_time, sender_msg;
    }
}