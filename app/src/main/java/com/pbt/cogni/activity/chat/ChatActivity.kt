package com.pbt.cogni.activity.chat

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
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
import java.io.ByteArrayOutputStream
import java.io.File


class ChatActivity : AppCompatActivity(), PermissionCallBack {

    companion object {
        private const val TAG: String = "ChatActivity"
        var isChatVisible: Boolean = false
    }

    private var binding: ActivityChat2Binding? = null
    private var chatViewModel: ChatViewModel? = null
    private val CAMERA_REQUEST = 1888
    private val GELARY_REQUEST = 1088
    private val MY_CAMERA_PERMISSION_CODE = 1001

    var mimeTypes = arrayOf(
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",  // .doc & .docx
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",  // .ppt & .pptx
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",  // .xls & .xlsx
        "text/plain",
        "application/pdf",
        "application/zip"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                MY_CAMERA_PERMISSION_CODE
            )


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
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

                    var bitmap: Intent = result.data!!
                    AppUtils.logDebug(TAG,"result  launcgher----"+result.data.toString())
//                    chatViewModel!!.getImageUri(
//                        applicationContext,
//                        bitmap.getExtras()?.get("data") as Bitmap
//                    )
                    val photo :Bitmap= result.data?.getExtras()?.get("data")as Bitmap
                val tempUri: Uri = getImageUri(applicationContext, photo)

                    chatViewModel!!.imageUri?.set(tempUri)


//                val finalFile = File(getRealPathFromURIII(tempUri))
//
//                    AppUtils.logDebug(TAG,"galllary launcgher  uri ----"+tempUri+"\n"+finalFile)
//                    binding?.rlImageSend?.visibility = View.VISIBLE
//                    binding?.chatViewModel?.isVisiBled?.set(true)
//                    binding?.imgPreview?.setImageBitmap(bitmap.getExtras()?.get("data") as Bitmap)
                        chatViewModel!!.uploadImage()



                }
            }
        }

    private fun getImageUri(applicationContext: Context?, photo: Bitmap): Uri {

        val bytes = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(applicationContext?.getContentResolver(), photo, "Title", null)
        return Uri.parse(path)

    }

    var gallaryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            AppUtils.logDebug(TAG, "Camera Result ${result.resultCode}")
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {

                    val selectedImage: Uri? = result.data!!.data
                    AppUtils.logDebug(TAG,"galllary launcgher  uri ----"+selectedImage)

                    chatViewModel!!.selectedImage?.set(selectedImage!!.toString())
                    chatViewModel!!.imageUri?.set(selectedImage!!)
//                    var file  =  File(AppUtils.getRealPathFromURI(selectedImage!!,this))
                //                    chatViewModel!!.uploadImage(file)

                 //   <-------------Ravi------------>
                    val Image: Uri? = result!!.data?.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = contentResolver.query(
                Image!!,
                filePathColumn, null, null, null
            )
            cursor?.moveToFirst()
            val columnIndex: Int = cursor!!.getColumnIndex(filePathColumn[0])
            val picturePath: String = cursor.getString(columnIndex)
            cursor.close()


            var imageFile =  File(getRealPathFromuri(Image))
                    AppUtils.logDebug(TAG,"uri-----$selectedImage \n file-----$imageFile")


                }
            }
        }

    private fun getRealPathFromURIII(uri: Uri?) :String?{
        var path = ""
        if (contentResolver != null) {
            val cursor = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
       return  path
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

        private fun getRealPathFromuri(contentURI: Uri): String? {
        val result: String?
        val cursor = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
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

}