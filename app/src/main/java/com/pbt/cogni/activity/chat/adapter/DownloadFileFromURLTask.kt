package com.pbt.cogni.activity.chat.adapter

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.pbt.cogni.R
import com.pbt.cogni.activity.chat.ChatActivity
import java.io.*
import java.net.URL
import java.security.AccessController.getContext


class DownloadFileFromURLTask() : AsyncTask<String?, String?, String?>() {




    /**
     * Downloading file in background thread
     */
    @SuppressLint("SdCardPath")
    override fun doInBackground(vararg f_url: String?): String? {
        Log.d("##ChatAdapter", "do in background")
        var count: Int
        try {
            val url = URL(f_url[0])
            val conection = url.openConnection()
            conection.connect()
            // getting file length
            val lenghtOfFile = conection.contentLength

            // input stream to read file - with 8k buffer
            val input: InputStream = BufferedInputStream(
                url.openStream(),
                8192
            )
            val myObj =
                File(Environment.getExternalStorageDirectory().toString() + "/" + "filename.txt")
            // Output stream to write file
            val output: OutputStream = FileOutputStream(myObj.toString())
            val data = ByteArray(1024)
            var total: Long = 0
            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (total * 100 / lenghtOfFile).toInt())

                // writing data to file
                output.write(data, 0, count)
            }

            // flushing output
            output.flush()

            // closing streams
            output.close()
            input.close()
        } catch (e: Exception) {
            Log.e("Error: ", e.message)
        }
        return null
    }

    /**
     * Updating progress bar
     */
    override fun onProgressUpdate(vararg progress: String?) {
        // setting progress percentage
        Log.d("##DownloadFile", progress.toString())
//        progresss?.setProgress(Integer.parseInt(progress[0]!!))

      ChatActivity.progressbar?.setProgress(Integer.parseInt(progress[0]!!))
        Log.d("##DownloadFile", ChatActivity.progressbar?.progress.toString())

    

    }

    /**
     * After completing background task Dismiss the progress dialog
     */
    override fun onPostExecute(file_url: String?) {
        Log.i("##ChatAdapter", "on post excute!")
    }


}