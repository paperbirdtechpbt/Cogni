package com.pbt.cogni.activity.chat.upload
import okhttp3.RequestBody
import android.os.Handler
import android.os.Looper
import com.pbt.cogni.util.AppUtils
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class ProgressRequestBody(file: File, content_type: String?, listener: UploadCallbacks) :
    RequestBody() {

    private var mFile: File? = file
    private var content_type: String? = content_type
    private val mPath: String? = null
    public var mListener: UploadCallbacks? = null

    override fun contentType(): okhttp3.MediaType? {
        return (content_type+"/*").toMediaTypeOrNull();
    }

//    fun ProgressRequestBody(file: File, content_type: String?, listener: UploadCallbacks) {
//        this.content_type = content_type
//        mFile = file
//        mListener = listener
//    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {

        val fileLength = mFile!!.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val `in` = FileInputStream(mFile)
        var uploaded: Long = 0

        try {
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (`in`.read(buffer).also { read = it } != -1) {

                // update progress on UI thread
                handler.post(ProgressUpdater(uploaded, fileLength))
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
            }
        } finally {
            `in`.close()
        }
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mFile!!.length()
    }

    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int)
        fun onError()
        fun onFinish()
    }

    private class ProgressUpdater(private val mUploaded: Long, private val mTotal: Long) :
        Runnable {
        override fun run() {
            AppUtils.logDebug("ProgressREquestBody","percent ==>> "+(100 * mUploaded / mTotal).toInt())
            mListener?.onProgressUpdate((100 * mUploaded / mTotal).toInt())
        }
    }

    companion object {
        private var mListener: UploadCallbacks? = null
        private const val DEFAULT_BUFFER_SIZE = 2048
    }
}