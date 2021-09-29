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
import com.firebase.client.ChildEventListener
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
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
    var mAdapter: ChatAdapter ? = null

    init {
        message = ObservableField("")
        userId = ObservableField("")
        data = MutableLiveData<ArrayList<Chat>>(ArrayList<Chat>())
    }


    fun onMesageTextChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
        AppUtils.logDebug("ChatView","Message Change Call")
    }


    fun setData(context: Context, id: String, reff: Firebase) {

        userId!!.set(id)

        reff!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                try {
                    val chat = dataSnapshot.getValue(Chat::class.java)

                    if (chat.sender != null && chat.read != null) {
                        if (!chat.sender.equals("20") && (chat.read == 0 || chat.read == 1)) {
                            chat.read = 2
                            dataSnapshot.ref.setValue(chat)
                        }
                    }
                    AppUtils.logDebug("ChatViewMode", "Data : " + Gson().toJson(data.value))
                    mAdapter?.add(chat)

                    val mManager: NotificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    mManager.cancelAll();

                }catch (e: Exception){
                    e.localizedMessage
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {}
            override fun onCancelled(firebaseError: FirebaseError) {}
        })

    }

    fun initChat(context: Context,id : String) {
        Firebase.setAndroidContext(context)
        reference1 = Firebase(BASE_FIREBASE_URLC.toString() + id)



        setData(context, id, reference1!!)
    }

    fun sendMessage(view: View) {

        if (!AppUtils.isNetworkConnected(context)) {
            Toast.makeText(context, "Please Connect To Internet !", Toast.LENGTH_SHORT)
                .show()
        } else {

            if (userId != null) {

                var chat: Chat = Chat.createFromParcel(Parcel.obtain())
                chat.sender = "20"
                chat.timestamp = Date().time
                chat.type = "text"
                chat.text = message?.get()
                chat.read = 0
                reference1!!.push().setValue(chat)
                message?.set("")

            }
        }
    }
}