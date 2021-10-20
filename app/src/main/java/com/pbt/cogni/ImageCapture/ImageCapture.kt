package com.pbt.cogni.ImageCapture

import android.R.attr
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pbt.cogni.R
import io.grpc.Compressor
import kotlinx.android.synthetic.main.activity_image_capture.*
import java.io.ByteArrayOutputStream
import java.io.File
import android.R.attr.bitmap

import android.R.attr.path
import android.util.Log
import java.io.FileOutputStream
import java.io.InputStream
import android.R.attr.data





class ImageCapture : AppCompatActivity() {

    private val CAMERA_REQUEST = 1888
    private val MY_CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_capture)

        btn_captureimage.setOnClickListener{
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)




        if (requestCode === CAMERA_REQUEST && resultCode === RESULT_OK) {

            val imageBitmap = data?.extras?.get("data") as Bitmap


            img_caputredimage.setImageBitmap(imageBitmap)
//            val pic =data?.getParcelableExtra<Bitmap>("data")

            getImageUri(applicationContext, imageBitmap)

        }
    }



    private fun getImageUri(context: Context, pic: Bitmap?): Uri {

        val bytes = ByteArrayOutputStream()
        pic?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            Images.Media.insertImage(context.getContentResolver(), pic, "Title", null)
        return Uri.parse(path)
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }

            else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }


}