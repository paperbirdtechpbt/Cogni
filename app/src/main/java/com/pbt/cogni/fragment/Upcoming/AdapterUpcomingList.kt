package com.pbt.cogni.fragment.Upcoming

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.pbt.cogni.R
import com.pbt.cogni.activity.map.Resultt
import com.pbt.cogni.fragment.audioVideoCall.AudioVideCallAdapter
import com.pbt.cogni.util.RecyclerviewClickLisetner
import kotlinx.android.synthetic.main.item_upcoming_row.view.*
import javax.security.auth.callback.Callback

class AdapterUpcomingList( var upcomingeventes: ArrayList<UpcomingEvents>,
                           var callbacks: ( UpcomingEvents) -> Unit
                           )
    : RecyclerView.Adapter<AdapterUpcomingList.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upcoming_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.txtstartlocation.text="  "+upcomingeventes.get(position).startlocation
       holder.txtendlocation.text="  "+upcomingeventes.get(position).endlocation
       holder.txtdate.text=upcomingeventes.get(position).dates
        holder.itemView.item_upcoming_roww.setOnClickListener{
       callbacks.invoke(upcomingeventes.get(position))
        }}

    override fun getItemCount(): Int {
        Log.d("####listsize",upcomingeventes.toString())
          return  upcomingeventes.size
    }
    class ViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        val txtstartlocation = binding.findViewById<TextView>(R.id.upcoming_txtstartlocation)
        val txtendlocation = binding.findViewById<TextView>(R.id.upcoming_txtEndlocation)
        val txtdate = binding.findViewById<TextView>(R.id.upcoming_txtDate)

    }

}
