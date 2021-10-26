package com.pbt.cogni.fragment.Finish

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pbt.cogni.R

class CustomAdapter(private val mList: List<ItemsViewModel>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.totalexpense_image)
        val textView: TextView = itemView.findViewById(R.id.totalexpense_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view :View= LayoutInflater.from(parent.context)
            .inflate(R.layout.item_total_expense, parent, false)


        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
        holder.imageView.setImageResource(ItemsViewModel.image)

        holder.textView.text = ItemsViewModel.text
    }

    override fun getItemCount(): Int {
       return mList.size
    }

}
