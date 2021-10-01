package com.pbt.cogni.fragment.Chat

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.pbt.cogni.R
import com.pbt.cogni.WebService.Api
import com.pbt.cogni.activity.chat.ChatActivity
import com.pbt.cogni.activity.home.MainActivity
import com.pbt.cogni.model.AnalystModel
import com.pbt.cogni.repository.AnalystRepository
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.RecyclerviewClickLisetner
import com.pbt.cogni.viewModel.AnalystViewModelFactory
import kotlinx.android.synthetic.main.user_chat_list_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserChatListFragment : Fragment(),RecyclerviewClickLisetner {

    private lateinit var viewModel: UserChatListViewModel
    private lateinit var factory: AnalystViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return  inflater.inflate(R.layout.user_chat_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val api =  Api()
        val repository = AnalystRepository(api)
        factory = AnalystViewModelFactory(repository)

        viewModel = ViewModelProviders.of(this,factory).get(UserChatListViewModel::class.java)

        viewModel.getAnalyst("58","5","analystanalyst@gmail.com")
        viewModel?.listAnalystList.observe(viewLifecycleOwner, Observer { analyst ->
            recyclerviewChatUserList.also {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.setHasFixedSize(true)
                it.adapter = AdapterChatUserList(analyst,this)
            }
        })
    }

    override fun onRecyclerViewItemClick(view: View, analystModel: AnalystModel) {
        when(view.id){
            R.id.item_row -> {
                val intent = Intent(activity, ChatActivity::class.java)
                startActivity(intent)
            }
        }
    }
}