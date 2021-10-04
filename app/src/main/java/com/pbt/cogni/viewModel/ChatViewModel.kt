package com.pbt.cogni.viewModel

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.firebase.client.ChildEventListener
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.google.gson.Gson
import com.pbt.cogni.activity.chat.Chat
import com.pbt.cogni.activity.chat.adapter.ChatAdapter
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.Config.BASE_FIREBASE_URLC
import java.util.*
import kotlin.collections.ArrayList


class ChatViewModel(app: Application) : AndroidViewModel(app) {

    val context = app

    var data = MutableLiveData<ArrayList<Chat>>(ArrayList<Chat>())
    var reference1: Firebase? = null
    var typingReference: Firebase? = null
    var message: ObservableField<String>? = null
    var userId: ObservableField<String>? = null
    var mAdapter: ChatAdapter? = null
    var isTyping: ObservableField<String>? = null


    init {
        message = ObservableField("")
        userId = ObservableField("")
        isTyping = ObservableField("")
        data = MutableLiveData<ArrayList<Chat>>(ArrayList<Chat>())
    }


    fun onMesageTextChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
        AppUtils.logDebug("ChatView", "Message Change Call")
    }


    fun initChat(context: Context, reciverID: String, userID: String,chatID: String) {

        userId!!.set(userID)
        Firebase.setAndroidContext(context)

//        var reff1: Firebase? = Firebase(BASE_FIREBASE_URLC + userID+"_"+reciverID)
//        var reff2: Firebase? = Firebase(BASE_FIREBASE_URLC +reciverID+"_"+userID)
//
//
//        AppUtils.logDebug("")
//
//        reference1!!.child("messages").addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
//
//            }
//
//            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {
//            }
//            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
//            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {}
//            override fun onCancelled(firebaseError: FirebaseError) {}
//        })

        reference1 = Firebase(BASE_FIREBASE_URLC.toString() + chatID)
        typingReference = Firebase(BASE_FIREBASE_URLC.toString() + chatID+"/"+AppConstant.TYPING)

        setData(context, reference1!!,typingReference!!)
    }

    fun setData(context: Context, reff: Firebase, typingReference: Firebase) {

        reff.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                try {

                    if (!dataSnapshot.key.equals("typing")) {

                        val chat = dataSnapshot.getValue(Chat::class.java)

                        if (!chat.sender!!.equals(userId!!.get()) && (chat.read == 0 || chat.read == 1)) {
                            chat.read = 2
                            dataSnapshot.ref.setValue(chat)
                        }

                        chat.key = dataSnapshot.key
                        mAdapter?.add(chat)

                        mAdapter?.notifyDataSetChanged()

                        data.value?.add(chat)

                        val mManager: NotificationManager =
                            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        mManager.cancelAll();
                    }

                } catch (e: Exception) {
                    e.localizedMessage
                }
            }
            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {

                val map = dataSnapshot.getValue().toString()
                if (!map.contains("user1")) {
                    //                val chat = dataSnapshot.getValue(Chat::class.java)
                    data.value!!.forEachIndexed { index, chat ->

                        if (chat.sender.equals(userId!!.get()) && dataSnapshot.key.equals(chat.key)) {
                            mAdapter?.getItem(index)?.read = 2
                            AppUtils.logDebug(TAG, "index ==>> " + mAdapter?.getItem(index)!!.text)
                        }
                    }
                    mAdapter?.notifyDataSetChanged()
                }
            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {}
            override fun onCancelled(firebaseError: FirebaseError) {}
        })

        typingReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                Log.e(TAG," onChildAdded call ")
            }
            override fun onChildChanged(dataSnapshot: DataSnapshot?, p1: String?) {
                val map = dataSnapshot!!.value as Map<*, *>

//                if("20".toString().equals(userId)){
                isTyping!!.set("Typing...")

                Handler(Looper.getMainLooper()).postDelayed({
                    Log.e(TAG,"Typing... thread end  call ")
                    isTyping!!.set("")
                }, 2000)
//                }



            }
            override fun onChildRemoved(p0: DataSnapshot?) {}
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
            override fun onCancelled(p0: FirebaseError?) {}
        });

    }
//    suspend fun test(){
//
//        delay(3000)
//    }

    fun sendMessage(view: View) {

        if (!AppUtils.isNetworkConnected(context)) {
            Toast.makeText(context, "Please Connect To Internet !", Toast.LENGTH_SHORT)
                .show()
        } else {

            if (!message?.equals("")!!) {

                var chat: Chat = Chat.createFromParcel(Parcel.obtain())
                chat.sender = userId!!.get()
                chat.timestamp = Date().time
                chat.type = "text"
                chat.text = message?.get()
                chat.read = 0
                reference1!!.push().setValue(chat)
                message?.set("")

            }
        }
    }

    companion object {
        private const val TAG: String = "ChatViewModel"
    }
}