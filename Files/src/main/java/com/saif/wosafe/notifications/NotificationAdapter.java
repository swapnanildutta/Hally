package com.saif.wosafe.notifications;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.saif.wosafe.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter implements ListAdapter {
    Context context;
    List<NotificationData> notificationData;

    public NotificationAdapter(Context context, List<NotificationData> notificationData) {
        this.context = context;
        this.notificationData = notificationData;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return notificationData.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.notification_list, null);

        }
        final CircleImageView userPic = view.findViewById(R.id.Notification_image);
        final CircleImageView NotificationType = view.findViewById(R.id.Notification_type);
        final TextView NotificationText = view.findViewById(R.id.Notification_text);
        final TextView NotificationTime = view.findViewById(R.id.Notification_time);
        Glide.with(context).load(notificationData.get(i).getUserProfile()).into(userPic);
        Glide.with(context).load(notificationData.get(i).getNotificationType()).into(NotificationType);
        String notificationText=notificationData.get(i).getNotificationText();
        if(notificationText.length() > 75){
            notificationText = notificationText.substring(0,75) + " ...";
        }
        NotificationText.setText(notificationText);
        NotificationTime.setText(notificationData.get(i).getNotificationTime());
        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return notificationData.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}
