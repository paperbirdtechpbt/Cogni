 package com.pbt.cogni.util

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.loader.content.CursorLoader
import com.pbt.cogni.BuildConfig
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern



 class AppUtils {

    companion object {


        val DEBUG: Boolean = BuildConfig.DEBUG

        public fun isNetworkConnected(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null
        }

        fun isEmailValid(email: String): Boolean {
            val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }

        fun logError(tag: String, message: String) {
            if (DEBUG == true)
                Log.e("##" + tag, message)
        }

        fun logDebug(tag: String, message: String) {
            if (DEBUG == true)
                Log.d("##" + tag, message)
        }

        fun logWarning(tag: String, message: String) {
            if (DEBUG == true)
                Log.w("##" + tag, message)
        }

        fun isAppIsInBackground(context: Context): Boolean {
            var isInBackground = true
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                val runningProcesses = am.runningAppProcesses
                for (processInfo in runningProcesses) {
                    if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (activeProcess in processInfo.pkgList) {
                            if (activeProcess == context.packageName) {
                                isInBackground = false
                            }
                        }
                    }
                }
            } else {
                val taskInfo = am.getRunningTasks(1)
                val componentInfo = taskInfo[0].topActivity
                if (componentInfo!!.packageName == context.packageName) {
                    isInBackground = false
                }
            }
            return isInBackground
        }

        fun getDisplayableTime(value: Long): String? {
            val difference: Long
            val mDate = System.currentTimeMillis()
            if (mDate > value) {
                difference = mDate - value
                val seconds = difference / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                val months = days / 31
                val years = days / 365
                return if (seconds < 86400) {
                    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    formatter.format(Date(value))
                    //return "not yet";
                } else if (seconds < 172800) // 48 * 60 * 60
                    "yesterday" else if (seconds < 2592000) // 30 * 24 * 60 * 60
                    "$days days ago" else if (seconds < 31104000) // 12 * 30 * 24 * 60 * 60
                    if (months <= 1) "one month ago" else "$days months ago" else if (years <= 1) "one year ago" else "$years years ago"
            }
            return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date().getTime())
        }

        fun getRandomString(length: Int): String {
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            return (1..length)
                .map { allowedChars.random() }
                .joinToString("")
        }

        fun paramRequestBody(param: String): RequestBody {
            val parameter: RequestBody =
                param.toRequestBody("multipart/form-data".toMediaTypeOrNull());
            return parameter
        }

        fun paramRequestBodyImage(param: String, context: Context): MultipartBody.Part {

            val file = File(param)
            val requestFile: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("file", file.name, requestFile)
            return body
        }

        fun getRealPathFromURI(contentUri: Uri, context: Context): String? {
//            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val loader = CursorLoader(context, contentUri, proj, null, null, null)
            val cursor: Cursor? = loader.loadInBackground()
            val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val result: String = cursor!!.getString(column_index)
            cursor.close()
            return result
        }

        fun checkIsimage(extenstion: String): String? {
            if (extenstion.equals("jpg") || extenstion.equals("jpeg") || extenstion.equals("png") || extenstion.equals(
                    "webp"
                )
            )
                return "image"
            else if (extenstion == "text")
                return "text"
            else
                return "doc"
        }


        fun getPhotoFileUri(fileName: String, context: Context): File {
            val mediaStorageDir =
                File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ImageCapture")

            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            }
            return File(mediaStorageDir.path + File.separator + fileName)
        }






    }

}