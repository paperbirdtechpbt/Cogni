package com.pbt.cogni.WebService

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object Coroutines {
    fun<T : Any> ioThenMain(work :  suspend (() -> T?),callBack : ((T?)->Unit)) =
        CoroutineScope(Dispatchers.Main).launch {
            val data  =  CoroutineScope(Dispatchers.IO).async  rt@{

                return@rt work()
            }.await()

            callBack(data)
        }

}