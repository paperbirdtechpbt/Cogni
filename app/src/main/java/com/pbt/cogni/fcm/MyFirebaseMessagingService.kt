package com.pbt.cogni.fcm

import android.annotation.SuppressLint
import android.app.ActivityManager.RunningAppProcessInfo

import android.app.NotificationManager.IMPORTANCE_HIGH

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build

import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

import com.firebase.client.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

import com.pbt.cogni.R
import com.pbt.cogni.activity.call.CallActivity
import com.pbt.cogni.activity.chat.ChatActivity
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppUtils

import com.pbt.cogni.util.Config
import com.pbt.cogni.util.MyPreferencesHelper
import org.json.JSONObject
import android.graphics.BitmapFactory
import android.app.*


private const val CHANNEL_ID = "my_channel"


class MyFirebaseMessagingService : FirebaseMessagingService() {
    var isInBackground: Boolean? = null


    @SuppressLint("RemoteViewLayout")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {



        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
//            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
//
            try {

                val myProcess = RunningAppProcessInfo()
                ActivityManager.getMyMemoryState(myProcess)

                isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;


                if (isInBackground == true) {
                    val kgMgr = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
                    val showing = kgMgr.inKeyguardRestrictedInputMode()
                    if (showing) {
                        //app inbackground but  locked
                        openIntent(remoteMessage)

                    } else {


                        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(android.R.drawable.arrow_up_float)
                            .setContentTitle("title")
                            .setContentText("description")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setFullScreenIntent(getFullScreenIntent(), true)


                        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                        with(notificationManager) {
                            buildChannel()

                            val notification = builder.build()

                            notify(0, notification)
                        }
                        //app inbackground but not locked

                    }
                } else {
                    //app in foreground
                    openIntent(remoteMessage)

                }

//                val fullScreenIntent = Intent(this, CallActivity::class.java)
//                val fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
//                    fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//                val notificationBuilder =
//                    NotificationCompat.Builder(this, CHANNEL_ID)
////                        .setSmallIcon(R.drawable.notification_icon)
//                        .setContentTitle("Incoming call")
//                        .setContentText("(919) 555-1234")
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//                        .setCategory(NotificationCompat.CATEGORY_CALL)
//
////                 Use a full-screen intent only for the highest-priority alerts where you
////                 have an associated activity that you would like to launch after the user
////                 interacts with the notification. Also, if your app targets Android 10
////                 or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
////                 order for the platform to invoke this notification.
////                        .setFullScreenIntent(fullScreenPendingIntent, true)
//
//                val incomingCallNotification = notificationBuilder.build()

//                this.runOnUiThread(Runnable // start actions in UI thread
//                {
//                    displayData() // this action have to be in UI thread
//                })


//_____________-------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

//                var obj: JSONObject = JSONObject(remoteMessage.data.toString())
//                if (obj.getJSONObject("data").has("payload")) {
//                    var payload: JSONObject = obj.getJSONObject("data").getJSONObject("payload")
//
//                    if (payload.has("title") && payload.getString("title").equals("ChatMessage")) {
//
//                        if (AppUtils.isAppIsInBackground(this) && !ChatActivity.isChatVisible)
//                            sendMessageToServer(payload)
//
//                        if (!ChatActivity.isChatVisible) {
//                            sendNotification(
//                                MyPreferencesHelper.getUser(this)!!.FirstName,
//                                payload.getString("message")
//                            )
//                        }
//                    }
//                }
                //---------------------------i-m-p-o-r-t-a-n-t------------------------------------//

            } catch (e: Exception) {
                if (AppUtils.DEBUG)
                    AppUtils.logError(TAG, "Exception : " + e.message)
            }

//            Log.d(TAG, "Message  obj: "+)


//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow()
//            }

        }

//    //     Check if message contains a notification payload.
        remoteMessage.notification?.let {

//            val intentt = Intent(this, CallActivity::class.java).apply {
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            }
//            intentt.putExtra("notfication", false)
//
//
//            startActivity(intentt)
//            Log.d(TAG, "Message Notification Body: ${it.body}")
//            sendNotification(it.body.ge)
        }

    }

    private fun NotificationManager.buildChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name ="channelId" //getString(R.string.notification_channel_name)
            val descriptionText ="Incoming Call" //getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }




//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "Example Notification Channel"
//            val descriptionText = "This is used to demonstrate the Full Screen Intent"
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//            }
//
//            createNotificationChannel(channel)
//        }
    }


    private fun getFullScreenIntent(): PendingIntent? {

        val destination =
            CallActivity::class.java
        val intent = Intent(this, destination)

        // flags and request code are 0 for the purpose of demonstration
        return PendingIntent.getActivity(this, 0, intent, 0)
    }


    private fun openIntent(remoteMessage: RemoteMessage) {
        AppUtils.logDebug(TAG, "====>> " + Gson().toJson(remoteMessage.data))

        val intent = Intent(this, CallActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra("notfication", false)
        startActivity(intent)

        Log.d("Tutorialspoint.com", "Your application is in ForeGround state")
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createnotifcationChannel(notificationManager: NotificationManager) {
        val channelName = "channelname"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

    //new Token genrated
    override fun onNewToken(token: String) {
        MyPreferencesHelper.setStringValue(this, AppConstant.PREF_TOKEN, token)
        sendRegistrationToServer(token)
    }

    //registion token  send
    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    private fun customeNotification() {
        // Get the layouts to use in the custom notification
        val notificationLayout = RemoteViews(packageName, R.layout.custom_push)
//        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.notification_large)
        val channelId = getString(R.string.default_notification_channel_id)
// Apply the layouts to the notification
        val customNotification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
//            .setCustomBigContentView(notificationLayoutExpanded)
            .build()
    }

    //show notification
    private fun sendNotification(title: String, message: String) {
        val sound: Uri =
            Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.notification);
//        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(applicationContext, sound)
        r.play()

        val intent = Intent(this, ChatActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//        val sound: Uri =  Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.notification);
        val notificationLayout = RemoteViews(packageName, com.pbt.cogni.R.layout.custom_push)
//        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        notificationLayout.setTextViewText(R.id.title, title);
        notificationLayout.setTextViewText(R.id.text, message);
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic__chat_profile)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

            .setAutoCancel(true)
            .setCustomContentView(notificationLayout)
            .setSound(sound)
//            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }


    fun sendMessageToServer(payload: JSONObject) {

        if (!AppUtils.isNetworkConnected(this)) {
            Toast.makeText(this, "Please Connect To Internet !", Toast.LENGTH_SHORT).show()
        } else {
            Firebase.setAndroidContext(this)
            val reference1: Firebase? =
                Firebase(Config.BASE_FIREBASE_URLC.toString() + payload.getString("chatID"))
            reference1!!.child(payload.getString("messageID")).child("read").setValue(1)
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

}