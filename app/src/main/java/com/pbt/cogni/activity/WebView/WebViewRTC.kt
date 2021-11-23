package com.pbt.cogni.activity.WebView

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.pbt.cogni.R
import kotlinx.android.synthetic.main.activity_web_view_rtc.*


class WebViewRTC : AppCompatActivity(),View.OnClickListener {
    private var www: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view_rtc)
        www=findViewById(R.id.webView)
        www!!.getSettings().setMediaPlaybackRequiresUserGesture(false);

        www!!.webViewClient = MyBrowser()
        www!!.setWebChromeClient(object : WebChromeClient() {
            // Grant permissions for cam
            override fun onPermissionRequest(request: PermissionRequest) {
                Log.d("TAG", "onPermissionRequest")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources())
                }
            } })

        wenviewbutton.setOnClickListener(this)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onClick(v: View?) {
        wenviewbutton.visibility=View.GONE
        val url ="https://webrtc-demo-6a14b.web.app/"

        www?.getSettings()?.setLoadsImagesAutomatically(true)
        www?.getSettings()?.setJavaScriptEnabled(true)
        www?.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
        www?.loadUrl(url)
    }
    private class MyBrowser : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

}