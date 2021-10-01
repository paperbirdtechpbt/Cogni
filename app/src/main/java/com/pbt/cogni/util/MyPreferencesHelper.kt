package com.pbt.cogni.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.pbt.cogni.activity.login.User
import com.pbt.cogni.activity.login.UserDetailsData
import com.pbt.cogni.util.AppConstant.PREF_USER

class MyPreferencesHelper {


    companion object {

        var preferences: SharedPreferences? = null

        fun openPref(context: Context) {
           MyPreferencesHelper.preferences =
                context.getSharedPreferences(AppConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        }

        fun setStringValue(context: Context, key: String?, value: String?) {
           MyPreferencesHelper.openPref(context)
            val editor: SharedPreferences.Editor =
               MyPreferencesHelper.preferences!!.edit()
            editor.putString(key, value)
            editor.commit()
           MyPreferencesHelper.preferences = null
        }

        fun setIntValue(context: Context, key: String?, value: Int) {
           MyPreferencesHelper.openPref(context)
            val editor: SharedPreferences.Editor =
               MyPreferencesHelper.preferences!!.edit()
            editor.putInt(key, value)
            editor.commit()
           MyPreferencesHelper.preferences = null
        }

        fun getStringValue(context: Context, key: String?, defvalue: String?): String? {
           MyPreferencesHelper.openPref(context)
            val result: String? =
               MyPreferencesHelper.preferences?.getString(key, defvalue)
           MyPreferencesHelper.preferences = null
            return result
        }

        fun getintValue(context: Context, key: String?, defvalue: Int): Int? {
           MyPreferencesHelper.openPref(context)
            val result: Int? = MyPreferencesHelper.preferences?.getInt(key, defvalue)
           MyPreferencesHelper.preferences = null
            return result
        }

        fun clearPref(context: Context) {
           MyPreferencesHelper.openPref(context)
            val editor: SharedPreferences.Editor? =
               MyPreferencesHelper.preferences?.edit()
            editor?.clear()
            editor?.commit()
        }

        fun getUser(context: Context): UserDetailsData? {
            MyPreferencesHelper.openPref(context)
            val user: UserDetailsData? = Gson().fromJson(MyPreferencesHelper.preferences?.getString(PREF_USER, ""),UserDetailsData::class.java)
            MyPreferencesHelper.preferences = null
            return user
        }
    }

}