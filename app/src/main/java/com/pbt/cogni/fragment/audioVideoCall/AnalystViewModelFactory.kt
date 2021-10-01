package com.pbt.cogni.fragment.audioVideoCall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pbt.cogni.fragment.Chat.UserChatListViewModel
import com.pbt.cogni.repository.AnalystRepository

class AnalystViewModelFactory(private val repository: AnalystRepository) : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserChatListViewModel(repository) as T
    }
}