package com.pbt.cogni.util

import android.view.View
import com.pbt.cogni.model.Routes
import com.pbt.cogni.model.Users

interface RoutesViewRecyclerViewItemClick {
    fun onRecyclerViewItemClick(view : View, routes: Routes)
}