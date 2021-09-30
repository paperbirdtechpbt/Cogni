package com.pbt.cogni.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Parcel
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.firebase.client.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pbt.cogni.R
import com.pbt.cogni.activity.chat.Chat
import com.pbt.cogni.activity.chat.ChatActivity
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.Config
import org.json.JSONObject
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
//            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            try {

                var obj: JSONObject = JSONObject(remoteMessage.data.toString())
                if (obj.getJSONObject("data").has("payload")) {
                    var payload: JSONObject = obj.getJSONObject("data").getJSONObject("payload")

                    if (payload.has("title") && payload.getString("title").equals("ChatMessage") && AppUtils.isAppIsInBackground(this)) {
                         sendMessageToServer(payload)
                    }
                }

                Log.d(
                    TAG,
                    "Message  payload: " + obj.getJSONObject("data").getJSONObject("payload")
                )


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

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
//            sendNotification(it.body.ge)
        }

    }

    //new Token genrated
    override fun onNewToken(token: String) {
        sendRegistrationToServer(token)
    }

    //registion token  send
    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    //show notification
    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic__chat_profile)
            .setContentTitle(messageBody)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
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
            val reference1: Firebase? = Firebase(Config.BASE_FIREBASE_URLC.toString() + payload.getString("chatID"))
            reference1!!.child(payload.getString("messageID")).child("read").setValue(1)
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

}