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
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.callback.PermissionCallBack
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.AppUtils.Companion.paramRequestBody
import com.pbt.cogni.util.AppUtils.Companion.paramRequestBodyImage
import com.squareup.okhttp.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Part
import java.io.ByteArrayOutputStream
import java.io.File


class AddExpenseViewModel(val activity: Application) : AndroidViewModel(activity),
    Callback<HttpResponse> {

    private val context = activity
    private val CAMERA_REQUEST = 1888

    var listType: ObservableField<List<String>>? = null
    var description: ObservableField<String>? = null
    var ammount: ObservableField<Int>? = null
    var expenseType: ObservableField<String>? = null
    var selectedImage: ObservableField<String>? = null
    var imageUri: ObservableField<Uri>? = null
    var permissionIsGranted: PermissionCallBack? = null

    init {
        description = ObservableField("")
        ammount = ObservableField(0)
        expenseType = ObservableField("")
        imageUri = ObservableField()
        selectedImage = ObservableField(context.resources.getString(R.string.choose_image))
        listType = ObservableField<List<String>>()
    }

    fun addExpense(view: View) {
        ApiClient.client.create(ApiInterface::class.java).addExpense(
            paramRequestBody("10"),
            paramRequestBody(ammount?.get()!!.toString()),
            paramRequestBody(description?.get()!!),
            paramRequestBody(expenseType?.get()!!),
            paramRequestBody("10"),
            paramRequestBodyImage(imageUri?.get()!!,context)
        ).enqueue(this)
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
        imageUri?.set(uri)
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

    override fun onResponse(call: Call<HttpResponse>, response: Response<HttpResponse>) {
        TODO("Not yet implemented")
    }

    override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
        TODO("Not yet implemented")
    }

}