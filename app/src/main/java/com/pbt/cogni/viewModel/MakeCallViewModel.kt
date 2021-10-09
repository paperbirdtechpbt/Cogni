package com.pbt.cogni.viewModel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.pbt.cogni.activity.call.CallActivity
import com.pbt.cogni.util.AppConstant.Companion.CALL

class MakeCallViewModel(application: Application): AndroidViewModel(application) {

    val context = application

    fun callUI(){
        val intent = Intent(context, CallActivity::class.java)
        intent.putExtra(CALL, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
    fun videoCallUI(){

        val intent = Intent(context, CallActivity::class.java)
        intent.putExtra(CALL, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}