package com.pbt.cogni.activity.Test

import android.R.attr
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pbt.cogni.R
import kotlinx.android.synthetic.main.activity_image_upload_uri.*
import androidx.core.app.ActivityCompat.startActivityForResult

import android.content.Intent
import android.provider.MediaStore
import android.graphics.BitmapFactory

import android.database.Cursor
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.pbt.cogni.util.AppUtils.Companion.getRealPathFromURI
import java.io.File
import androidx.core.app.ActivityCompat.startActivityForResult

import androidx.core.content.FileProvider

import android.os.Environment
import android.R.attr.data

import android.graphics.Bitmap
import android.R.attr.data
import android.R.attr.data
import android.app.AlertDialog
import android.content.Context
import android.provider.MediaStore.Images
import java.io.ByteArrayOutputStream


class ImageUploadUri : AppCompatActivity() {

    private val REQUEST_CAMERA_IMAGE = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_upload_uri)

//        imageuri.setOnClickListener{
//            val i = Intent(
//                Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
////
////            val i = Intent("android.media.action.IMAGE_CAPTURE")
//
//            startActivityForResult(i, RESULT_LOAD_IMAGE)
//
//        }

        imageuri.setOnClickListener{
            openDialog()
            val m_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(m_intent, REQUEST_CAMERA_IMAGE)
        }

    }

    private fun openDialog() {
        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(
            this
        )
        myAlertDialog.setTitle("Upload Pictures Option")
        myAlertDialog.setMessage("How do you want to set your picture?")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CAMERA_IMAGE -> if (resultCode == RESULT_OK) {
                val photo :Bitmap= data?.getExtras()?.get("data")as Bitmap

                imageviewimage.setImageBitmap(photo)
                val tempUri: Uri = getImageUri(applicationContext, photo)

                val finalFile = File(getRealPathFromURIII(tempUri))

                Log.d("####pathandFILE",tempUri.toString()+"\n"+finalFile.toString())
            }
        }
    }


    fun getRealPathFromURIII(uri: Uri?): String? {
        var path = ""
        if (contentResolver != null) {
            val cursor = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null)
        return Uri.parse(path)
    }

    //    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
//            val selectedImage: Uri? = data.data
//            Log.d("###uri",selectedImage.toString())
//            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
//            val cursor: Cursor? = contentResolver.query(
//                selectedImage!!,
//                filePathColumn, null, null, null
//            )
//            cursor?.moveToFirst()
//            val columnIndex: Int = cursor!!.getColumnIndex(filePathColumn[0])
//            val picturePath: String = cursor.getString(columnIndex)
//            cursor?.close()
//            val imageView: ImageView = findViewById(R.id.imageviewimage) as ImageView
//            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath))
//
//            var imageFile:File =  File(getRealPathFromuri(selectedImage))
//            Log.d("###uri",imageFile.toString())
//        }
//    }
//    private fun getRealPathFromuri(contentURI: Uri): String? {
//        val result: String?
//        val cursor = contentResolver.query(contentURI, null, null, null, null)
//        if (cursor == null) {
//            result = contentURI.path
//        } else {
//            cursor.moveToFirst()
//            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
//            result = cursor.getString(idx)
//            cursor.close()
//        }
//        return result
//    }
}