package com.pbt.cogni.fcm

import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.firebase.client.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.pbt.cogni.R
import com.pbt.cogni.activity.call.CallActivity
import com.pbt.cogni.activity.chat.ChatActivity
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppConstant.Companion.CALL
import com.pbt.cogni.util.AppConstant.Companion.CONST_CHAT_ID
import com.pbt.cogni.util.AppConstant.Companion.CONST_CHAT_MESSAGE
import com.pbt.cogni.util.AppConstant.Companion.CONST_DATA
import com.pbt.cogni.util.AppConstant.Companion.CONST_MESSAGE
import com.pbt.cogni.util.AppConstant.Companion.CONST_MESSAGE_ID
import com.pbt.cogni.util.AppConstant.Companion.CONST_NOTI_TITLE_INCOMMING_CALL
import com.pbt.cogni.util.AppConstant.Companion.CONST_NUMBER
import com.pbt.cogni.util.AppConstant.Companion.CONST_PAYLOAD
import com.pbt.cogni.util.AppConstant.Companion.CONST_READ_STATUS
import com.pbt.cogni.util.AppConstant.Companion.CONST_TITLE
import com.pbt.cogni.util.AppConstant.Companion.ROOM_ID
import com.pbt.cogni.util.AppConstant.Companion.ROOM_ID_SMALL
import com.pbt.cogni.util.AppConstant.Companion.STR_INCOMMING_VOICE_CALL
import com.pbt.cogni.util.AppConstant.Companion.STR_NO_INTERNET
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.Config.BASE_FIREBASE_URL
import com.pbt.cogni.util.MyPreferencesHelper
import org.json.JSONObject

private const val CHANNEL_ID = "my_channel"


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var isInBackground: Boolean? = null
    var NOTIFICATION_ID = 1
    var NOTifiicatioid = 2


    //new Token genrated
    override fun onNewToken(token: String) {
        MyPreferencesHelper.setStringValue(this, AppConstant.PREF_TOKEN, token)
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {

            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            try {

                var obj: JSONObject = JSONObject(remoteMessage.data.toString())

                if (obj.getJSONObject(CONST_DATA).has(CONST_PAYLOAD)) {
                    var payload: JSONObject =
                        obj.getJSONObject(CONST_DATA).getJSONObject(CONST_PAYLOAD)

                    mobilenumber = payload.getString(CONST_MESSAGE)
                    val intent = Intent(this, CallActivity::class.java)
                    intent.putExtra("mobilenumber", mobilenumber)

                    if (payload.has(CONST_TITLE) && payload.getString(CONST_TITLE)
                            .equals(CONST_CHAT_MESSAGE)
                    ) {

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
                        var boolean: Boolean = payload.getString("call").toBoolean()
                        checkPhoneStatus(
                            mobilenumber!!,
                            payload.getString(ROOM_ID_SMALL),
                            boolean,
                            remoteMessage
                        )
                    }
                }

            } catch (e: Exception) {
                if (AppUtils.DEBUG)
                    AppUtils.logError(TAG, "Exception : " + e.message)
            }

        }

        remoteMessage.notification?.let {
            if (AppUtils.DEBUG)
                AppUtils.logDebug(TAG, " Normal Notificatio  : " + it.body)
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
                AppUtils.logDebug(TAG, "app inbackground  locked")
                openIntent(number, roomId, call, remoteMessage)

            } else {
                //app inbackground but not locked
                AppUtils.logDebug(TAG, "app inbackground but not locked")
                popUpNotificaiton(number, roomId, call, remoteMessage)
            }
        } else {
            //app in foreground
            AppUtils.logDebug(TAG, "app in Foreground")
            openIntent(number, roomId, call, remoteMessage)
        }
    }


    private fun popUpNotificaiton(
        number: String,
        roomId: String,
        call: Boolean,
        remoteMessage: RemoteMessage
    ) {

        val buttonIntent = Intent(this, ButtonReceiver::class.java)
        buttonIntent.putExtra("notificationId", NOTIFICATION_ID)


        val resultIntent =
            Intent(this, CallActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        passdata(resultIntent, number, roomId, call, remoteMessage)


        val resultPendingIntent =
            PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

//Create the PendingIntent
        val btPendingIntent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0)

        val notificationLayout = RemoteViews(packageName, R.layout.item_incoming_call)
        notificationLayout.setTextViewText(R.id.txtCallerName, number)
        notificationLayout.setOnClickPendingIntent(R.id.txtanswer, resultPendingIntent)


        notificationLayout.setOnClickPendingIntent(R.id.txtreject, btPendingIntent)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_call)
//            .setSound(soundUri)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000, 1000, 1000))
            .setContentText("From" + " ${remoteMessage.from}")
            .setCustomContentView(notificationLayout)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(resultPendingIntent, true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        with(notificationManager) {
            buildChannel()

            val notification = builder.build()
            notification.flags = Notification.FLAG_AUTO_CANCEL

            notify(1, notification)
        }

    }

    private fun passdata(
        resultIntent: Intent,
        number: String,
        roomId: String,
        call: Boolean,
        remoteMessage: RemoteMessage
    ) {
        resultIntent.putExtra(CONST_NUMBER, number)
        resultIntent.putExtra(CALL, call)
        resultIntent.putExtra(ROOM_ID, roomId)
        resultIntent.putExtra(CONST_NUMBER, number)
    }


    private fun NotificationManager.buildChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channelId"
            val descriptionText = STR_INCOMMING_VOICE_CALL
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel("CHANNEL_ID", name, importance)
            mChannel.description = descriptionText
            mChannel.enableVibration(true)
            playsound()
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun playsound() {
        val soundUri =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.callringotn)

        ringtone = RingtoneManager.getRingtone(applicationContext, soundUri)
        ringtone?.play()
    }

    fun stopSound() {
        ringtone?.stop()

    }

    private fun openIntent(
        number: String,
        roomId: String,
        call: Boolean,
        remoteMessage: RemoteMessage
    ) {
        val intent = Intent(this, CallActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        passdata(intent, number, roomId, call, remoteMessage)
        startActivity(intent)
    }

    //show chat notification
    private fun sendNotification(title: String, message: String) {
        val sound: Uri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.callringotn);
        val r = RingtoneManager.getRingtone(applicationContext, sound)
        r.play()

        val intent = Intent(this, CallActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationLayout = RemoteViews(packageName, R.layout.item_incoming_call)
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
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

   //firebase message change read status
    fun sendMessageToServer(payload: JSONObject) {
        if (!AppUtils.isNetworkConnected(this)) {
            Toast.makeText(this, STR_NO_INTERNET, Toast.LENGTH_SHORT).show()
        } else {
            Firebase.setAndroidContext(this)
            val reference1: Firebase? = Firebase(BASE_FIREBASE_URL.toString() + payload.getString(CONST_CHAT_ID))
            reference1!!.child(payload.getString(CONST_MESSAGE_ID)).child(CONST_READ_STATUS).setValue(1)
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        public var ringtone: Ringtone? = null
        var mobilenumber: String? = null
    }
}