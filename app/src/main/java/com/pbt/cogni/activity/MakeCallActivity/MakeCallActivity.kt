package com.pbt.cogni.activity.MakeCallActivity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pbt.cogni.R
import com.pbt.cogni.databinding.MakeCallActivityBinding
import com.pbt.cogni.viewModel.MakeCallViewModel

class MakeCallActivity : AppCompatActivity() {

    var binding: MakeCallActivityBinding? = null
    var makeCallViewModel: MakeCallViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.make_call_activity)

        makeCallViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(MakeCallViewModel::class.java)

        binding?.callViewModel = makeCallViewModel
    } }