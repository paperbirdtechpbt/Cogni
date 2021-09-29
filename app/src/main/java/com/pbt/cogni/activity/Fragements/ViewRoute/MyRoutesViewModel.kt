package com.pbt.cogni.activity.Fragements.ViewRoute

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyRoutesViewModel : ViewModel() {
    var lst = MutableLiveData<ArrayList<MyRoutesDataClass>>()
    var newlist = arrayListOf<MyRoutesDataClass>()

    fun add(blog: MyRoutesDataClass) {
        newlist.add(blog)
        lst.value = newlist
    }


}
