package com.pbt.cogni.fragment.Chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.pbt.cogni.WebService.Coroutines
import com.pbt.cogni.activity.login.Users
import com.pbt.cogni.activity.map.ResponseDataClass
import com.pbt.cogni.model.AnalystModel
import com.pbt.cogni.model.BaseAnalystModel
import com.pbt.cogni.repository.AnalystRepository
import com.pbt.cogni.util.AppUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class UserChatListViewModel(private val analystRepository: AnalystRepository) : ViewModel() {

    private lateinit var job : Job

    private  val _AnalystList  = MutableLiveData<List<Users>>()
    private  val _userList  = MutableLiveData<ResponseDataClass>()
    val data : LiveData<ResponseDataClass>
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
//                _AnalystList.value =_userList.
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