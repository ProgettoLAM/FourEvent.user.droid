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

    private String mMessage;
    private String mTitle;

    //This method will be called on every new message received
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Getting the message from the bundle 
        mMessage = data.getString(MESSAGE);
        mTitle = data.getString(TITLE);
        //Displaying a notification with the message
        sendNotification();
    }
    
    //This method is generating a notification and displaying the notification 
    private void sendNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mMessage))
                .setContentTitle(mTitle)
                .setSmallIcon(R.drawable.ic_inbox_notification)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
 
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }
}