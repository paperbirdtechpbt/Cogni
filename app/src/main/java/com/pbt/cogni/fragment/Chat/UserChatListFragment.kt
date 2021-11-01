package com.pbt.cogni.fragment.Chat

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.pbt.cogni.R
import com.pbt.cogni.WebService.Api
import com.pbt.cogni.activity.chat.ChatActivity
import com.pbt.cogni.model.Users
import com.pbt.cogni.repository.AnalystRepository
import com.pbt.cogni.util.AppConstant.Companion.RECEIVER_ID
import com.pbt.cogni.util.AppConstant.Companion.RECEIVER_NAME
import com.pbt.cogni.util.MyPreferencesHelper
import com.pbt.cogni.util.RecyclerviewClickLisetner
import com.pbt.cogni.viewModel.AnalystViewModelFactory
import kotlinx.android.synthetic.main.user_chat_list_fragment.*

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

        viewModel.getAnalyst(MyPreferencesHelper.getUser(requireActivity())!!.companyId,MyPreferencesHelper.getUser(requireActivity())!!.RoleId,MyPreferencesHelper.getUser(requireActivity())!!.UserName)

        viewModel?.listAnalystList.observe(viewLifecycleOwner, Observer { analyst ->
            recyclerviewChatUserList.also {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.setHasFixedSize(true)
                it.adapter = AdapterChatUserList(analyst,this)
            }
        })
    }

    override fun onRecyclerViewItemClick(view: View, users: Users) {
        when(view.id){
            R.id.item_row -> {
                val intent = Intent(activity, ChatActivity::class.java)
                intent.putExtra(RECEIVER_ID,users.Id)
                intent.putExtra(RECEIVER_NAME,users.FirstName+" "+users.LastName)
                startActivity(intent)
            }
        }
    }
}