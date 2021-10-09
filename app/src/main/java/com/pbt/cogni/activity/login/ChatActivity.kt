package com.pbt.cogni.activity.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pbt.cogni.R
import com.pbt.cogni.model.Message
import com.pbt.cogni.model.User
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context?, user: User, loggedUserId: String) {
            val intent: Intent? = Intent(context, ChatActivity::class.java)
            intent?.putExtra("user", user)
            intent?.putExtra("loggedUserId", loggedUserId)
            context?.startActivity(intent)
        }
    }
    private var user: User? = null
    private var loggedUserId: String? = null;
    private var messageList: ArrayList<Message>? = ArrayList()
    private var database: FirebaseDatabase? = null
    private var adapter: ChatAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        user = intent.getParcelableExtra("user")
        loggedUserId = intent.getStringExtra("loggedUserId")

        title = user?.displayname
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


        database = FirebaseDatabase.getInstance()

        btnSend.setOnClickListener {
            if (!TextUtils.isEmpty(etMessage.text.toString().trim())) {
                sendMessage()
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }


        }
        getData()

    }

    private fun sendMessage() {
        val key = database?.reference?.child("message")?.push()?.key
        val firebase =
            database?.reference?.child("message")?.child(getMessageId(loggedUserId!!, user?.uid!!))?.child(key!!)

        firebase?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

        var message = Message(etMessage.text.toString().trim(), loggedUserId!!, user?.uid!!, System.currentTimeMillis())
        firebase?.setValue(message)

        etMessage.setText("")
    }

    private fun getMessageId(uid1: String, uid2: String): String {
        if (uid1 > uid2) {
            return uid1 + uid2
        } else {
            return uid2 + uid1
        }
    }

    private fun getData() {

        val query = database?.reference?.child("message")?.child(getMessageId(loggedUserId!!, user?.uid!!))

        query?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                progress.visibility = View.GONE
            }

            override fun onDataChange(p0: DataSnapshot) {
                progress.visibility = View.GONE
                messageList?.clear()
                for (data in p0.children) {
                    var message: Message? = Message(
                        data.child("message").value as String,
                        data.child("senderId").value as String,
                        data.child("receiverId").value as String,
                        data.child("timestamp").value as Long
                    )
                    messageList?.add(message!!)

                }

                setAdapter()
            }

        })
    }

    private fun setAdapter() {
        if (adapter == null) {
            rvMessage.layoutManager = LinearLayoutManager(this)
            adapter = ChatAdapter(this, messageList!!, loggedUserId!!)
            rvMessage.adapter = adapter
        } else {
            adapter?.notifyDataSetChanged()
        }

        rvMessage.scrollToPosition(messageList?.size!! - 1)
    }
}