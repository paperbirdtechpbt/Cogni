package com.pbt.cogni.util

import android.view.View
import com.pbt.cogni.model.AnalystModel

interface RecyclerviewClickLisetner {
fun onRecyclerViewItemClick(view : View,analystModel: AnalystModel)
}