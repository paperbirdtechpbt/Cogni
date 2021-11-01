package com.pbt.cogni.activity.login

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.pbt.cogni.R
import com.pbt.cogni.activity.home.MainActivity
import com.pbt.cogni.callback.LoginListener

import com.pbt.cogni.databinding.ActivityLoginBinding
import com.pbt.cogni.util.AppConstant.Companion.PREF_IS_LOGIN
import com.pbt.cogni.util.AppConstant.Companion.PREF_USER
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import com.pbt.cogni.viewModel.LoginViewModel
import es.dmoral.toasty.Toasty

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
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                AppUtils.logDebug("LoginActivity","Login api Respose ====>> "+Gson().toJson(response.data))
                Toasty.success(this, "${response?.message}", Toasty.LENGTH_SHORT).show()
            } else
                Toasty.warning(this, "${response?.message}", Toasty.LENGTH_SHORT).show()
        })
    }

    override fun showPassword(isShow: Boolean) {
        if (isShow == true)
            binding?.edPassword?.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        else
            binding?.edPassword?.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }


}