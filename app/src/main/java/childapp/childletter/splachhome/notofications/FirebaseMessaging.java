package childapp.childletter.splachhome.notofications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import childapp.childletter.splachhome.ChatActivity;

public class FirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // get current id from shared prference
        SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
        String savedCurrentUser=sp.getString("Current_USERID","None");
        String sent=remoteMessage.getData().get("sent"); //hiuid
        String user=remoteMessage.getData().get("user");// mid
        FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();
        if (fuser!=null && user.equals(fuser.getUid()))
        {
            if (!savedCurrentUser.equals(sent))
            {

                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
                {
                    sendOAndAboveNotification(remoteMessage);

                }
                else {
                    sendNormalNotification(remoteMessage);

                }

            }


        }











    }

    private void sendNormalNotification(RemoteMessage remoteMessage) {

        String user=remoteMessage.getData().get("sent");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        Log.e("body",body+"");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("hasUid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pintent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setContentIntent(pintent);

        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if (i>0)
        {
            j=i;
        }
        notificationManager.notify(j, builder.build());




    }

    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {

        String user=remoteMessage.getData().get("sent");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("hasUid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pintent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
         OrAndAboveNotofication notofication1=new OrAndAboveNotofication(this);
        Notification.Builder builder=notofication1.getNotofications(title,body,pintent,defSoundUri,icon);

        int j=0;
        if (i>0)
        {
            j=i;
        }
      notofication1.getManger().notify(j, builder.build());





    }



}
