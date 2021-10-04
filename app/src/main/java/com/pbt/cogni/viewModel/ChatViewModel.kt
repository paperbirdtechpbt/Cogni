package com.pbt.cogni.viewModel

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Parcel
import android.view.View
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.firebase.client.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.pbt.cogni.activity.chat.Chat
import com.pbt.cogni.activity.chat.adapter.ChatAdapter
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.Config.BASE_FIREBASE_URLC
import java.util.*
import kotlin.collections.ArrayList


class ChatViewModel(app: Application) : AndroidViewModel(app) {

    val context = app

    var data = MutableLiveData<ArrayList<Chat>>(ArrayList<Chat>())
    var reference1: Firebase? = null
    var message: ObservableField<String>? = null
    var userId: ObservableField<String>? = null
    var mAdapter: ChatAdapter? = null

    init {
        message = ObservableField("")
        userId = ObservableField("")
        data = MutableLiveData<ArrayList<Chat>>(ArrayList<Chat>())
    }


    fun onMesageTextChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
        AppUtils.logDebug("ChatView", "Message Change Call")
    }


    fun initChat(context: Context, reciverID: String,userID : String) {


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



        reference1 = Firebase(BASE_FIREBASE_URLC.toString() + reciverID)

        setData(context,reference1!!)
    }

    fun setData(context: Context,reff: Firebase) {

        reff!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                try {
                    val chat = dataSnapshot.getValue(Chat::class.java)

                    AppUtils.logDebug(TAG,"Time Stamp : "+chat.timestamp)

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

                } catch (e: Exception) {
                    e.localizedMessage
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {

//                val newChat = dataSnapshot.getValue(Chat::class.java)

                AppUtils.logDebug(TAG,"Data Cahnge : "+Gson().toJson(dataSnapshot.getValue()))

                data.value!!.forEachIndexed { index, chat ->

                    if(chat.sender.equals(userId!!.get())  && dataSnapshot.key.equals(chat.key)){
                        mAdapter?.getItem(index)?.read = 2
                        AppUtils.logDebug(TAG,"index ==>> "+mAdapter?.getItem(index)!!.text)
                    }
                }

                mAdapter?.notifyDataSetChanged()

            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {}
            override fun onCancelled(firebaseError: FirebaseError) {}
        })

    }

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
    companion object{
        private const val TAG : String = "ChatViewModel"
    }
}