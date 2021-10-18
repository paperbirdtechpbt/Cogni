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

import android.app.*

import android.content.ContentResolver

import android.app.PendingIntent
import android.app.NotificationManager
import android.media.Ringtone
import com.pbt.cogni.util.AppConstant.Companion.CALL
import com.pbt.cogni.util.AppConstant.Companion.CONST_CHAT_MESSAGE
import com.pbt.cogni.util.AppConstant.Companion.CONST_DATA
import com.pbt.cogni.util.AppConstant.Companion.CONST_MESSAGE
import com.pbt.cogni.util.AppConstant.Companion.CONST_NOTI_TITLE_INCOMMING_CALL

import com.pbt.cogni.util.AppConstant.Companion.CONST_PAYLOAD
import com.pbt.cogni.util.AppConstant.Companion.CONST_TITLE
import com.pbt.cogni.util.AppConstant.Companion.ROOM_ID
import com.pbt.cogni.util.AppConstant.Companion.SMALL_ROOM_ID
import android.app.NotificationChannel


private const val CHANNEL_ID = "my_channel"


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var isInBackground: Boolean? = null
    var NOTIFICATION_ID = 1



    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("RemoteViewLayout")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {


        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
//
            try {


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

                val obj = JSONObject(remoteMessage.data.toString())


                if (obj.getJSONObject(CONST_DATA).has(CONST_PAYLOAD)) {
                    val payload: JSONObject = obj.getJSONObject(CONST_DATA).getJSONObject(CONST_PAYLOAD)

                    mobilenumber = payload.getString(CONST_MESSAGE)
                    Log.d("##Mynumber", mobilenumber.toString())
//                    val intent = Intent(this, CallActivity::class.java)
//                    intent.putExtra("mobilenumber", mobilenumber)

                    sendernamee= payload.getString("senderName")
                    sendernumberr=payload.getString(CONST_MESSAGE)


                    if (payload.has(CONST_TITLE) && payload.getString(CONST_TITLE).equals(
                            CONST_CHAT_MESSAGE)) {

                        if (AppUtils.isAppIsInBackground(this) && !ChatActivity.isChatVisible)
                            sendMessageToServer(payload)

                        if (!ChatActivity.isChatVisible) {
                            sendNotification(
                                MyPreferencesHelper.getUser(this)!!.FirstName,
                                payload.getString(CONST_MESSAGE)
                            )
                        }
                    } else if (payload.has(CONST_TITLE) && payload.getString(CONST_TITLE)
                            .equals(CONST_NOTI_TITLE_INCOMMING_CALL)
                    ) {
                        val boolean: Boolean = payload.getString("call").toBoolean()
                        Log.d("##checkboolenad",boolean.toString())
                        checkPhoneStatus(
                            mobilenumber!!,
                            payload.getString(SMALL_ROOM_ID),
                            boolean,
                            remoteMessage
                        )
                    }
                }
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

        }

    }

    private fun checkPhoneStatus(
        number: String, roomId: String, call: Boolean, remoteMessage: RemoteMessage
    ) {

        val myProcess = RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(myProcess)

        isInBackground =
            myProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND //true


        if (isInBackground == true) {
            val kgMgr = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            val showing = kgMgr.inKeyguardRestrictedInputMode()
            if (showing) {
                //app inbackground but  locked
                Log.d("##checkstatus", "app inbackground  locked")
                openIntent(number, roomId, call, remoteMessage)


            } else {
                //app inbackground but not locked
                Log.d("##checkstatus", "app inbackground but not locked")

                popUpNotificaiton(number, roomId, call, remoteMessage)

            }
        } else {
            //app in foreground
            Log.d("##checkstatus", "app in Foreground")
            openIntent(number, roomId, call, remoteMessage)
        }
    }
    private fun popUpNotificaiton(number: String, roomId: String, call: Boolean, remoteMessage: RemoteMessage) {
        Log.d("##notification","In method send Notification ")
        val sound: Uri =
            Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.callringotn);
//        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        ringtone = RingtoneManager.getRingtone(applicationContext, sound)
        ringtone?.play()

        val buttonIntent = Intent(this, ButtonReceiver::class.java)
        buttonIntent.putExtra("notificationId", NOTIFICATION_ID)



        val intent = Intent(this, CallActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        passdata(intent,number,roomId,call,remoteMessage,"notificatino")


        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notificationLayout = RemoteViews(packageName, R.layout.item_incoming_call)

        val resultIntent = Intent(this, CallActivity::class.java)
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        passdata(resultIntent,number,roomId,call,remoteMessage,"notificaiton")
//        passdata(resultIntent, number, roomId, call, remoteMessage)


        val resultPendingIntent =
            PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

//Create the PendingIntent
        val btPendingIntent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0)


        notificationLayout.setOnClickPendingIntent(R.id.txtanswer, resultPendingIntent)
        notificationLayout.setOnClickPendingIntent(R.id.txtreject, btPendingIntent)
        notificationLayout.setTextViewText(R.id.txtCallerName, sendernamee)



        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic__chat_profile)
            .setContentTitle("title")
            .setContentText("message")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCustomContentView(notificationLayout)
//            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent,true)


//         Since android Oreo notification channel is needed.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channelname"
            val description =" getString(R.string.channel_description)"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val buildNotification = notificationBuilder.build()
        val mNotifyMgr = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.notify(1,  buildNotification)
    }

    private fun passdata(
        resultIntent: Intent,
        number: String,
        roomId: String,
        call: Boolean,
        remoteMessage: RemoteMessage,
        notification:String
    ) {
        Log.d("##passdata",number+roomId+call+remoteMessage)
        resultIntent.putExtra(AppConstant.CONST_SENDER_NAME, number)
        resultIntent.putExtra(CALL, call)
        resultIntent.putExtra(ROOM_ID, roomId)
        resultIntent.putExtra(remoteMessage.from, AppConstant.CONST_SENDER_MOBILE_NUMBER)
        resultIntent.putExtra("notification",notification)

    }



     fun playsound() {
        val soundUri =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.callringotn)

        ringtone = RingtoneManager.getRingtone(applicationContext, soundUri)
        ringtone?.play()
    }

    fun stopSound() {
        Log.d("##onstopsound","Stop sound")
        ringtone?.stop()
    }



    private fun openIntent(
        number: String,
        roomId: String,
        call: Boolean,
        remoteMessage: RemoteMessage
    ) {
        AppUtils.logDebug(TAG, "====>> " + Gson().toJson(remoteMessage.data))

        val intent = Intent(this, CallActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        passdata(intent, number, roomId, call, remoteMessage,"notificaiton")
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


    //show notification
    private fun sendNotification(title: String, message: String) {
        Log.d("##notification","In method send Notification ")
        val sound: Uri =
            Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.callringotn);
//        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        ringtone = RingtoneManager.getRingtone(applicationContext, sound)
        ringtone?.play()

        val buttonIntent = Intent(this, ButtonReceiver::class.java)
        buttonIntent.putExtra("notificationId", NOTIFICATION_ID)

        val intent = Intent(this, CallActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notificationLayout = RemoteViews(packageName, R.layout.item_incoming_call)

        val resultIntent =
            Intent(this, CallActivity::class.java)
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        passdata(resultIntent, number, roomId, call, remoteMessage)

        val resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val btPendingIntent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0)

        notificationLayout.setOnClickPendingIntent(R.id.txtanswer, resultPendingIntent)
        notificationLayout.setOnClickPendingIntent(R.id.txtreject, btPendingIntent)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic__chat_profile)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCustomContentView(notificationLayout)
            .setContentIntent(pendingIntent)
//            .setFullScreenIntent(pendingIntent,true)


//         Since android Oreo notification channel is needed.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channelname"
            val description =" getString(R.string.channel_description)"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val buildNotification = notificationBuilder.build()
        val mNotifyMgr = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotifyMgr.notify(1,  buildNotification)
    }



    fun sendMessageToServer(payload: JSONObject) {

        if (!AppUtils.isNetworkConnected(this)) {
            Toast.makeText(this, "Please Connect To Internet !", Toast.LENGTH_SHORT).show()
        } else {
            Firebase.setAndroidContext(this)
            val reference1: Firebase? =
                Firebase(Config.BASE_FIREBASE_URL.toString() + payload.getString("chatID"))
            reference1!!.child(payload.getString("messageID")).child("read").setValue(1)
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
         var ringtone: Ringtone? = null
        var mobilenumber: String? = null
        var sendernamee=""
        var sendernumberr=""

    }

}