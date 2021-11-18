package com.pbt.cogni.activity.chat

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pbt.cogni.R
import com.pbt.cogni.activity.chat.adapter.ChatAdapter
import com.pbt.cogni.callback.PermissionCallBack
import com.pbt.cogni.databinding.ActivityChat2Binding
import com.pbt.cogni.model.Chat
import com.pbt.cogni.util.AppConstant.Companion.RECEIVER_ID
import com.pbt.cogni.util.AppConstant.Companion.RECEIVER_NAME
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import com.pbt.cogni.viewModel.ChatViewModel
import kotlinx.android.synthetic.main.activity_chat2.*
import kotlinx.android.synthetic.main.activity_test.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity : AppCompatActivity(), PermissionCallBack {

    companion object {
       public final var mProgressDialog: ProgressDialog? = null
        private const val TAG: String = "ChatActivity"
        var isChatVisible: Boolean = false
         var binding: ActivityChat2Binding? = null
        var progressbar:ProgressBar?=null


    }


    private var chatViewModel: ChatViewModel? = null
    private val CAMERA_REQUEST = 1888
    private val GELARY_REQUEST = 1088
    private val MY_CAMERA_PERMISSION_CODE = 1001

//    var mimeTypes = arrayOf(
//        "application/msword",
//        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",  // .doc & .docx
//        "application/vnd.ms-powerpoint",
//        "application/vnd.openxmlformats-officedocument.presentationml.presentation",  // .ppt & .pptx
//        "application/vnd.ms-excel",
//        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",  // .xls & .xlsx
//        "text/plain",
//        "application/pdf",
//        "application/zip"
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mProgressDialog =  ProgressDialog(this)
        mProgressDialog!!.setMessage("A message")
        mProgressDialog!!.setIndeterminate(true)
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        mProgressDialog!!.setCancelable(true)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat2)
        chatViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(ChatViewModel::class.java)
        binding?.chatViewModel = chatViewModel
        binding?.executePendingBindings()

        val reciverID: Int = intent.getIntExtra(RECEIVER_ID, 0)
        val reciverName: String = intent.getStringExtra(RECEIVER_NAME)
        val userID: Int = MyPreferencesHelper.getUser(this@ChatActivity)!!.id.toInt()

        binding?.chatViewModel?.initChat(this@ChatActivity, reciverID, userID, reciverName)
        binding?.chatViewModel?.mAdapter = ChatAdapter(this@ChatActivity, ArrayList<Chat>())
        binding?.listviewChat?.setAdapter(binding?.chatViewModel?.mAdapter)
        chatViewModel!!.permissionIsGranted = this
        isChatVisible = true

        binding!!.backArrow.setOnClickListener {
            finish()
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        )
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), MY_CAMERA_PERMISSION_CODE)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AppUtils.logDebug(TAG, " requestCode : " + requestCode)
        when (requestCode) {
            MY_CAMERA_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                }
                return
            }
        }
    }


    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            AppUtils.logDebug(TAG, "Camera Result ${result.resultCode}")
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {

                    val selectedImageUri: Uri? = result.data?.data

                    val data: Intent? = result.data
                    val imageBitmap = data?.extras?.get("data") as Bitmap


                    val root = Environment.getExternalStorageDirectory().toString()
                    val myDir = File("$root/req_images")

                    myDir.mkdirs()
                    val generator = Random()
                    var n = 10000
                    n = generator.nextInt(n)
                    val fname = "Image-$n.jpg"
                    val file = File(myDir, fname)
                    Log.i(TAG, "" + file)
                    if (file.exists()) file.delete()
                    try {
                        val out = FileOutputStream(file)
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        out.flush()
                        out.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val filePath = Uri.parse("$root/req_images/"+fname).toString()

                    chatViewModel!!.imageUri?.set(filePath)
                    AppUtils.logDebug(TAG,"$filePath")


//                val finalFile = File(getRealPathFromURIII(tempUri))
//
//                    AppUtils.logDebug(TAG,"galllary launcgher  uri ----"+tempUri+"\n"+finalFile)
                    binding?.rlImageSend?.visibility = View.VISIBLE
                    binding?.chatViewModel?.isVisiBled?.set(true)
                    binding?.imgPreview?.setImageBitmap(imageBitmap)

//                    chatViewModel!!.uploadImage()


                }
            }
            AppUtils.logDebug(TAG,"Outside the result ok dialog")
        }



    var gallaryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            AppUtils.logDebug(TAG, "Camera Result ${result.resultCode}")
            if (result.resultCode == Activity.RESULT_OK) {
                AppUtils.logDebug(TAG, "Result Is ok")
                if (result.data != null) {

                    val selectedImage: Uri? = result?.data!!.data
                  val file =  getRealPathFromURII(selectedImage)

                    Log.d("##test","URI--$selectedImage\n file--$file")


                }

            }

        }

    private fun getRealPathFromURII(contentUri: Uri?):String {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(contentUri!!, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor.close()
        return res!!

    }
    fun DownloadFile(file_url: String,context: Context,filename:String,image:ImageView,progressBar: ProgressBar) {
        progressbar=progressBar
        DownloadFileUrl().execute(file_url)
//        val request = DownloadManager.Request(Uri.parse(file_url))
//            .setTitle(filename)
//            .setDescription("Downloading")
//            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
//            .setAllowedOverMetered(true)
//            .setVisibleInDownloadsUi(false)
//        request.allowScanningByMediaScanner()
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"File")
//        val downloadManager:DownloadManager=context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
////        downloadManager.enqueue(request)
//        val  downloadId:Long = downloadManager.enqueue(request)
//

    }


    override fun isGranted(isGranted: Boolean) {
        AppUtils.logDebug(TAG, "Check Permission " + isGranted)
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                this@ChatActivity,
                arrayOf(Manifest.permission.CAMERA),
                MY_CAMERA_PERMISSION_CODE
            )
        } else {

            selectImage(this@ChatActivity)

        }
    }

    fun selectImage(context: Activity) {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
//        val options = arrayOf<CharSequence>("Take Photo")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose your profile picture")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                setResult(CAMERA_REQUEST, cameraIntent)
                resultLauncher.launch(cameraIntent)
            } else if (options[item] == "Choose from Gallery") {

                val pickPhoto = Intent()
                pickPhoto.setType("*/*")
                pickPhoto.setAction(Intent.ACTION_GET_CONTENT)
                setResult(GELARY_REQUEST, pickPhoto)
                gallaryLauncher.launch(pickPhoto)

            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        isChatVisible = true
    }

    override fun onPause() {
        super.onPause()
        isChatVisible = false
    }


class DownloadFileUrl : AsyncTask<String, String, String>() {
    override fun doInBackground(vararg fileUrl: String?): String? {

        try {
            var input: InputStream? = null
            var output: OutputStream? = null
            var connection: HttpURLConnection? = null
            try {
                val url = URL(fileUrl.get(0))
                connection = url.openConnection() as HttpURLConnection
                connection.connect()
                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() !== HttpURLConnection.HTTP_OK)
                    return "Server returned HTTP " + connection.getResponseCode()
                    .toString() + " " + connection.getResponseMessage()
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                val fileLength: Int = connection.getContentLength()
                // download the file
                input = connection.getInputStream()
                output = FileOutputStream("/sdcard/file_name.extension")
                val data = ByteArray(4096)
                var total: Long = 0
                var count: Int
                while (input.read(data).also { count = it } != -1) {
                    // allow canceling with back button
                    if (isCancelled) return null
                    total += count.toLong()
                    AppUtils.logDebug(TAG,"total"+total.toString())
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                      publishProgress(((total * 100 / fileLength).toString()))
                    output.write(data, 0, count)
                    AppUtils.logDebug(TAG,"filelength"+fileLength.toString())
                }
            } catch (e: java.lang.Exception) {
                return e.toString()
            } finally {
                try {
                    if (output != null) output.close()
                    input?.close()
                } catch (ignored: IOException) {
                    AppUtils.logError(TAG,"Exception:"+ignored.message)
                }
                if (connection != null) connection.disconnect()
            }
        } finally {

        }
return null
    }

    protected fun onProgressUpdate(vararg progress: Int?) {
        super.onProgressUpdate(progress.toString())

        mProgressDialog!!.isIndeterminate = false
        mProgressDialog!!.max = 100
        mProgressDialog!!.progress = progress[0]!!
    }




}

}