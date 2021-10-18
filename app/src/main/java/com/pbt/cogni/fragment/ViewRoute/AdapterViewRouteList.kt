package com.pbt.cogni.fragment.ViewRoute

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pbt.cogni.R
import com.pbt.cogni.activity.map.Resultt
import com.pbt.cogni.databinding.ItemChatUserBinding
import com.pbt.cogni.fragment.Chat.AdapterChatUserList
import com.pbt.cogni.fragment.ViewRoute.ViewRouteFragementViewModel.Companion.myDAta
import com.pbt.cogni.fragment.audioVideoCall.AudioVideCallAdapter
import com.pbt.cogni.fragment.audioVideoCall.AudioVideoViewModel
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.model.Routes
import com.pbt.cogni.model.Users
import com.pbt.cogni.repository.AnalystRepo
import com.pbt.cogni.util.MyPreferencesHelper
import com.pbt.cogni.util.RecyclerviewClickLisetner
import com.squareup.okhttp.Route

class AdapterViewRouteList(

    var context: Context?,
//    var callbacks: (Int, View, Resultt, String) -> Unit

) :
    RecyclerView.Adapter<AdapterViewRouteList.ViewHolder>() {
       private var countrylist: HttpResponse? = null

    fun setCountryList(countrylist: HttpResponse) {
        this.countrylist = countrylist
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterViewRouteList.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.myroutes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterViewRouteList.ViewHolder, position: Int) {
       holder.txtorigin.setText(myDAta?.result?.get(position)?.StartCity)
       holder.txtdestination.setText(myDAta?.result?.get(position)?.EndCity)
    }

    override fun getItemCount(): Int {
        if (countrylist == null) {
            return 0

        } else {

//            return countrylist?.size!!

        }
        return 0
    }

    class ViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        val txtorigin = binding.findViewById<TextView>(R.id.origin)
        val txtdestination = binding.findViewById<TextView>(R.id.destination)
        val txtbtnVIewRoute = binding.findViewById<TextView>(R.id.btnVIewRoute)



    }

}