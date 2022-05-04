package com.pbt.cogni.activity.call

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pbt.cogni.R
import com.pbt.cogni.fcm.MyFirebaseMessagingService
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetOngoingConferenceService
import org.jitsi.meet.sdk.JitsiMeetView
import java.net.URL

class JitsiCall : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jitsi_call)
        val view = JitsiMeetView(this)
        val url: URL = URL("https://meet.jit.si/Room_36_1")

        val options: JitsiMeetConferenceOptions = JitsiMeetConferenceOptions.Builder()
//            .setServerURL(URL("https://meet.jit.si/ActualFaultsDoWildly"))
            .setRoom("https://meet.jit.si/${MyFirebaseMessagingService.roomId}")
//            .setRoom("test123")
            .setAudioMuted(false)
            .setVideoMuted(false)
            .setAudioOnly(false)
//            .setWelcomePageEnabled(false)
            .setConfigOverride("requireDisplayName", false)
            .setFeatureFlag("resolution",true)
            .build()

        view.join(options)
        setContentView(view)
    }
}