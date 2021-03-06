package com.pbt.cogni.fragment.audioVideoCall

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pbt.cogni.R
import com.pbt.cogni.activity.call.CallActivity
import com.pbt.cogni.repository.AnalystRepo
import com.pbt.cogni.repository.AnalystRepository
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppConstant.KEY_CALL
import com.pbt.cogni.util.AppConstant.ROOM_ID

class AudioVideCallAdapter (

    var list: ArrayList<AnalystRepo>,
    var context: Context?,

    ) :
    RecyclerView.Adapter<AudioVideCallAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AudioVideCallAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_analyst, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioVideCallAdapter.ViewHolder, position: Int) {
        holder.bind(list.get(position)!!)

        holder.rlVideoCall.setOnClickListener {
            val intent = Intent(context, CallActivity::class.java)
            intent.putExtra(AppConstant.KEY_CALL, true);
            intent.putExtra(ROOM_ID,list.get(position).roomId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intent)
        }

        holder.rlVoiceCall.setOnClickListener {
            val intent = Intent(context, CallActivity::class.java)
            intent.putExtra(KEY_CALL, false);
            intent.putExtra("name",list.get(position).analysisName)
            intent.putExtra(ROOM_ID,list.get(position).roomId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        val txtAnalyst = binding.findViewById<TextView>(R.id.txtAnalyst)
        val rlVideoCall = binding.findViewById<RelativeLayout>(R.id.rlVideoCall)
        val rlVoiceCall = binding.findViewById<RelativeLayout>(R.id.rlVoiceCall)

        fun bind(blog: AnalystRepo) {
            txtAnalyst.text = blog.analysisName
        }

    }

}
