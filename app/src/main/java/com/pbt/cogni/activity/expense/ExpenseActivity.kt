package com.pbt.cogni.activity.expense

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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

            viewModel!!.getImageUri(applicationContext, imageBitmap)
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
    }

}