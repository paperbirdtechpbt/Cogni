package com.pbt.cogni.activity.chat

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.opengl.Visibility
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
//                    chatViewModel!!.getImageUri(
//                        applicationContext,
//                        bitmap.getExtras()?.get("data") as Bitmap
//                    )
                    binding?.rlImageSend?.visibility = View.VISIBLE
                    binding?.chatViewModel?.isVisiBled?.set(true)
                    binding?.imgPreview?.setImageBitmap(bitmap.getExtras()?.get("data") as Bitmap)
                }
            }
        }
    var gallaryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            AppUtils.logDebug(TAG, "Camera Result ${result.resultCode}")
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    val selectedImage: Uri? = result.data!!.data
                    chatViewModel!!.selectedImage?.set(selectedImage!!.toString())
                    chatViewModel!!.imageUri?.set(selectedImage!!)
                }
            }
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
                pickPhoto.setType("application/*");
                pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
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