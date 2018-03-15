package com.huang.android.logistic.Firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.huang.android.logistic.Chat.Chat;
import com.huang.android.logistic.Lihat_Pesanan.Pending.DetailOrderPending;
import com.huang.android.logistic.Lihat_Pesanan.TrackHistory;
import com.huang.android.logistic.MainActivity;
import com.huang.android.logistic.Maps.LiveAllDriversMaps;
import com.huang.android.logistic.Maps.LiveMaps;
import com.huang.android.logistic.Model.PushNotificationAction.PushNotificationAction;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import java.util.Map;


/**
 * Created by davidwibisono on 12/17/17.
 */

public class FirebaseMessageService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("FCM","testing");

        if (remoteMessage.getData() != null) {
            if (remoteMessage.getData().size() > 0) {
                Map<String, String> data = remoteMessage.getData();
                String normalizeBody = Html.fromHtml(remoteMessage.getData().get("body") + "").toString();
                displayNotification(remoteMessage.getData().get("title"), normalizeBody,data);
            }
        }

    }

    private void displayNotification(String title, String content, Map<String,String > data){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());

        notificationBuilder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Hearty365")
                .setPriority(NotificationCompat.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                .setContentTitle(title)
                .setContentText(content)
                .setContentInfo("Info");
        String action = data.get("action");
        String job_order = data.get("job_order");
        String vendorToken = Utility.utility.getLoggedName(this).replace(" ","_");
        String subject = data.get("subject");
        if (vendorToken.equals(subject)) {
            if (action != null) {
                if (action.equals(PushNotificationAction.GPS_UPDATE)) {
                    final String driver = data.get("driver");
                    if (driver != null && TrackHistory.instance != null) {
                        TrackHistory.instance.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TrackHistory.instance.refreshDriverPosition(driver);
                            }
                        });
                    }
                    if (driver != null && LiveMaps.instance != null) {
                        if (driver.equals(LiveMaps.instance.driver)) {
                            LiveMaps.instance.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LiveMaps.instance.refetchBackgroundUpdate();
                                }
                            });
                        }
                    } else if (driver != null && LiveAllDriversMaps.instance != null) {
                        LiveAllDriversMaps.instance.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LiveAllDriversMaps.instance.refetchBackgroundUpdate(driver);
                            }
                        });
                    }
                    return;
                }
            }
            if (action != null && job_order != null) {
                if (action.equals(PushNotificationAction.CHAT_JOB_ORDER)) {

                    //check if the sender by self no need to burst notification
                    String vendor = Utility.utility.getLoggedName(getApplicationContext());
                    if (title.toLowerCase().contains(vendor.toLowerCase())) return;
                    else {

                        //check if chat activity is running on instance
                        Log.e("chat", Chat.instance + "");
                        if (Chat.instance != null) {
                            Chat.instance.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Chat.instance.refetchItems();
                                }
                            });
                        }
                        Intent goDetail = new Intent(getApplicationContext(), Chat.class);
                        goDetail.putExtra("joid", job_order);
                        PendingIntent goDetailPendingIntent =
                                PendingIntent.getActivity(
                                        this,
                                        0,
                                        goDetail,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                        notificationBuilder.setContentIntent(goDetailPendingIntent);
                    }
                } else if (action.equals(PushNotificationAction.NEW_JO)) {
                    Intent goDetail = new Intent(getApplicationContext(), DetailOrderPending.class);
                    goDetail.putExtra("joid", job_order);
                    goDetail.putExtra("notif", PushNotificationAction.NEW_JO);
                    PendingIntent goDetailPendingIntent =
                            PendingIntent.getActivity(
                                    this,
                                    0,
                                    goDetail,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    notificationBuilder.setContentIntent(goDetailPendingIntent);
                } else if (action.equals(PushNotificationAction.RTO_JO)) {
                    Intent goDetail = new Intent(getApplicationContext(), DetailOrderPending.class);
                    String value = data.get("value");
                    goDetail.putExtra("joid", job_order);
                    goDetail.putExtra("value", value);
                    goDetail.putExtra("notif", PushNotificationAction.RTO_JO);
                    PendingIntent goDetailPendingIntent =
                            PendingIntent.getActivity(
                                    this,
                                    0,
                                    goDetail,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    notificationBuilder.setContentIntent(goDetailPendingIntent);
                } else {
                    Intent goDetail = new Intent(getApplicationContext(), MainActivity.class);
                    PendingIntent goDetailPendingIntent =
                            PendingIntent.getActivity(
                                    this,
                                    0,
                                    goDetail,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    notificationBuilder.setContentIntent(goDetailPendingIntent);
                }
            } else {
                Intent goDetail = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent goDetailPendingIntent =
                        PendingIntent.getActivity(
                                this,
                                0,
                                goDetail,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                notificationBuilder.setContentIntent(goDetailPendingIntent);
            }

            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notificationBuilder.build());
        }
    }
}
