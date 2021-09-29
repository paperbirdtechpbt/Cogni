package com.pbt.cogni.activity.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.firebase.client.Firebase

import com.pbt.cogni.R
import com.pbt.cogni.activity.chat.adapter.ChatAdapter
import com.pbt.cogni.databinding.ActivityChat2Binding
import com.pbt.cogni.viewModel.ChatViewModel

class ChatActivity : AppCompatActivity() {

    var binding : ActivityChat2Binding? = null;
    var chatViewModel  : ChatViewModel? =  null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat2)
        chatViewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(ChatViewModel::class.java)
        binding?.chatViewModel = chatViewModel
        binding?.executePendingBindings()

         var id : String = "10"

        binding?.chatViewModel?.initChat(this@ChatActivity,id)

        binding?.chatViewModel?.mAdapter = ChatAdapter(this@ChatActivity,ArrayList<Chat>())
        binding?.listviewChat?.setAdapter(binding?.chatViewModel?.mAdapter)


    }
}