package com.pbt.cogni.activity.expense

import android.Manifest
import android.R.attr
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pbt.cogni.R
import com.pbt.cogni.callback.PermissionCallBack
import com.pbt.cogni.databinding.ActivityExpenseBinding
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.viewModel.AddExpenseViewModel
import kotlinx.android.synthetic.main.activity_image_capture.*
import okhttp3.MediaType

import okhttp3.RequestBody
import android.widget.Toast
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.pbt.cogni.model.HttpResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import android.R.attr.data
import android.database.Cursor
import android.util.Log
import android.R.attr.data





class ExpenseActivity : AppCompatActivity(),PermissionCallBack {

    private var viewModel: AddExpenseViewModel? = null
    private var binding: ActivityExpenseBinding? = null
    private val CAMERA_REQUEST = 1888
    private val MY_CAMERA_PERMISSION_CODE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_expense)
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(AddExpenseViewModel::class.java)

        binding?.addExpenseViewModel = viewModel

        viewModel!!.permissionIsGranted = this

        viewModel!!.bindAdapter()

       if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED )
           requestPermissions(this, arrayOf(Manifest.permission.CAMERA), MY_CAMERA_PERMISSION_CODE)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AppUtils.logDebug(TAG," requestCode : "+requestCode)
        when (requestCode) {
            MY_CAMERA_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    requestUploadSurvey()
                }
                return
            }
        }
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        AppUtils.logDebug(TAG,"Camera Result ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as Bitmap
//            img_caputredimage.setImageBitmap(imageBitmap)
//            val pic =data?.getParcelableExtra<Bitmap>("data")
            val selectedImageUri: Uri = data.getData() as Uri
//            val selectedImageUri: Uri = data.extras?.get("data") as Uri
            var  path:String = getPathFromURI(selectedImageUri)
            Log.d("###imagepather",path)

            viewModel!!.getImageUri(applicationContext, imageBitmap)
        }
    }

    private fun getPathFromURI(contentUri: Uri?): String {
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

    override fun isGranted(isGranted: Boolean) {
        AppUtils.logDebug(TAG,"Check Permission "+isGranted)
        if(!isGranted){
            requestPermissions(this@ExpenseActivity, arrayOf(Manifest.permission.CAMERA), MY_CAMERA_PERMISSION_CODE)
        }else{
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            setResult(CAMERA_REQUEST,cameraIntent)
            resultLauncher.launch(cameraIntent)
        }
    }

    companion object{
        private var TAG : String  = "ExpenseActivity"
    }

}