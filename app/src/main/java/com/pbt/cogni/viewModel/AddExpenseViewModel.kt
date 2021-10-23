package com.pbt.cogni.viewModel

import android.Manifest
import android.app.Activity
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
import com.pbt.cogni.util.AppUtils.Companion.paramRequestBody
import com.pbt.cogni.util.AppUtils.Companion.paramRequestBodyImage
import com.pbt.cogni.util.MyPreferencesHelper
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


class AddExpenseViewModel(val activity: Application) : AndroidViewModel(activity),
    Callback<HttpResponse> {

    private val context = activity
    private val CAMERA_REQUEST = 1888


    var listType: ObservableField<List<String>>? = null
    var description: ObservableField<String>? = null
    var ammount: ObservableField<String>? = null
    var expenseType: ObservableField<String>? = null
    var selectedImage: ObservableField<String>? = null
    var imageUri: ObservableField<Uri>? = null
    var routeId: ObservableField<String>? = null
    lateinit var activityContext : Activity
    var permissionIsGranted: PermissionCallBack? = null

    init {
        description = ObservableField("")
        ammount = ObservableField("")
        expenseType = ObservableField("")
        routeId = ObservableField("")
        imageUri = ObservableField()
        selectedImage = ObservableField(context.resources.getString(R.string.choose_image))
        listType = ObservableField<List<String>>()
    }

//    expenseType:sd
//    description:test
//    price:2
//    routeid:1
//    createdBy:fd
//    expenseTypeId:1

    fun addExpense(view: View) {
//
//        if(expenseType?.get()!!.equals(context.resources.getString(R.string.select_expense_type)))
//            Toasty.warning(context,context.resources.getString(R.string.please_select_expense_type),Toasty.LENGTH_SHORT).show()
//         else if(imageUri?.get()!! == null)
//            Toasty.warning(context,context.resources.getString(R.string.please_select_image))
//        else if(description?.get().toString().isNullOrEmpty())
////            Toasty.warning(context,context )
//        else if(ammount?.get().toString().isEmpty())
//            Toasty.warning(context,context.resources.getString(R.string.please_enter_ammount),Toasty.LENGTH_SHORT).show()

        ApiClient.client.create(ApiInterface::class.java).addExpense(
            paramRequestBody(routeId?.get()!!),//routeid
            paramRequestBody(ammount?.get()!!),//price
            paramRequestBody(description?.get()!!),//description
            paramRequestBody(expenseType?.get()!!),//expenseType
            paramRequestBody("10"),//expenseTypeId
            paramRequestBody(MyPreferencesHelper.getUser(context)?.UserName!!),//createdBy
            paramRequestBodyImage(imageUri?.get()!!, context)//image
        ).enqueue(this)
    }

    fun captureImage(view: View) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        )
            permissionIsGranted!!.isGranted(false)
        else
            permissionIsGranted!!.isGranted(true)
    }

    fun onSelectItem(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        expenseType?.set(parent?.getSelectedItem().toString())
    }

    fun getImageUri(context: Context, pic: Bitmap?): Uri {
        val bytes = ByteArrayOutputStream()
        pic?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(context.getContentResolver(), pic, "Title", null)
        var uri: Uri = Uri.parse(path)
        val fileName = "unknown"
        if (uri.getScheme().toString().compareTo("content") === 0) {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            if (cursor!!.moveToFirst()) {
                val column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) //Instead of "MediaStore.Images.Media.DATA" can be used "_data"
                var filePathUri = android.net.Uri.parse(cursor.getString(column_index))
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
        list.add(context.resources.getString(R.string.select_expense_type))
        list.add("Toll Text")
        list.add("Petrol")
        list.add("To eat")
        listType?.set(list)
    }

    override fun onResponse(call: Call<HttpResponse>, response: Response<HttpResponse>) {
        if (response?.body()?.code == false) {
            Toasty.success(context, context.resources.getString(R.string.expense_succsess_full), Toasty.LENGTH_SHORT).show()
            activityContext?.finish()
        }
    }

    override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
        Toasty.error(context, t.message.toString(), Toasty.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG: String = "AddExpenseViewMode"
    }


}