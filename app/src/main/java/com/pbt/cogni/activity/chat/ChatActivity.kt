package com.pbt.cogni.activity.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.firebase.client.Firebase

import com.pbt.cogni.R
import com.pbt.cogni.activity.chat.adapter.ChatAdapter
import com.pbt.cogni.databinding.ActivityChat2Binding
import com.pbt.cogni.model.Chat
import com.pbt.cogni.util.AppConstant
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

         var reciverID : Int = intent.getIntExtra(AppConstant.RECEIVER_ID,0)
         var reciverName : String = intent.getStringExtra(AppConstant.RECEIVER_NAME)
         var userID : Int = MyPreferencesHelper.getUser(this@ChatActivity)!!.id.toInt()

        binding?.chatViewModel?.initChat(this@ChatActivity,reciverID,userID,reciverName)

        binding?.chatViewModel?.mAdapter = ChatAdapter(this@ChatActivity,ArrayList<Chat>())
        binding?.listviewChat?.setAdapter(binding?.chatViewModel?.mAdapter)

        isChatVisible = true;

        binding!!.backArrow.setOnClickListener({
            finish()
        })


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