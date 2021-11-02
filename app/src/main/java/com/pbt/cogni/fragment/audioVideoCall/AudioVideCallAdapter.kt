package com.pbt.cogni.fragment.audioVideoCall

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pbt.cogni.R
import com.pbt.cogni.activity.map.Resultt
import com.pbt.cogni.fragment.audioVideoCall.AudioVideoViewModel.Companion.myResult
import com.pbt.cogni.fragment.audioVideoCall.AudioVideoViewModel.Companion.result
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.repository.AnalystRepo
import com.pbt.cogni.util.MyPreferencesHelper

class AudioVideCallAdapter(
    var context: Context?,

    var callbacks: (Int, View,Resultt,String) -> Unit

) :
    RecyclerView.Adapter<AudioVideCallAdapter.ViewHolder>() {
    companion object{
       var callerName:String?=null

    }
    private var countrylist: HttpResponse? = null

    fun setCountryList(countrylist: HttpResponse) {
        this.countrylist = countrylist
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AudioVideCallAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_analyst, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioVideCallAdapter.ViewHolder, position: Int) {
         callerName=  myResult?.mydata?.get(position)?.Firstname +""+ myResult?.mydata?.get(position)?.LastName
        val sendername=MyPreferencesHelper.getUser(context!!)?.FirstName+" "+MyPreferencesHelper.getUser(context!!)?.FirstName
        Log.d("##sendername", callerName)

        holder.txtAnalyst.text =callerName
        holder.txtAnalystnumber.text = myResult?.mydata?.get(position)?.Mobile
        holder.rlVideoCall.setOnClickListener {
                callbacks.invoke(holder.adapterPosition,it, myResult?.mydata?.get(position)!!
                ,sendername)
        }
        holder.rlVoiceCall.setOnClickListener {
                callbacks.invoke(holder.adapterPosition,it, myResult?.mydata?.get(position)!!,sendername)
        }

    }

    override fun getItemCount(): Int {
        if (countrylist == null) {
            return 0
        } else {
            return countrylist?.data!!.size()
        }
    }

    class ViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        val txtAnalyst = binding.findViewById<TextView>(R.id.txtAnalyst)
        val txtAnalystnumber = binding.findViewById<TextView>(R.id.txtAnalystnumber)
        val rlVideoCall = binding.findViewById<RelativeLayout>(R.id.rlVideoCall)
        val rlVoiceCall = binding.findViewById<RelativeLayout>(R.id.rlVoiceCall)
        val layout=binding.findViewById<RelativeLayout>(R.id.relativelayout)
    }

}
