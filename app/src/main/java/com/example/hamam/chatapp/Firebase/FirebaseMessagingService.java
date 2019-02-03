package com.example.hamam.chatapp.Firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.hamam.chatapp.R;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);









        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_body = remoteMessage.getNotification().getBody();
        String click_action =remoteMessage.getNotification().getClickAction();
        String from_sender_id = remoteMessage.getData().get("from_sender_id");



        NotificationCompat.Builder mBuilder  = new NotificationCompat.Builder(this, "M_CH_ID")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle(notification_title)
                .setContentText(notification_body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        Intent resultIntent = new Intent(click_action);
        resultIntent.putExtra("visit_user_id",from_sender_id);




        PendingIntent resultpendIntent =
                PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultpendIntent);









        int notificationId =  (int) System.currentTimeMillis();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, mBuilder.build());



    }
}





