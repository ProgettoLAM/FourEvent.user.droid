package lam.project.foureventuserdroid.utils.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import lam.project.foureventuserdroid.MainActivity;
import lam.project.foureventuserdroid.R;

public class GCMPushReceiverService extends GcmListenerService {

    private static final String MESSAGE = "message";
    private static final String TITLE = "title";
    private static final String AUTHOR = "author";

    private String mMessage;
    private String mTitle;
    private String mAuthor;

    @Override
    public void onMessageReceived(String from, Bundle data) {

        mMessage = data.getString(MESSAGE);
        mTitle = data.getString(TITLE);
        mAuthor = data.getString(AUTHOR);


        sendNotification();
    }

    private void sendNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mMessage))
                .setContentTitle(mTitle)
                .setContentText(mMessage)
                .setSmallIcon(R.drawable.ic_inbox_notification)
                .setAutoCancel(true)
                .setSubText(mAuthor)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
 
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build());
    }
}