package com.example.messagingapplication;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ocpsoft.pretty.time.PrettyTime;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private Context mContext;
    private List<Message> mList;
    private int mResource;

    public MessageAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
        mContext = context;
        mList = objects;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MessageAdapter.viewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new MessageAdapter.viewHolder();
            holder.image_sender = (ImageView) convertView.findViewById(R.id.senderImage);
            holder.message_sender = (TextView) convertView.findViewById(R.id.senderMessage);
            holder.image_me = (ImageView) convertView.findViewById(R.id.myImage);
            holder.message_me = (TextView) convertView.findViewById(R.id.myMessage);
            holder.message_time_sender= (TextView) convertView.findViewById(R.id.senderTime);
            holder.message_time_me= (TextView) convertView.findViewById(R.id.myTime);

            convertView.setTag(holder);
        }

        Message msg = mList.get(position);
        holder = (MessageAdapter.viewHolder) convertView.getTag();
        TextView message_sender = holder.message_sender;
        ImageView image_sender = holder.image_sender;
        TextView message_me = holder.message_me;
        ImageView image_me = holder.image_me;
        TextView message_time_sender = holder.message_time_sender;
        TextView message_time_me = holder.message_time_me;

        PrettyTime p = new PrettyTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (msg.getuId().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            //message from me

            convertView.findViewById(R.id.senderContainer).setVisibility(View.GONE);
            convertView.findViewById(R.id.myContainer).setVisibility(View.VISIBLE);

            String date = msg.getTime();

            try {
                message_time_me.setText(p.format(format.parse(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(msg.getType().equals("TEXT")){
                image_me.setVisibility(View.GONE);
                message_me.setVisibility(View.VISIBLE);
                message_me.setText(msg.getText());
            }else if (msg.getType().equals("IMAGE")){
                image_me.setVisibility(View.VISIBLE);
                message_me.setVisibility(View.GONE);
                Picasso.with(mContext).load(msg.getMessage_url()).into(image_me);
            }
        }else{
            //message from sender

            convertView.findViewById(R.id.senderContainer).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.myContainer).setVisibility(View.GONE);

            String date = msg.getTime();

            try {
                message_time_sender.setText(p.format(format.parse(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(msg.getType().equals("TEXT")){
                image_sender.setVisibility(View.GONE);
                message_sender.setVisibility(View.VISIBLE);
                message_sender.setText(msg.getText());
                if (msg.isRead()){
                    message_sender.setBackgroundColor(Color.WHITE);

                }else{
                    message_sender.setBackgroundColor(Color.YELLOW);
                }
            }else if (msg.getType().equals("IMAGE")){
                image_me.setVisibility(View.VISIBLE);
                message_me.setVisibility(View.GONE);

                Picasso.with(mContext).load(msg.getMessage_url()).into(image_me);
            }
        }
        return convertView;
    }

    private static class viewHolder {
        ImageView image_sender, image_me;
        TextView message_sender,message_me,message_time_sender, message_time_me;
    }
}