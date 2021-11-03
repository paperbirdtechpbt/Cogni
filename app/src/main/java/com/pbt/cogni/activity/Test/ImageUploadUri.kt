package com.pbt.cogni.activity.Test


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pbt.cogni.R
import kotlinx.android.synthetic.main.activity_image_upload_uri.*
import android.content.Intent
import android.provider.MediaStore
import android.net.Uri
import android.util.Log
import java.io.File
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.pbt.cogni.BuildConfig
import android.provider.OpenableColumns
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.ImageView


class ImageUploadUri : AppCompatActivity() {

    private val REQUEST_CAMERA_IMAGE = 10

    val APP_TAG = "##ImageUploadUri"
    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val GET_DOCUMENT=1100
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_upload_uri)

//        imageuri.setOnClickListener{
//            val i = Intent(
//                Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
////780-=+
////            val i = Intent("android.media.action.IMAGE_CAPTURE")
//
//            startActivityForResult(i, RESULT_LOAD_IMAGE)
//
//        }
//hgh
//        imageuri.setOnClickListener{
//
//            val m_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(m_intent, REQUEST_CAMERA_IMAGE)
//        }
        imageuri.setOnClickListener{
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                onLaunchCamera()
            }
            else{
//                val i = Intent(
//                Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
///
            val i = Intent("android.media.action.IMAGE_CAPTURE")

            startActivityForResult(i, REQUEST_CAMERA_IMAGE)
            }

        }

    }

    private fun onLaunchCamera() {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val intent = Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT)
        val i = intent.setType("*/*").toString()

        if (i == "*/*") {
             startActivityForResult(intent,GET_DOCUMENT)
        }
        else{


            photoFile = getPhotoFileUri(photoFileName)
        Log.d(APP_TAG, photoFileName)
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    photoFile!!
                )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            Log.d(APP_TAG, fileProvider.toString())


            if (intent.resolveActivity(packageManager) != null) {

                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }
    }

    private fun getPhotoFileUri(fileName: String): File{
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory")
        }

        return File(mediaStorageDir.path + File.separator + fileName)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA_IMAGE) {
            Log.d(APP_TAG,"in capurture image rewuest")
            if (resultCode == RESULT_OK) {

                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                val ivPreview: ImageView = findViewById(R.id.imageviewimage)
                ivPreview.setImageBitmap(takenImage)
            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
        else{

            Log.d(APP_TAG,"else")
            if (resultCode === RESULT_OK) {
                // Get the Uri of the selected file
                val uri: Uri = data?.getData()!!
                val uriString = uri.toString()
                val myFile = File(uriString)
                val path = myFile.absolutePath
                var displayName: String? = null
                if (uriString.startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor =
                            this.getContentResolver().query(uri, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                            Log.d(APP_TAG,"myfile"+myFile.toString()+"\n"+"Uri"+uriString)
                            Toast.makeText(this,displayName,Toast.LENGTH_LONG).show()
                        }
                    } finally {
                        cursor?.close()
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.name
                    Log.d(APP_TAG,"myfile"+myFile.toString()+"\n"+"Uri"+uriString)
                    Toast.makeText(this,displayName,Toast.LENGTH_LONG).show()

                }
            }
        }
    }
//
//    private fun openDialog() {
//        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(
//            this
//        )
//        myAlertDialog.setTitle("Upload Pictures Option")
//        myAlertDialog.setMessage("How do you want to set your picture?")
//
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (requestCode) {
//            REQUEST_CAMERA_IMAGE -> if (resultCode == RESULT_OK) {
//                val photo :Bitmap= data?.getExtras()?.get("data")as Bitmap
//
//                imageviewimage.setImageBitmap(photo)
//                val tempUri: Uri = getImageUri(applicationContext, photo)
//
//                val finalFile = File(getRealPathFromURIII(tempUri))
//                Toast.makeText(this,tempUri.toString()+"\n"+finalFile.toString(),Toast.LENGTH_LONG).show()
//
//                Log.d("####pathandFILE",tempUri.toString()+"\n"+finalFile.toString())
//            }
//        }
//    }


//    fun getRealPathFromURIII(uri: Uri?): String {
//        var path = ""
//        if (contentResolver != null) {
//            val cursor = contentResolver.query(uri!!, null, null, null, null)
//            if (cursor != null) {
//                cursor.moveToFirst()
//                val idx = cursor.getColumnIndex(Images.ImageColumns.DATA)
//                path = cursor.getString(idx)
//                cursor.close()
//            }
//        }
//        return path
//    }
//
//    fun getImageUri(inContext: Context, inImage: Bitmap): Uri{
//        val bytes = ByteArrayOutputStream()
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//        val path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null)
//        return Uri.parse(path)
//    }

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