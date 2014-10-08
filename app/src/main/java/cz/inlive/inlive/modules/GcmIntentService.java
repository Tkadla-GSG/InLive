package cz.inlive.inlive.modules;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import cz.inlive.inlive.InLiveApplication;
import cz.inlive.inlive.R;
import cz.inlive.inlive.activity.LandingPageActivity;
import cz.inlive.inlive.activity.LoginActivity;
import cz.inlive.inlive.database.DatabaseHandler;
import cz.inlive.inlive.database.objects.Info;
import cz.inlive.inlive.utils.Constants;

public class GcmIntentService extends IntentService {

    private String TAG = "GcmIntentService";
    public static final int NOTIFICATION_ID = 1;

    private DatabaseHandler mDatabaseHandler;

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;


    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                /*sendNotification("Send error: " + extras.toString());*/
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                /*sendNotification("Deleted messages on server: " +
                        extras.toString());*/
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // get JSON from notification

                String gcmMessage = extras.getString("message");

                Log.d(TAG, "message received : " + gcmMessage);

                // Looking for this pattern
                //{"result":{"message":"Test TEst","type":"info"}}
                if(gcmMessage != null && !gcmMessage.isEmpty()) {

                    try {
                        JSONObject object = new JSONObject(gcmMessage);

                        if(object.has("result") && !object.isNull("result")) {
                            //retype object to allow check for "result"
                            object = object.getJSONObject("result");
                        }

                        if(object.has("message") && !object.isNull("message")){

                            // Post notification of received message.
                            sendNotification(object.getString("message"));

                            if(object.has("type") && !object.isNull("type")){
                                String type = object.getString("type");

                                if(Constants.TYPE_INFO.equals(type)){
                                    // this is indeed valid message from server and i should persist it in DB
                                    Info info = new Info();
                                    info.parseJSON(object);
                                    info.setReceived(System.currentTimeMillis());

                                    mDatabaseHandler = ((InLiveApplication)getApplication()).getDatabaseHandler();

                                    mDatabaseHandler.saveInfo(info);
                                }

                            }

                            Log.i(TAG, "Received: " + extras.toString());

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_logo)
                        .setContentText("" + R.string.new_bet)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSound(alarmSound)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg));

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, LandingPageActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(LoginActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        Notification notification = mBuilder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.FLAG_SHOW_LIGHTS;
        notification.ledOnMS = 200;
        notification.ledOffMS = 200;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(NOTIFICATION_ID, notification);

    }
}