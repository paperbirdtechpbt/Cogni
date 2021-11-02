package com.pbt.cogni.activity.map

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pbt.cogni.R
import com.pbt.cogni.databinding.ItemExpenseBinding
import com.pbt.cogni.model.Expense
import com.pbt.cogni.util.ClickListener
import com.pbt.cogni.util.Config.BASE_NETWORK_IMAGE


class AdapterExpense(
    private val listAnalystList: List<Expense>,
    private val listener: ClickListener
) :
    RecyclerView.Adapter<AdapterExpense.MyViewHolder>() {

    inner class MyViewHolder(val recyclerViewBinding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(recyclerViewBinding.root)

    override fun getItemCount() = listAnalystList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_expense, parent, false
            )
        )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.recyclerViewBinding.expense = listAnalystList[position]
        holder.recyclerViewBinding.root.setOnClickListener {
            listener.onItemClick(position, it)
        }
    }
}

@BindingAdapter("android:src")
fun setImageUrl(view: ImageView, url: String?) {
    Glide.with(view.context).load(BASE_NETWORK_IMAGE+url).placeholder(R.drawable.backgroundimage).into(view)
}
