package co.geeksters.hq.services.notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.GlobalMenuActivity;
import co.geeksters.hq.activities.GlobalMenuActivity_;
import co.geeksters.hq.activities.RegisterActivity_;
import co.geeksters.hq.fragments.MarketPlaceFragment_;
import co.geeksters.hq.global.GlobalVariables;

import static android.content.Intent.*;

/**
 * Created by geeksters6 on 02/04/15.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public String notificationMessage;
    public int notificationPostId;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        notificationMessage = intent.getStringExtra("message");
        String jsonData = "";
        if(notificationMessage.equals("You have a new comment on your post")){


      /*      try {
                JSONObject jsonObj = new JSONObject(intent.getStringExtra("data"));
                notificationPostId = jsonObj.getString("post_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            Bundle extras = intent.getExtras();
            jsonData = extras.getString("custom");
            try {
                JSONObject jsonObj = new JSONObject(jsonData).getJSONObject("data");
                notificationPostId = jsonObj.getInt("post_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        sendNotification(notificationMessage);
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = null;
        if(notificationMessage.equals("You have a new comment on your post")){
            resultIntent = new Intent(this, GlobalMenuActivity_.class);
            GlobalVariables.notificationPostId = notificationPostId;
            GlobalVariables.notifiyedByPost = true;
        }
        else{

            resultIntent = new Intent(this, GlobalMenuActivity_.class);
            GlobalVariables.notifiyedByTodo = true;

        }

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        /*PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, FLAG_ACTIVITY_NEW_TASK);*/

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Thousand Network")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(notificationMessage))
                        .setContentText(notificationMessage);
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        //Toast.makeText(BaseApplication.getAppContext(),"sendNotification notification success",Toast.LENGTH_SHORT).show();
    }
}