package com.example.anurag.anurag_chat_app;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title=remoteMessage.getNotification().getTitle();
        String body=remoteMessage.getNotification().getBody();
        String click_action=remoteMessage.getNotification().getClickAction();
        String activity=remoteMessage.getData().get("activity");
        String from_user_id=remoteMessage.getData().get("from_user_id");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        int notificationId=(int)System.currentTimeMillis();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, mBuilder.build());

        Intent intent = new Intent(this,activity.getClass());
        intent.putExtra("from_user_id",from_user_id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder.setContentIntent(pendingIntent);

    }





// notificationId is a unique int for each notification that you must define


}
