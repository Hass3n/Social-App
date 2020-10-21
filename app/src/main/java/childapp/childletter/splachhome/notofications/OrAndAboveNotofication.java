package childapp.childletter.splachhome.notofications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;

public class OrAndAboveNotofication extends ContextWrapper {

    private  static final  String ID="some_id";
    private  static final  String NAME="FirebaseApp";
    private NotificationManager notificationManager;

    public OrAndAboveNotofication(Context base) {

        super(base);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            CreateChannel();

        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void CreateChannel() {

        NotificationChannel  notificationChannel=new NotificationChannel(ID,NAME,NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
       getManger().createNotificationChannel(notificationChannel);


    }

    public NotificationManager getManger()
    {
        if (notificationManager==null)
        {
            notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        }

          return notificationManager;
    }


    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getNotofications(String title, String body, PendingIntent pendingIntent, Uri sounduUri, String icon)
    {


        return new Notification.Builder(getApplicationContext(),ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(sounduUri)
                .setAutoCancel(true)
                .setSmallIcon(Integer.parseInt(icon));


    }



}
