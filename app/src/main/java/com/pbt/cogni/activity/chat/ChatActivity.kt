package com.pbt.cogni.activity.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.firebase.client.Firebase

import com.pbt.cogni.R
import com.pbt.cogni.activity.chat.adapter.ChatAdapter
import com.pbt.cogni.databinding.ActivityChat2Binding
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

         var reciverID : String = "10"
         var userID : String = MyPreferencesHelper.getUser(this@ChatActivity)!!.id

        binding?.chatViewModel?.initChat(this@ChatActivity,reciverID,userID)

        binding?.chatViewModel?.mAdapter = ChatAdapter(this@ChatActivity,ArrayList<Chat>())
        binding?.listviewChat?.setAdapter(binding?.chatViewModel?.mAdapter)


        isChatVisible = true;




//        Firebase.setAndroidContext(this)
//        reference1 = Firebase(Config.BASE_FIREBASE_URLC.toString() + id)
//        reference1!!.addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
//
//
//                val chat = dataSnapshot.getValue(Chat::class.java)
//
//                AppUtils.logDebug("ChatViewModel", "onChildAdded : " + Gson().toJson(chat))
//
////                    if (!chat.sender.equals("20") && (chat.read == 0 || chat.read == 1)) {
////                        chat.read = 2
////                        dataSnapshot.ref.setValue(chat)
////                    }
////                    mAdapter?.add(chat)
////                    mAdapter?.notifyDataSetChanged()
////
////                    val mManager: NotificationManager =
////                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
////                    mManager.cancelAll();
//
//
//            }
//
//            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
//
//            }
//
//            override fun onChildRemoved(p0: DataSnapshot?) {
//
//            }
//
//            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
//
//            }
//
//            override fun onCancelled(p0: FirebaseError?) {
//
//            }
//
//
//        })

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