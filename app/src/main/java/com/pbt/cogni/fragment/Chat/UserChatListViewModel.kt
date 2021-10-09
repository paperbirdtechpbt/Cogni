package com.pbt.cogni.fragment.Chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.pbt.cogni.WebService.Coroutines
import com.pbt.cogni.model.Users
import com.pbt.cogni.model.BaseAnalystModel
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.model.User
import com.pbt.cogni.repository.AnalystRepository
import com.pbt.cogni.util.AppUtils
import kotlinx.coroutines.Job

class UserChatListViewModel(private val analystRepository: AnalystRepository) : ViewModel() {

    private lateinit var job : Job
    companion object{
          val _AnalystList  = MutableLiveData<List<Users>>()

    }


    private  val _userList  = MutableLiveData<HttpResponse>()

    val data : LiveData<HttpResponse>
    get() = _userList

    val listAnalystList : LiveData<List<Users>>
        get() = _AnalystList

     fun getAnalyst(companyId : String,roleId : String,userName : String){

        job = Coroutines.ioThenMain({
             analystRepository.getAnylyst(companyId,roleId,userName) },
            {
                if(it!!.code == false){

            _userList.value = it
            _AnalystList.value =  Gson().fromJson(Gson().toJson(it?.data),BaseAnalystModel::class.java).result

            AppUtils.logDebug("UserChatListViewModel","Resposne Result : "+Gson().toJson(it?.data))
                }
            })


    }

    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized)
            job.cancel()
    }

}