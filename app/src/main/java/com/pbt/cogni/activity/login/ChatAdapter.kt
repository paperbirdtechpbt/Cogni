package com.pbt.cogni.activity.login

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pbt.cogni.R
import com.pbt.cogni.model.Message
import kotlinx.android.synthetic.main.row_my_message.view.*

class ChatAdapter(val context: Context, val data: ArrayList<Message>, val loggedUId: String) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, type: Int): ViewHolder {
        return if (type == 1) {
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_others_message, p0, false))
        } else {
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_my_message, p0, false))
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        var message = data[p1]

        p0.tvMessage.text = message.message
        p0.tvTimeDate.text = getRelativeTimeString(message.timestamp)
    }

    override fun getItemViewType(position: Int): Int {
        return if (loggedUId.equals(data.get(position).senderId)) {
            0
        } else {
            1
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage = view.tvMessage
        val tvTimeDate = view.tvTimeDate
    }



    fun getRelativeTimeString(timestamp: Long): String {
        return if (System.currentTimeMillis() <= timestamp + 60000) {
            "Just Now"
        } else {
            DateUtils.getRelativeTimeSpanString(
                timestamp,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS
            ).toString()
        }
    }
}
