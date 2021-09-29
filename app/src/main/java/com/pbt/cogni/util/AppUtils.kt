package com.pbt.cogni.util

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.pbt.cogni.BuildConfig
import java.util.regex.Pattern

class AppUtils {
    companion object {

        public fun isNetworkConnected(context : Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null
        }

        val  DEBUG : Boolean = BuildConfig.DEBUG
        fun isEmailValid(email: String): Boolean {
            val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }

        fun logError(tag : String ,message :String){
            if(DEBUG == true )
                Log.e("##"+tag,message)
        }

        fun logDebug(tag : String ,message :String){
            if(DEBUG == true )
                Log.d("##"+tag,message)
        }
        fun logWarning(tag : String ,message :String){
            if(DEBUG == true )
                Log.w("##"+tag,message)
        }
    }


}