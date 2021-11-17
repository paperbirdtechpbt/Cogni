package com.pbt.cogni.activity.expense

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pbt.cogni.R
import com.pbt.cogni.activity.MapsActivity
import com.pbt.cogni.activity.chat.ChatActivity
import com.pbt.cogni.callback.PermissionCallBack
import com.pbt.cogni.databinding.ActivityExpenseBinding
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.viewModel.AddExpenseViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.*


class ExpenseActivity : AppCompatActivity(),PermissionCallBack {

    private var viewModel: AddExpenseViewModel? = null
    private var binding: ActivityExpenseBinding? = null
    private val CAMERA_REQUEST = 1888
    private val MY_CAMERA_PERMISSION_CODE = 1001
    private  var routeId : String = ""
    private  var assignId : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);


        routeId = intent.getStringExtra(AppConstant.CONST_ROUTE_ID)
        assignId=intent.getStringExtra(AppConstant.CONST_ASSIGN_ID)
        AppUtils.logDebug(TAG,"RoutID : "+routeId)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_expense)
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(AddExpenseViewModel::class.java)
        binding?.addExpenseViewModel = viewModel
        viewModel!!.permissionIsGranted = this
        viewModel!!.routeId?.set(routeId)
        viewModel!!.assignID?.set(assignId)
        viewModel!!.activityContext = this@ExpenseActivity
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
        AppUtils.logDebug(TAG, "Camera Result ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {


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

            AppUtils.logDebug(TAG,"$filePath")

            viewModel!!.selectedImage?.set(fname)
            viewModel!!.imageUri?.set(filePath)
            }
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
        var activity : Activity ? = null

    }

}