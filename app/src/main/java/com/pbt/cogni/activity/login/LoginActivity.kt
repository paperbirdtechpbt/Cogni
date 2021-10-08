package com.pbt.cogni.activity.login

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.pbt.cogni.R
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.SplashActivity
import com.pbt.cogni.activity.home.MainActivity

import com.pbt.cogni.databinding.ActivityLoginBinding
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppConstant.PREF_IS_LOGIN
import com.pbt.cogni.util.AppConstant.PREF_USER
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Response


class LoginActivity : AppCompatActivity(), LoginListener {

    var viewModel: LoginViewModel? = null
    var binding: ActivityLoginBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(LoginViewModel::class.java)

        binding?.viewModel = viewModel

        viewModel!!.loginListener = this

        initObservables()

    }

    private fun initObservables() {
        viewModel?.userLogin?.observe(this, Observer { response ->
            if (response?.code == false) {

                MyPreferencesHelper.setStringValue(this,PREF_USER,Gson().toJson(response.data))
                MyPreferencesHelper.setStringValue(this,PREF_IS_LOGIN,"true")


                AppUtils.logDebug("LoginActivity","Login api Respose ====>> "+Gson().toJson(response.data))
                Toasty.success(this, "${response?.message}", Toasty.LENGTH_SHORT).show()

               callAPi()

            } else
                Toasty.warning(this, "${response?.message}", Toasty.LENGTH_SHORT).show()
        })
    }

     fun callAPi() {

        var token: String?
        token = MyPreferencesHelper.getStringValue(this, AppConstant.PREF_TOKEN, "")
        val userdataDetails: UserDetailsData? = MyPreferencesHelper.getUser(this)

        Log.e("##id", userdataDetails?.id + "\n" + token)

        val apiclient = ApiClient.clientUpdateToken()
        val apiInterface = apiclient?.create(ApiInterface::class.java)
        val call = apiInterface?.updateToken(userdataDetails!!.id, token!!)

        call?.enqueue(object : retrofit2.Callback<HttpResponse> {
            override fun onResponse(call: Call<HttpResponse>, response: Response<HttpResponse>) {

                Log.e("##apiSuccess", response.body()?.code.toString())
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()


            }

            override fun onFailure(call: Call<HttpResponse>, t: Throwable) {

            }

        })
    }


    override fun showPassword(isShow: Boolean) {
        if (isShow == true)
            binding?.edPassword?.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        else
            binding?.edPassword?.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }


}