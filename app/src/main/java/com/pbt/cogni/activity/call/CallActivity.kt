package com.pbt.cogni.activity.call


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.modules.core.PermissionListener
import com.pbt.cogni.R
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.home.MainActivity
import com.pbt.cogni.fcm.MyFirebaseMessagingService
import com.pbt.cogni.fcm.MyFirebaseMessagingService.Companion.payLOAD
import com.pbt.cogni.fcm.MyFirebaseMessagingService.Companion.ringtone
import com.pbt.cogni.fcm.MyFirebaseMessagingService.Companion.roomId
import com.pbt.cogni.fragment.audioVideoCall.AudioVideoViewModel
import com.pbt.cogni.fragment.audioVideoCall.AudioVideoViewModel.Companion.myRoomId
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.model.UserDetailsData
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import kotlinx.android.synthetic.main.activity_call_activity.*
import kotlinx.android.synthetic.main.activity_login.*
import org.jitsi.meet.sdk.*
import org.jitsi.meet.sdk.log.JitsiMeetLogger
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.net.URL


class CallActivity : AppCompatActivity(),JitsiMeetActivityInterface  {

    var isVideoCall=false
//    var sendcallid=""
    var sendid=""
    var reciverid=""
    var typeocall=""
    var typeocallBoolean=false
    var postmilli:Long?=null
    var currentMilli:Long?=null
    private var autodisconnect=false
    var handler= Handler()


    public override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_activity)

        showOnLockScreenAndTurnScreenOn()

//        autodisconnect.setl

        val  sendcallid= intent.extras?.getString("makecallRoomId")
        AppUtils.logDebug(TAG,"roomId=="+ roomId)

        sendid= intent.extras?.getString("sendid").toString()
        reciverid= intent.extras?.getString("reciverid").toString()
        typeocall= intent.extras?.getString("typeofcall").toString()
        typeocallBoolean= intent.extras?.getString("isVideoCall").toBoolean()
        postmilli=intent.extras?.getLong("currentmilli")
        currentMilli=System.currentTimeMillis()


        if (sendcallid.isNullOrEmpty()){

            btnAcceptCall.setOnClickListener{

                StartCallProcess(roomId, isVideoCall)
            }
            btn_rejectCall.setOnClickListener{

                MyFirebaseMessagingService().stopSound()
                finish()
            }
            if (intent?.extras?.getString("notification") != null) {
                var sendername= payLOAD?.getString("senderName")
                var message= payLOAD?.getString("message")

                layout_callername.setText(sendername)
                layout_callernumber.setText(message)
//            MyFirebaseMessagingService().setautoCancle()
                incomingCallLayout.visibility = View.VISIBLE
            }
            else{

                StartCallProcess(roomId,isVideoCall)
            }
        }
        else{
            StartCallProcess(sendcallid,typeocallBoolean)

        }
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        supportActionBar?.hide()



    }

    private fun setAutoDisconnect(boolean:Boolean) {
        val timer:Long

        if (boolean){
            if (postmilli.toString()=="0"){
                postmilli=29000
                Log.d("##diconnect","postmilli---"+postmilli.toString())
                timer=postmilli!!
            }
            else{
                val mytimer=currentMilli!!-postmilli!!
                val timerrr:Long=30000-mytimer
                timer=timerrr
            }
        }
        else{ timer=30000 }
        if (!autodisconnect){
            handler.postDelayed({
                ringtone?.stop()
                finish()
            }, timer)
        }
    }

    private fun showOnLockScreenAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    private fun StartCallProcess(roomId: String,isVideoCall:Boolean) {

        try{


            val view = JitsiMeetView(this)
            setJitsiListener(view)

            var jitsiMeetUserInfo :JitsiMeetUserInfo=  JitsiMeetUserInfo()
            jitsiMeetUserInfo = JitsiMeetUserInfo()
            jitsiMeetUserInfo.setDisplayName("")
            val serverUrl= URL("https://meet.jit.si")
            if(isVideoCall){
                val options: JitsiMeetConferenceOptions = JitsiMeetConferenceOptions.Builder()
                    .setServerURL(serverUrl)
//                .setRoom("https://meet.jit.si/$roomId")
                    .setRoom(roomId)
                    .setAudioMuted(false)
                    .setVideoMuted(false)
                    .setAudioOnly(false)
//                .setWelcomePageEnabled(false)
                    .setConfigOverride("invite.enabled", false)
                    .setConfigOverride("meeting-password.enabled", false)
                    .setConfigOverride("requireDisplayName", false)
                    .setConfigOverride("toolbox.enabled", false)
                    .setConfigOverride("meeting-password.enabled", false)
                    .setConfigOverride("meeting-name.enabled", false)
                    .setFeatureFlag("resolution",true)
                    .setUserInfo(jitsiMeetUserInfo)
                    .build()


                view.join(options)
                setContentView(view)
            }
            else{
                val options: JitsiMeetConferenceOptions = JitsiMeetConferenceOptions.Builder()
                    .setServerURL(serverUrl)
//                .setRoom("https://meet.jit.si/$roomId")
                    .setRoom(roomId)
                    .setAudioMuted(false)
                    .setVideoMuted(true)
                    .setAudioOnly(true)
//                .setWelcomePageEnabled(false)
                    .setConfigOverride("invite.enabled", false)
                    .setConfigOverride("meeting-password.enabled", false)
                    .setConfigOverride("requireDisplayName", false)
                    .setConfigOverride("toolbox.enabled", false)
                    .setConfigOverride("meeting-password.enabled", false)
                    .setConfigOverride("meeting-name.enabled", false)
                    .setFeatureFlag("resolution",true)
                    .setUserInfo(jitsiMeetUserInfo)
                    .build()


                view.join(options)
                setContentView(view)
            }

        }
        catch (e:Exception)
        {
            AppUtils.logError(TAG,e.message.toString())
        }


    }
    private fun setJitsiListener(view: JitsiMeetView) {
        view.listener = object : JitsiMeetViewListener {

            private fun on(name: String, data: Map<String, Any>) {
//                Log.d("ReactNative", JitsiMeetViewListener::class.java.simpleName + " " + name + " " + data)
            }


            override fun onConferenceJoined(data: Map<String, Any>) {
                on("CONFERENCE_JOINED", data)
                MyFirebaseMessagingService().stopSound()


                AppUtils.logDebug(TAG,"CONFERENCE_JOINED")

            }

            override fun onConferenceTerminated(data: MutableMap<String, Any>?) {
                MyFirebaseMessagingService().stopSound()

                AppUtils.logDebug(TAG,"onConferenceTerminated")
                val userdataDetails: UserDetailsData? = MyPreferencesHelper.getUser(this@CallActivity)

                val apiclient = ApiClient.getClient()

                val apiInterface = apiclient?.create(ApiInterface::class.java)
                AudioVideoViewModel.myRoomId =getRandomString(8)

                val call = apiInterface?.makeCall( sendid,
                    reciverid,
                    myRoomId,"EndMeeting",typeocall)
                call?.enqueue(object : retrofit2.Callback<HttpResponse> {
                    override fun onResponse(
                        call: Call<HttpResponse>,
                        response: Response<HttpResponse>
                    ) {

                        AppUtils.logDebug(AudioVideoViewModel.TAG, "Call Successfull")
                        val intent=Intent(this@CallActivity,MainActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
                        Log.d("####makeCAllresponse",t.message.toString())
                    }


                })
                JitsiMeetLogger.i("Conference terminated: " + data)
                startActivity(Intent(this@CallActivity, MainActivity::class.java))
                finish()

            }


            override fun onConferenceWillJoin(data: Map<String, Any>) {
                MyFirebaseMessagingService().stopSound()

                on("CONFERENCE_WILL_JOIN", data)
//                AudioVideoViewModel().sendCall(this@CallActivity,"36","1",
//                    myRoomId,"StartMeeting")

                AppUtils.logDebug(TAG,"CONFERENCE_WILL_JOIN")
            }

        }
    }


    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }


    override fun onBackPressed() {
        val boolean=showAlertDialog()
        if (boolean){
            this.finish()

            super.onBackPressed()
        }

    }

    override fun onUserLeaveHint() {

        showAlertDialog()
        this.finish()
        super.onUserLeaveHint()
    }


    private fun showAlertDialog() :Boolean{
        var boolean=false
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Leave Meeting")

        builder.setMessage(R.string.dialogMessage)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            finish()
            boolean=true
        }

        builder.setNeutralButton("Cancel"){dialogInterface , which ->
            Toast.makeText(applicationContext,"clicked cancel\n operation cancel",Toast.LENGTH_LONG).show()

        }
        builder.setNegativeButton("No"){dialogInterface, which ->
            Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
            boolean=false
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
       return boolean
    }


    companion object {
        private const val TAG = "CallActivity"
//        private const val APPRTC_URL = "https://appr.tc"
//        private const val UPPER_ALPHA_DIGITS = "ACEFGHJKLMNPQRUVWXY123456789"
        var name: String? = ""
        var sendername: String? = ""
//        private const val STAT_CALLBACK_PERIOD = 1000
    }


    override fun requestPermissions(p0: Array<out String>?, p1: Int, p2: PermissionListener?) {

    }


}

