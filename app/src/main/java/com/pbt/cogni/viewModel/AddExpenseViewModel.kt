package com.pbt.cogni.viewModel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.pbt.cogni.R
import com.pbt.cogni.callback.PermissionCallBack
import com.pbt.cogni.util.AppUtils
import kotlinx.android.synthetic.main.activity_image_capture.*
import java.io.ByteArrayOutputStream


class AddExpenseViewModel(val activity: Application) : AndroidViewModel(activity) {

    private val context = activity
    private val CAMERA_REQUEST = 1888

    var listType: ObservableField<List<String>>? = null
    var description: ObservableField<String>? = null
    var ammount: ObservableField<String>? = null
    var expenseType: ObservableField<String>? = null
    var selectedImage: ObservableField<String>? = null
    var permissionIsGranted: PermissionCallBack? = null

    init {
        description = ObservableField("")
        ammount = ObservableField("")
        expenseType = ObservableField("")
        selectedImage = ObservableField(context.resources.getString(R.string.choose_image))
        listType = ObservableField<List<String>>()
    }

    fun addExpense(view: View) {

    }

    fun captureImage(view: View) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            permissionIsGranted!!.isGranted(false)
         else
            permissionIsGranted!!.isGranted(true)
    }

    fun onSelectItem(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        AppUtils.logDebug(TAG, "onSelect Item" + parent?.getSelectedItem())
    }

    fun getImageUri(context: Context, pic: Bitmap?): Uri {
        val bytes = ByteArrayOutputStream()
        pic?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(context.getContentResolver(), pic, "Title", null)
       var uri : Uri = Uri.parse(path)
        val fileName = "unknown"
        if (uri.getScheme().toString().compareTo("content") === 0) {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            if (cursor!!.moveToFirst()) {
                val column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) //Instead of "MediaStore.Images.Media.DATA" can be used "_data"
              var  filePathUri = android.net.Uri.parse(cursor.getString(column_index))
                selectedImage?.set(filePathUri.getLastPathSegment().toString())
            }

        } else if (uri.getScheme()?.compareTo("file") === 0) {
            selectedImage?.set(uri.getLastPathSegment().toString())
        } else {
            selectedImage?.set(fileName.toString() + "_" + uri.getLastPathSegment())
        }

        return uri;
    }


    fun bindAdapter() {
        var list = ArrayList<String>()
        list.add("Select Type")
        list.add("Toll Text")
        list.add("Petrol")
        list.add("To eat")
        listType?.set(list)
    }


    companion object {
        const val TAG: String = "AddExpenseViewMode"
    }

}