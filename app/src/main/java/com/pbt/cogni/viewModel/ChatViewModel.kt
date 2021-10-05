package com.pbt.cogni.viewModel

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Handler
import android.os.Looper
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
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.pbt.cogni.activity.chat.Chat
import com.pbt.cogni.activity.chat.adapter.ChatAdapter
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.Config.BASE_FIREBASE_URLC
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class ChatViewModel(app: Application) : AndroidViewModel(app) {

    val context = app

    var data = MutableLiveData<ArrayList<Chat>>(ArrayList<Chat>())
    var reference1: Firebase? = null
    var typingReference: Firebase? = null
    var message: ObservableField<String>? = null
    var userId: ObservableField<Int>? = null
    var chatID: ObservableField<Int>? = null
    var reciverId: ObservableField<Int>? = null
    var currentUser: ObservableField<String>? = null
    var mAdapter: ChatAdapter? = null
    var isTyping: ObservableField<String>? = null


    init {
        message = ObservableField("")
        currentUser = ObservableField("")
        chatID = ObservableField(0)
        userId = ObservableField(0)
        reciverId = ObservableField(0)
        isTyping = ObservableField("")
        data = MutableLiveData<ArrayList<Chat>>(ArrayList<Chat>())
    }


    fun onMesageTextChanged(s: CharSequence, start: Int, befor: Int, count: Int) {

        if (s.length > 0) {
            var type = object {
                var id = userId!!.get()!!.toInt()
                var isTyping = s.toString()
            }

            FirebaseDatabase.getInstance().getReference("messages").child(chatID!!.get().toString())
                .child(AppConstant.TYPING).child(currentUser!!.get().toString()).setValue(type)
        }
    }


    fun initChat(context: Context, reciverID: Int, userID: Int, chatID: Int) {

        userId!!.set(userID)
        this.chatID!!.set(chatID)
        reciverId!!.set(reciverID)
        Firebase.setAndroidContext(context)

        reference1 = Firebase(BASE_FIREBASE_URLC.toString() + chatID)
        typingReference =
            Firebase(BASE_FIREBASE_URLC.toString() + chatID + "/" + AppConstant.TYPING)

        val checkTypeReff =
            FirebaseDatabase.getInstance().getReference("messages").child("${chatID}");
        val query: Query = checkTypeReff.child("typing")
        setData(context, reference1!!, typingReference!!, query, chatID)
    }

    fun setData(
        context: Context,
        reff: Firebase,
        typingReference: Firebase,
        query: Query,
        chatID: Int
    ) {

        query.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {

                AppUtils.logWarning(TAG," 103 onChildAdded "+snapshot!!.getValue())

                if (snapshot.getValue() == null) {

                    val typing = object {
                        var user1 = object {
                            var id = reciverId!!.get()!!.toInt()
                            var isTyping = ""
                        }
                        var user2 = object {
                            var id = userId!!.get()!!.toInt()
                            var isTyping = ""
                        }
                    }
                    FirebaseDatabase.getInstance().getReference("messages").child("${chatID}")
                        .child(AppConstant.TYPING).setValue(typing)
                    currentUser!!.set("user1")
                } else {

                    val map = snapshot!!.value as Map<*, *>
                    var obj = map.get("user1") as Map<*, *>
                    if (obj.get("id") == userId!!.get()!!.toInt()) {
                        AppUtils.logDebug(TAG, "query Change Call ===>> " + obj.get("id"))
                        currentUser!!.set("user1")
                    } else {
                        AppUtils.logDebug(TAG, "query Change Call else ==> " + obj.get("id"))
                        currentUser!!.set("user2")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        reff.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String??) {

                AppUtils.logWarning(TAG," 140 onChildAdded "+dataSnapshot!!.getValue())
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
                    } else {

                        val map = dataSnapshot!!.value as Map<*, *>
                        if (!map.isEmpty()) {
                            try {
                                val obj = JSONObject(map.get("user1").toString())
                                if (userId!!.equals(obj.getString("id"))) {
                                    currentUser!!.set("user1")
                                } else {
                                    currentUser!!.set("user2")
                                }
                            } catch (e: Exception) {
                                AppUtils.logError(TAG, "Exception : ==${e.message}")
                            }

                        }
                        AppUtils.logDebug(TAG, " Typing ====>> " + Gson().toJson(map.get("user1")))
                    }

                } catch (e: Exception) {
                    e.message
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String??) {
                AppUtils.logWarning(TAG," 186 onChildChanged "+dataSnapshot!!.getValue())
                val map = dataSnapshot.getValue().toString()
                if (!map.contains("user1") && !map.contains("user2")) {
                data.value!!.forEachIndexed { index, chat ->

                    if (chat.sender == userId!!.get() && dataSnapshot.key.equals(chat.key)) {
                        mAdapter?.getItem(index)?.read = 2
                        AppUtils.logDebug(TAG, "index ==>> " + mAdapter?.getItem(index)!!.text)
                    }
                }
                mAdapter?.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {}
            override fun onCancelled(firebaseError: FirebaseError) {
            }
        })

        typingReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot?, p1: String??) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, p1: String??) {

                AppUtils.logWarning(TAG," 213  "+dataSnapshot!!.getValue())
                val map = dataSnapshot!!.value as Map<*, *>
                if (!map.get("id").toString().equals(userId!!.get())) {
                    isTyping!!.set("Typing...")
                    Handler(Looper.getMainLooper()).postDelayed({
                        isTyping!!.set("")
                    }, 2000)
                }
            }

            override fun onChildRemoved(p0: DataSnapshot?) {}
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
            override fun onCancelled(p0: FirebaseError?) {}
        });

    }

    fun sendMessage(view: View) {

        if (!AppUtils.isNetworkConnected(context)) {
            Toast.makeText(context, "Please Connect To Internet !", Toast.LENGTH_SHORT).show()
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