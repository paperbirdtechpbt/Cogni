package com.pbt.cogni.activity.viewRoute

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pbt.cogni.R

class MyRoutesListAdapter(

    var list: ArrayList<MyRoutesDataClass>,
    var context: Context?,
    var callbacks: (Int, View) -> Unit

) :
    RecyclerView.Adapter<MyRoutesListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyRoutesListAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.myroutes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyRoutesListAdapter.ViewHolder, position: Int) {
        holder.bind(list.get(position)!!)

        holder.btnVIewRoute.setOnClickListener {

            callbacks.invoke(holder.adapterPosition,it)
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(binding: View) : RecyclerView.ViewHolder(binding) {
        val origin = binding.findViewById<TextView>(R.id.origin)
        val dest = binding.findViewById<TextView>(R.id.destination)
        val btnVIewRoute=binding.findViewById<Button>(R.id.btnVIewRoute)


        fun bind(blog: MyRoutesDataClass) {
            origin.text = blog.origin
            dest.text = blog.destination

        }

    }

}
