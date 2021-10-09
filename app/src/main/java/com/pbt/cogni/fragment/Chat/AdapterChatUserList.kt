package com.pbt.cogni.fragment.Chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pbt.cogni.R
import com.pbt.cogni.model.Users
import com.pbt.cogni.databinding.ItemChatUserBinding
import com.pbt.cogni.util.RecyclerviewClickLisetner

class AdapterChatUserList(private val listAnalystList: List<Users>, private  val listener : RecyclerviewClickLisetner) :
    RecyclerView.Adapter<AdapterChatUserList.MyViewHolder>() {
    inner class MyViewHolder(val recyclerViewBinding: ItemChatUserBinding) :
        RecyclerView.ViewHolder(recyclerViewBinding.root)

    override fun getItemCount() = listAnalystList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_chat_user, parent, false
            )
        )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.recyclerViewBinding.users = listAnalystList[position]
        holder.recyclerViewBinding.root.setOnClickListener{
            listener.onRecyclerViewItemClick(holder.recyclerViewBinding.itemRow,listAnalystList[position])
        }
    }
}
