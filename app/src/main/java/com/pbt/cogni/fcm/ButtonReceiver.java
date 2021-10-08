package com.pbt.cogni.fcm;

import static com.pbt.cogni.fcm.MyFirebaseMessagingService.*;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;

public class ButtonReceiver extends BroadcastReceiver  {

    @Override

    public void onReceive(Context context, Intent intent) {
        Log.d("##onrecieve","On recivece");
        int notificationId = intent.getIntExtra("notificationId", 0);


        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.cancel(notificationId);
        notificationManager.cancelAll();
        new MyFirebaseMessagingService().stopSound();

    }
}
