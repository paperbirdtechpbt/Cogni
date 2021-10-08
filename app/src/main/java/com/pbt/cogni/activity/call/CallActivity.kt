package com.pbt.cogni.activity.call

import android.os.Bundle
import com.pbt.cogni.R
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Handler
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.pbt.cogni.util.AppConstant.ROOM_ID
import org.appspot.apprtc.AppRTCClient
import org.appspot.apprtc.AppRTCClient.*
import org.appspot.apprtc.PeerConnectionClient
import org.appspot.apprtc.PeerConnectionClient.PeerConnectionEvents
import org.appspot.apprtc.PeerConnectionClient.PeerConnectionParameters
import org.appspot.apprtc.WebSocketRTCClient
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.IceCandidate
import org.webrtc.Logging
import org.webrtc.RendererCommon.ScalingType
import org.webrtc.SessionDescription
import org.webrtc.StatsReport
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoCapturer
import org.webrtc.VideoFrame
import org.webrtc.VideoRenderer
import org.webrtc.VideoRenderer.I420Frame
import org.webrtc.VideoSink
import java.lang.StringBuilder
import java.security.SecureRandom
import java.util.ArrayList
import android.view.WindowManager
import com.pbt.cogni.fcm.MyFirebaseMessagingService
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppConstant.CALL
import com.pbt.cogni.util.AppConstant.NUMBER
import kotlinx.android.synthetic.main.activity_call.*



class CallActivity : AppCompatActivity(), SignalingEvents, PeerConnectionEvents {
    private val remoteProxyRenderer = ProxyRenderer()
    private val localProxyVideoSink = ProxyVideoSink()
    private val remoteRenderers: MutableList<VideoRenderer.Callbacks> = ArrayList()
    private var peerConnectionClient: PeerConnectionClient? = null
    private var appRtcClient: AppRTCClient? = null
    private var signalingParameters: SignalingParameters? = null
    private var pipRenderer: SurfaceViewRenderer? = null
    private var fullscreenRenderer: SurfaceViewRenderer? = null
    private var logToast: Toast? = null
    private var activityRunning = false
    private var roomConnectionParameters: RoomConnectionParameters? = null
    private var peerConnectionParameters: PeerConnectionParameters? = null
    private var iceConnected = false
    private var isError = false
    private var callStartedTimeMs: Long = 0
    private var micEnabled = true
    private var isSwappedFeeds = false
    val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    var timerTextView: TextView? = null


    // Control buttons for limited UI
    private var button_speker: TextView? = null
    private var disconnectButton: TextView? = null
    private var cameraSwitchButton: TextView? = null
    private var audioManager: AudioManager? = null
    private var toggleMuteButton: TextView? = null
    private var videoCallEnable = false
    private var isSpeker = true

    var roomId: String? = ""
    var textView:TextView?=null



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public override fun onCreate(savedInstanceState: Bundle?) {
        cancleNotification()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_activity)


//        MyFirebaseMessagingService().stopSound() //stop ringtone when opening this class

        textView=findViewById(R.id.txtUsernameVoiceCall)


        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)


        supportActionBar?.hide()

        Log.d("##CHan", "oncreate")



//        videoCallEnable = intent.extras!!.getBoolean("Call")
        videoCallEnable=intent.extras!!.getBoolean(CALL)
        roomId = intent.extras?.getString(ROOM_ID)
        name = intent.extras?.getString(NUMBER)




        Log.d("##CHeckName", "----" + name + "---" + videoCallEnable.toString())

        if (videoCallEnable != true) {
            Log.d("##CHeckName", "InVoic Call")

        }

//        Log.e("##CAll ","Check is video call  "+)
        iceConnected = false
        signalingParameters = null

        // Create UI controls.
        pipRenderer = findViewById(R.id.pip_video_view)
        fullscreenRenderer = findViewById(R.id.fullscreen_video_view)
        disconnectButton = findViewById(R.id.button_call_disconnect)
        cameraSwitchButton = findViewById(R.id.button_call_switch_camera)
        toggleMuteButton = findViewById(R.id.button_call_toggle_mic)
        button_speker = findViewById(R.id.button_speker)
        if (!videoCallEnable) {

            Log.d("##CHance","-----")
            val imageview = findViewById<ImageView>(R.id.voicebackgroundumage)
           imageview.setVisibility(View.VISIBLE)

            val textView=findViewById<TextView>(R.id.txtUsernameVoiceCall)
            textView.setText(name)
            cameraSwitchButton?.setVisibility(View.GONE)
            button_speker?.setVisibility(View.VISIBLE)
            pipRenderer?.visibility=View.GONE
            fullscreenRenderer?.visibility= View.GONE
        }

        // Add buttons click events.
        disconnectButton?.setOnClickListener(OnClickListener { onCallHangUp() })
        cameraSwitchButton?.setOnClickListener(OnClickListener { onCameraSwitch() })
        toggleMuteButton?.setOnClickListener(OnClickListener {
            val enabled = onToggleMic()
            Log.e("##Call", "Toggle : $enabled")
            if (enabled) toggleMuteButton?.setBackground(getDrawable(R.drawable.ic_mic_on)) else toggleMuteButton?.setBackground(
                getDrawable(R.drawable.ic_mic_off)
            )
            //
            if (enabled) {
                if (videoCallEnable) {
                    Log.e("##Call", " Toggel Click Time $videoCallEnable")
                    setSpeakerOn()
                } else {
                    Log.e("##Call", " Toggel Click else  Time $videoCallEnable")
                    setHeadsetOn()
                }
            }
            toggleMuteButton?.setAlpha(if (enabled) 1.0f else 0.3f)
        })

        // Swap feeds on pip view click.
        pipRenderer?.setOnClickListener(OnClickListener { setSwappedFeeds(!isSwappedFeeds) })
        button_speker?.setOnClickListener(OnClickListener {
            if (button_speker?.getVisibility() == View.VISIBLE) {
            } else {
            }
            if (!isSpeker) {
                setHeadsetOn()
                isSpeker = true
                button_speker?.setBackground(getDrawable(R.drawable.ic_speaker))
            } else {
                setSpeakerOn()
                isSpeker = false
                button_speker?.setBackground(getDrawable(R.drawable.ic_speker_off))
            }
        })
        remoteRenderers.add(remoteProxyRenderer)

        // Create peer connection client.
        peerConnectionClient = PeerConnectionClient()

        // Create video renderers.
        pipRenderer?.init(peerConnectionClient!!.renderContext, null)
        pipRenderer?.setScalingType(ScalingType.SCALE_ASPECT_FIT)
        fullscreenRenderer?.init(peerConnectionClient!!.renderContext, null)
        fullscreenRenderer?.setScalingType(ScalingType.SCALE_ASPECT_FILL)
        pipRenderer?.setZOrderMediaOverlay(true)
        pipRenderer?.setZOrderMediaOverlay(true)
        pipRenderer?.setEnableHardwareScaler(true /* enabled */)
        fullscreenRenderer?.setEnableHardwareScaler(true /* enabled */)
        // Start with local feed in fullscreen and swap it to the pip when the call is connected.
        setSwappedFeeds(true /* isSwappedFeeds */)

        // Generate a random room ID with 7 uppercase letters and digits
        val randomRoomID = randomString(7, UPPER_ALPHA_DIGITS)
        // Show the random room ID so that another client can join from https://appr.tc
        val roomIdTextView = findViewById<TextView>(R.id.roomID)
        roomIdTextView.text = getString(R.string.room_id_caption) + "Budy4747"
        Log.d(TAG, getString(R.string.room_id_caption) + randomRoomID)

        // Connect video call to the 12random room
        roomId?.let { connectVideoCall(it) }
    }

     fun cancleNotification() {
//         txtTimerVoiceCall.setText(" ")
        val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        MyFirebaseMessagingService().stopSound()
    }


    private fun setSpeakerOn() {
        audioManager = this.getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager!!.isSpeakerphoneOn = true
        audioManager!!.mode = AudioManager.MODE_IN_COMMUNICATION
    }

    private fun setHeadsetOn() {
        audioManager = this.getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager!!.isSpeakerphoneOn = false
        audioManager!!.mode = AudioManager.MODE_IN_COMMUNICATION
    }

    // Create a random string
    private fun randomString(length: Int, characterSet: String): String {
        val sb = StringBuilder() //consider using StringBuffer if needed
        for (i in 0 until length) {
            val randomInt = SecureRandom().nextInt(characterSet.length)
            sb.append(characterSet.substring(randomInt, randomInt + 1))
        }
        return sb.toString()
    }

    // Join video call with randomly generated roomId
    private fun connectVideoCall(roomId: String) {

        Log.e("##Call", "RoomOID : " + roomId)
        val roomUri = Uri.parse(APPRTC_URL)
        val videoWidth = 0
        val videoHeight = 0
        peerConnectionParameters = PeerConnectionParameters(
            videoCallEnable,
            false,
            false,
            videoWidth,
            videoHeight,
            0, getString(R.string.pref_maxvideobitratevalue_default).toInt(),
            getString(R.string.pref_videocodec_default),
            true,
            false, getString(R.string.pref_startaudiobitratevalue_default).toInt(),
            getString(R.string.pref_audiocodec_default),
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            null
        )

        // Create connection client. Use the standard WebSocketRTCClient.
        // DirectRTCClient could be used for point-to-point connection
        appRtcClient = WebSocketRTCClient(this)
        // Create connection parameters.
        roomConnectionParameters = RoomConnectionParameters(
            roomUri.toString(),
            roomId,
            false,
            null
        )
        peerConnectionClient!!.createPeerConnectionFactory(
            applicationContext, peerConnectionParameters, this
        )
        startCall()
    }

    fun onCallHangUp() {
        disconnect()
    }

    fun onCameraSwitch() {
        if (peerConnectionClient != null) {
            peerConnectionClient!!.switchCamera()
        }
    }

    fun onToggleMic(): Boolean {
        if (peerConnectionClient != null) {
            micEnabled = !micEnabled
            peerConnectionClient!!.setAudioEnabled(micEnabled)
        }
        return micEnabled
    }

    private fun startCall() {
        if (appRtcClient == null) {
            Log.e(TAG, "AppRTC client is not allocated for a call.")
            Log.d("##CallCOnected","On swapped feeds")
            return
        }
        callStartedTimeMs = System.currentTimeMillis()

        // Start room connection.
        logAndToast(getString(R.string.connecting_to, roomConnectionParameters!!.roomUrl))
        appRtcClient!!.connectToRoom(roomConnectionParameters)
        Log.d("##CallCOnected","On swapped feeds")
    }

    @UiThread
    private fun callConnected() {

        var startTime: Long = 0
        var timerHandler = Handler()

        var timerRunnable: Runnable = object : Runnable {
            override fun run() {
                val millis = System.currentTimeMillis() - startTime
                var seconds = (millis / 1000).toInt()
                var minutes = seconds / 60
                var hours=minutes / 60
                seconds = seconds % 60
                minutes = minutes % 60
                timerTextView!!.text = String.format("%02d:%02d:%02d",hours, minutes, seconds)
                timerHandler.postDelayed(this, 500)
            }}

        timerTextView = findViewById(R.id.txtTimerVoiceCall)
        startTime = System.currentTimeMillis()
        timerHandler.postDelayed(timerRunnable, 0)
        Toast.makeText(this,"Call Connected",Toast.LENGTH_SHORT).show()

        val delta = System.currentTimeMillis() - callStartedTimeMs
        Log.i(TAG, "Call connected: delay=" + delta + "ms")
        if (peerConnectionClient == null || isError) {
            Log.w(TAG, "Call is connected in closed or error state")
            return
        }
        // Enable statistics callback.
        peerConnectionClient!!.enableStatsEvents(true, STAT_CALLBACK_PERIOD)
        setSwappedFeeds(true /* isSwappedFeeds */)
        Log.d("##CallCOnected","On VoiceCAll COnnected")
    }

    // Disconnect from remote resources, dispose of local resources, and exit.
    private fun disconnect() {

        activityRunning = false
        remoteProxyRenderer.setTarget(null)
        localProxyVideoSink.setTarget(null)
        if (appRtcClient != null) {
            appRtcClient!!.disconnectFromRoom()
            appRtcClient = null
        }
        if (pipRenderer != null) {
            pipRenderer!!.release()
            pipRenderer = null
        }
        if (fullscreenRenderer != null) {
            fullscreenRenderer!!.release()
            fullscreenRenderer = null
        }
        if (peerConnectionClient != null) {
            peerConnectionClient!!.close()
            peerConnectionClient = null
        }
        if (iceConnected && !isError) {
            setResult(RESULT_OK)
        } else {
            setResult(RESULT_CANCELED)
        }
        setHeadsetOn()
        finish()
        Log.d("##CallCOnected","On disconnect")
    }

    private fun disconnectWithErrorMessage(errorMessage: String) {
        if (!activityRunning) {
            Log.e(TAG, "Critical error: $errorMessage")
            disconnect()
        }
        //when was full else was user
        else {
            txtTimerVoiceCall.setText("Call Ended")
            Handler().postDelayed({
                finish()
            }, 1500)

//            AlertDialog.Builder(this)
//                .setTitle(getText(R.string.channel_error_title))
//                .setMessage(errorMessage)
//                .setCancelable(false)
//                .setNeutralButton(
//                    R.string.ok
//                ) { dialog, id ->
//                    dialog.cancel()
//                    disconnect()
//                }
//                .create()
//                .show()
        }
    }

    // Log |msg| and Toast about it.
    private fun logAndToast(msg: String) {
        Log.d(TAG, msg)
        if (logToast != null) {
            logToast!!.cancel()
        }
//       Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    }

    private fun reportError(description: String) {
        runOnUiThread {
            if (!isError) {
                isError = true
                disconnectWithErrorMessage(description)
            }
        }
    }

    // Create VideoCapturer
    private fun createVideoCapturer(): VideoCapturer? {
        val videoCapturer: VideoCapturer?
        Logging.d(TAG, "Creating capturer using camera2 API.")
        videoCapturer = createCameraCapturer(Camera2Enumerator(this))
        if (videoCapturer == null) {
            reportError(getString(R.string.msg_Report_error))
            return null
        }
        return videoCapturer
    }

    // Create VideoCapturer from camera
    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames



        Logging.d(TAG, "Looking for front facing cameras.")
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.")
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCallEnable) {
                    Log.e("##Call", " First Time $videoCallEnable")
                    setSpeakerOn()
                } else {
                    Log.e("##Call", " First Time else  $videoCallEnable")
                    setHeadsetOn()
                }
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.")
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.")
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

    private fun setSwappedFeeds(isSwappedFeeds: Boolean) {
        Logging.d(TAG, "setSwappedFeeds: $isSwappedFeeds")
        this.isSwappedFeeds = isSwappedFeeds
        localProxyVideoSink.setTarget(if (isSwappedFeeds) fullscreenRenderer else pipRenderer)
        remoteProxyRenderer.setTarget(if (isSwappedFeeds) pipRenderer else fullscreenRenderer)
        fullscreenRenderer!!.setMirror(isSwappedFeeds)
        pipRenderer!!.setMirror(!isSwappedFeeds)
        Log.d("##CallCOnected","On swapped feeds")
    }

    // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
    // All callbacks are invoked from websocket signaling looper thread and
    // are routed to UI thread.
    private fun onConnectedToRoomInternal(params: SignalingParameters) {
        val delta = System.currentTimeMillis() - callStartedTimeMs
        signalingParameters = params
        logAndToast("Creating peer connection, delay=" + delta + "ms")
        var videoCapturer: VideoCapturer? = null
        if (peerConnectionParameters!!.videoCallEnabled) {
            videoCapturer = createVideoCapturer()
        }
        peerConnectionClient!!.createPeerConnection(
            localProxyVideoSink, remoteRenderers, videoCapturer, signalingParameters
        )
        if (signalingParameters!!.initiator) {
            logAndToast("Creating OFFER...")
            // Create offer. Offer SDP will be sent to answering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient!!.createOffer()
        } else {
            if (params.offerSdp != null) {
                peerConnectionClient!!.setRemoteDescription(params.offerSdp)
                logAndToast("Creating ANSWER...")
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient!!.createAnswer()
            }
            if (params.iceCandidates != null) {
                // Add remote ICE candidates from room.
                for (iceCandidate in params.iceCandidates) {
                    peerConnectionClient!!.addRemoteIceCandidate(iceCandidate)
                }
            }
        }
    }

    override fun onConnectedToRoom(params: SignalingParameters) {
        runOnUiThread { onConnectedToRoomInternal(params) }
    }

    override fun onRemoteDescription(sdp: SessionDescription) {
        val delta = System.currentTimeMillis() - callStartedTimeMs
        runOnUiThread(Runnable {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received remote SDP for non-initilized peer connection.")
                return@Runnable
            }
            logAndToast("Received remote " + sdp.type + ", delay=" + delta + "ms")
            peerConnectionClient!!.setRemoteDescription(sdp)


            if (!signalingParameters!!.initiator) {
                logAndToast("Creating ANSWER...")
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient!!.createAnswer()
            }
        })
    }

    override fun onRemoteIceCandidate(candidate: IceCandidate) {
        runOnUiThread(Runnable {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received ICE candidate for a non-initialized peer connection.")
                return@Runnable
            }
            peerConnectionClient!!.addRemoteIceCandidate(candidate)
        })
    }

    override fun onRemoteIceCandidatesRemoved(candidates: Array<IceCandidate>) {
        runOnUiThread(Runnable {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received ICE candidate removals for a non-initialized peer connection.")
                return@Runnable
            }
            peerConnectionClient!!.removeRemoteIceCandidates(candidates)
        })
    }

    override fun onChannelClose() {
        runOnUiThread {
            logAndToast("Remote end hung up; dropping PeerConnection")
            disconnect()
        }
    }

    override fun onChannelError(description: String) {
        reportError(description)
    }

    // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
    // Send local peer connection SDP and ICE candidates to remote party.
    // All callbacks are invoked from peer connection client looper thread and
    // are routed to UI thread.
    override fun onLocalDescription(sdp: SessionDescription) {
        val delta = System.currentTimeMillis() - callStartedTimeMs
        runOnUiThread {
            if (appRtcClient != null) {
                logAndToast("Sending " + sdp.type + ", delay=" + delta + "ms")
                if (signalingParameters!!.initiator) {
                    appRtcClient!!.sendOfferSdp(sdp)
                } else {
                    appRtcClient!!.sendAnswerSdp(sdp)
                }
            }
            if (peerConnectionParameters!!.videoMaxBitrate > 0) {
                Log.d(
                    TAG,
                    "Set video maximum bitrate: " + peerConnectionParameters!!.videoMaxBitrate
                )
                peerConnectionClient!!.setVideoMaxBitrate(peerConnectionParameters!!.videoMaxBitrate)
            }
        }
    }

    override fun onIceCandidate(candidate: IceCandidate) {
        runOnUiThread {
            if (appRtcClient != null) {
                appRtcClient!!.sendLocalIceCandidate(candidate)
            }
        }
    }

    override fun onIceCandidatesRemoved(candidates: Array<IceCandidate>) {
        runOnUiThread {
            if (appRtcClient != null) {
                appRtcClient!!.sendLocalIceCandidateRemovals(candidates)
            }
        }
    }

    override fun onIceConnected() {
        val delta = System.currentTimeMillis() - callStartedTimeMs
        runOnUiThread {
            logAndToast("ICE connected, delay=" + delta + "ms")
            iceConnected = true
            callConnected()
        }
    }

    override fun onIceDisconnected() {
        runOnUiThread {
            logAndToast("ICE disconnected")
            iceConnected = false
            disconnect()
        }
    }

    override fun onPeerConnectionClosed() {}
    override fun onPeerConnectionStatsReady(reports: Array<StatsReport>) {}
    override fun onPeerConnectionError(description: String) {
        reportError(description)
    }

    public override fun onStop() {
        super.onStop()
        activityRunning = false
        if (peerConnectionClient != null) {
            peerConnectionClient!!.stopVideoSource()
        }
    }

    public override fun onStart() {
        super.onStart()
        activityRunning = true
        Log.d("##Chane", "onsart")
        // Video is not paused for screencapture. See onPause.
        if (peerConnectionClient != null) {
            peerConnectionClient!!.startVideoSource()
        }
    }

    override fun onDestroy() {
        Thread.setDefaultUncaughtExceptionHandler(null)
        disconnect()
        if (logToast != null) {
            logToast!!.cancel()
        }
        activityRunning = false
        Log.e("##CAll", "onDestroyed call ")
        onCallHangUp()
        super.onDestroy()
    }

    private class ProxyRenderer : VideoRenderer.Callbacks {
        private var target: VideoRenderer.Callbacks? = null

        @Synchronized
        override fun renderFrame(frame: I420Frame) {
            if (target == null) {
                Logging.d(TAG, "Dropping frame in proxy because target is null.")
                VideoRenderer.renderFrameDone(frame)
                return
            }
            target!!.renderFrame(frame)
        }

        @Synchronized
        fun setTarget(target: VideoRenderer.Callbacks?) {
            this.target = target
        }
    }

    private class ProxyVideoSink : VideoSink {
        private var target: VideoSink? = null

        @Synchronized
        override fun onFrame(frame: VideoFrame) {
            if (target == null) {
                Logging.d(TAG, "Dropping frame in proxy because target is null.")
                return
            }
            target!!.onFrame(frame)
        }

        @Synchronized
        fun setTarget(target: VideoSink?) {
            this.target = target
        }
    }

    companion object {
        private const val TAG = "CallActivity"
        private const val APPRTC_URL = "https://appr.tc"
        private const val UPPER_ALPHA_DIGITS = "ACEFGHJKLMNPQRUVWXY123456789"
        var name: String? = ""
        private const val STAT_CALLBACK_PERIOD = 1000
    }
}
