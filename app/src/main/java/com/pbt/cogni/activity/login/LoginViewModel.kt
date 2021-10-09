package com.pbt.cogni.activity.login

import android.app.Application
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.SplashActivity
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.util.AppUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


public class LoginViewModel(val activity: Application) : AndroidViewModel(activity),
    Callback<HttpResponse> {

    val context = activity

    var email: ObservableField<String>? = null
    var emailError: ObservableField<String>? = null
    var password: ObservableField<String>? = null
    var btnSelected: ObservableBoolean? = null
    var isPasswordShow: ObservableBoolean? = null
    var userLogin: MutableLiveData<HttpResponse>? = null

    var loginListener: LoginListener? = null

    init {
        btnSelected = ObservableBoolean(false)
        isPasswordShow = ObservableBoolean(false)
        email = ObservableField("")
        emailError = ObservableField("")
        password = ObservableField("")
        userLogin = MutableLiveData<HttpResponse>()
    }


    fun onEmailChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
        btnSelected?.set(AppUtils.isEmailValid(s.toString()) && password?.get()!!.length >= 8)
    }

    fun onPasswordChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
        btnSelected?.set(AppUtils.isEmailValid(email?.get()!!) && s.toString().length >= 5)
    }

    override fun onResponse(
        call: Call<HttpResponse>?,
        response: Response<HttpResponse>?
    ) {
        progressbar()
        userLogin?.value = response?.body()



        Log.e("##Respose", " Response : " + Gson().toJson(response?.body()))
    }

    fun progressbar() {


        var progressBar = ProgressBar(this.activity)

        val handler = Handler()



        progressBar = progressBar


        var i = 0
        Thread(Runnable {

            while (i < 200) {
                i += 1
                handler.post(Runnable {
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = i
                })
                try {
                    Thread.sleep(2000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            progressBar.visibility = View.INVISIBLE

        }).start()
    }

    fun login(view : View) {

//        progressbar()
        val imm = context.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0)


//        progressDialog?.value = true
        ApiClient.client.create(ApiInterface::class.java).login(
            email = email?.get()!!, password = password?.get()!!
        ).enqueue(this)

    }


    fun showHidePassword(view: View) {
        if (isPasswordShow?.get() == false) isPasswordShow?.set(true) else isPasswordShow?.set(false)

        isPasswordShow?.get()?.let { loginListener!!.showPassword(it) }
    }

    override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
        Log.e("##Respose", " Response : " + t?.message)
    }


    fun onDoneClicked(view: View, actionId: Int, event: KeyEvent?): Boolean {


        if (actionId == EditorInfo.IME_ACTION_DONE) {
            val imm = activity.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)

            login(view)
            return true
        }
        return false
    }


}

