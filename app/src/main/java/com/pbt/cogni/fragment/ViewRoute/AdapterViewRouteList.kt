package com.pbt.cogni.fragment.ViewRoute


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pbt.cogni.R
import com.pbt.cogni.databinding.ItemRoutesBinding
import com.pbt.cogni.model.Routes
import com.pbt.cogni.util.RoutesViewRecyclerViewItemClick


class AdapterViewRouteList(private val listAnalystList: List<Routes>, private  val listener : RoutesViewRecyclerViewItemClick) :
    RecyclerView.Adapter<AdapterViewRouteList.MyViewHolder>() {
    inner class MyViewHolder(val recyclerViewBinding: ItemRoutesBinding) :
        RecyclerView.ViewHolder(recyclerViewBinding.root)

    override fun getItemCount() = listAnalystList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_routes, parent, false
            )
        )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.recyclerViewBinding.routes = listAnalystList[position]
        holder.recyclerViewBinding.root.setOnClickListener{
            listener.onRecyclerViewItemClick(holder.recyclerViewBinding.itemRow,listAnalystList[position])
        }
    }
}
