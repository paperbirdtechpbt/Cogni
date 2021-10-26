package com.pbt.cogni.fragment.Finish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pbt.cogni.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FinishFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
      val view:View  =inflater.inflate(R.layout.fragment_finish, container, false)
        val recyclerview = view.findViewById<RecyclerView>(R.id.finish_recyclerview)


        recyclerview.layoutManager = LinearLayoutManager(context)


        val data = ArrayList<ItemsViewModel>()

            data.add(ItemsViewModel(R.drawable.lunch,"Lunch - 1000/-"))
            data.add(ItemsViewModel(R.drawable.reciept,"Toll - 500/-"))
            data.add(ItemsViewModel(R.drawable.reciept,"Extra - 500/-"))
            data.add(ItemsViewModel(R.drawable.lunch,"Breakfast - 500/-"))
            data.add(ItemsViewModel(R.drawable.lunch,"Breakfast - 500/-"))
            data.add(ItemsViewModel(R.drawable.lunch,"Breakfast - 500/-"))
            data.add(ItemsViewModel(R.drawable.lunch,"Breakfast - 500/-"))

        val adapter = CustomAdapter(data)

        recyclerview.adapter = adapter
        return  view
    }

//    companion object {
//
//
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            FinishFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}