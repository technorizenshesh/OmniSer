package com.technorizen.omniser.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.technorizen.omniser.R;
import com.technorizen.omniser.activities.SplashActivity;
import com.technorizen.omniser.fooddelivery.activities.FoodHomeActivity;
import com.technorizen.omniser.ondemand.activities.ViewRequestDetailsActivity;
import com.technorizen.omniser.taxi.activities.TrackTaxiAct;
import com.technorizen.omniser.utils.AppConstant;
import com.technorizen.omniser.utils.SharedPref;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    Intent intent;
    SharedPref sharedPref;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // log the getting message from firebase
        // Log.d(TAG, "From: " + remoteMessage.getFrom());

        //  if remote message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            // Log.d(TAG, "Message data payload : " + remoteMessage.getData());
            Map<String,String> data = remoteMessage.getData();
            String jobType = data.get("type");

            /* Check the message contains data If needs to be processed by long running job
               so check if data needs to be processed by long running job */

            // Handle message within 10 seconds
            handleNow(data);

             /* if (jobType.equalsIgnoreCase(JobType.LONG.name())) {
                 // For long-running tasks (10 seconds or more) use WorkManager.
                 scheduleLongRunningJob();
            } else {} */

        }

        // if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
            // Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // make a own server request here using your http client
    }

    private void handleNow(Map<String, String> data) {
        sendNotification(data.get("title"), data.get("message"));
          /* if (data.containsKey("title") && data.containsKey("message")) {
            sendNotification(data.get("title"), data.get("message"));
         } */
    }

    private void sendNotification(String title, String messageBody) {

        sharedPref = SharedPref.getInstance(this);

        if(sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
            if(messageBody != null) {
                Log.e("hjasgfasfdsf","title = " + title);
                Log.e("hjasgfasfdsf","messageBody = " + messageBody);

                JSONObject jsonObject = null;
                String msg = "";
                String status = "";
                String requestId = "";
                String notifyType = "";
                String key = "";

                try {
                    jsonObject = new JSONObject(messageBody);
             /* jsonObject = new JSONObject(messageBody);
             msg = "Your order is " + jsonObject.getString("message"); */
                    try {
                        msg = jsonObject.getString("key");
                        key = jsonObject.getString("key");
                    } catch (Exception e) {}

                    try {
                        notifyType = jsonObject.getString("noti_type");
                    } catch (Exception e) {}

                    try {
                        status = jsonObject.getString("status");
                    } catch (Exception e) {}

                    try {
                        requestId = jsonObject.getString("request_id");
                    } catch (Exception e) {}

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(key.equalsIgnoreCase("Taxi")) {
                    if(status.equalsIgnoreCase("Accept")) {
                        title = "New Booking Request";
                        try {
                            requestId = String.valueOf(jsonObject.get("request_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent1 = new Intent("driver_accept_request");
                        Log.e("fsdfsfsdfdsfdsf", "below intent = " + jsonObject.toString());
                        intent1.putExtra("object", jsonObject.toString());
                        sendBroadcast(intent1);
                        calltaxiStatusClicked(title,"Driver is on the way",requestId);
                    } else if(status.equalsIgnoreCase("Arrived")) {
                        title = "New Booking Request";
                        try {
                            requestId = String.valueOf(jsonObject.get("request_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", jsonObject.toString());
                        intent1.putExtra("object", jsonObject.toString());
                        sendBroadcast(intent1);
                        calltaxiStatusClicked(title,"Driver Arrived",requestId);
                    } else if(status.equalsIgnoreCase("Start")) {
                        title = "New Booking Request";
                        try {
                            requestId = String.valueOf(jsonObject.get("request_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", jsonObject.toString());
                        intent1.putExtra("object", jsonObject.toString());
                        sendBroadcast(intent1);
                        calltaxiStatusClicked(title,"Your trip has begin",requestId);
                    } else if(status.equalsIgnoreCase("Finish")) {
                        title = "New Booking Request";
                        try {
                            requestId = String.valueOf(jsonObject.get("request_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", jsonObject.toString());
                        intent1.putExtra("object", jsonObject.toString());
                        sendBroadcast(intent1);
                        calltaxiStatusClicked(title,"Your trip is finished",requestId);
                    } else if(status.equalsIgnoreCase("Cancel_by_driver")) {
                        title = "New Booking Request";
                        try {
                            requestId = String.valueOf(jsonObject.get("request_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", jsonObject.toString());
                        intent1.putExtra("object", jsonObject.toString());
                        sendBroadcast(intent1);
                        calltaxiStatusClicked(title,"Your trip is cancelled by driver",requestId);
                    }
                } else {
                    if("food".equals(notifyType)) {
                        intent = new Intent(this, FoodHomeActivity.class);
                    } else {
                        intent = new Intent(this, SplashActivity.class);
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                            PendingIntent.FLAG_ONE_SHOT);
                    String channelId = "1";
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                            .setSmallIcon(R.drawable.logo_green)
                            //.setLargeIcon(bitmap)
                            .setContentTitle(title)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentText(msg)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
                    NotificationManager notificationManager = (NotificationManager)
                            getSystemService(Context.NOTIFICATION_SERVICE);
                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // Channel human readable title
                        NotificationChannel channel = new NotificationChannel(channelId,
                                "Cloud Messaging Service",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }
                    notificationManager.notify(getNotificationId(), notificationBuilder.build());
                }

//              Drawable drawable = getApplicationInfo().loadIcon(getPackageManager());
//              Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            }
        }

    }

    private void calltaxiStatusClicked(String title, String msg, String requestId) {
        intent = new Intent(this, TrackTaxiAct.class);
        intent.putExtra("request_id",requestId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = "1";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setSmallIcon(R.drawable.logo_green)
                //.setLargeIcon(bitmap)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(getNotificationId() /* ID of notification */ , notificationBuilder.build());

    }

    private static int getNotificationId() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(9000);
    }


}

