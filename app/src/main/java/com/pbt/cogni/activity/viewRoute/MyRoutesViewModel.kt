package com.pbt.cogni.activity.viewRoute

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyRoutesViewModel : ViewModel() {
    var lst = MutableLiveData<ArrayList<MyRoutesDataClass>>()
    var newlist = arrayListOf<MyRoutesDataClass>()

    fun add(blog: MyRoutesDataClass) {
        newlist.add(blog)
        lst.value = newlist
    }


}
