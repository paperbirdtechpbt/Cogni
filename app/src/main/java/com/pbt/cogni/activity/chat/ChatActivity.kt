package com.pbt.cogni.activity.chat

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.firebase.client.ChildEventListener
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.google.gson.Gson

import com.pbt.cogni.R
import com.pbt.cogni.activity.chat.adapter.ChatAdapter
import com.pbt.cogni.databinding.ActivityChat2Binding
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.Config
import com.pbt.cogni.util.MyPreferencesHelper
import com.pbt.cogni.viewModel.ChatViewModel

class ChatActivity : AppCompatActivity() {

    companion object{
        private const val TAG : String = "ChatActivity"
        public  var isChatVisible : Boolean = false;
    }

    var binding : ActivityChat2Binding? = null;
    var chatViewModel  : ChatViewModel? =  null
    var reference1: Firebase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat2)
        chatViewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(ChatViewModel::class.java)
        binding?.chatViewModel = chatViewModel
        binding?.executePendingBindings()

         var reciverID : Int = 10
         var chatID : Int = 10
         var userID : Int = MyPreferencesHelper.getUser(this@ChatActivity)!!.id.toInt()

        binding?.chatViewModel?.initChat(this@ChatActivity,reciverID,userID,chatID)

        binding?.chatViewModel?.mAdapter = ChatAdapter(this@ChatActivity,ArrayList<Chat>())
        binding?.listviewChat?.setAdapter(binding?.chatViewModel?.mAdapter)

        isChatVisible = true;

    }

    override fun onResume() {
        super.onResume()
        isChatVisible =  true;
    }

    override fun onPause() {
        super.onPause()
        isChatVisible =  false;
    }

}