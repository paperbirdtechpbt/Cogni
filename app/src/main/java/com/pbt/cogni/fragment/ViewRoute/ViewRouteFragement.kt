package com.pbt.cogni.fragment.ViewRoute

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.pbt.cogni.R
import com.pbt.cogni.activity.viewRoute.MyRoutesDataClass
import com.pbt.cogni.activity.viewRoute.MyRoutesListAdapter

import com.pbt.cogni.activity.home.MainActivity

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.pbt.cogni.WebService.Api
import com.pbt.cogni.activity.MapsActivity
import com.pbt.cogni.activity.map.ShowRouteActivity
import com.pbt.cogni.fragment.Chat.AdapterChatUserList
import com.pbt.cogni.fragment.Chat.UserChatListViewModel
import com.pbt.cogni.fragment.audioVideoCall.AudioVideCallAdapter
import com.pbt.cogni.fragment.audioVideoCall.AudioVideoViewModel
import com.pbt.cogni.model.Routes
import com.pbt.cogni.model.Users
import com.pbt.cogni.repository.AnalystRepository
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import com.pbt.cogni.util.RecyclerviewClickLisetner



const val START = "origin"
 const val END = "destination"


class ViewRouteFragement : Fragment() ,RecyclerviewClickLisetner {
    val mylist = ArrayList<Routes>()
    lateinit var listAdapter: AdapterViewRouteList
    var recyclerView: RecyclerView? = null
    var destonation: String? = null




    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(START)
            param2 = it.getString(END)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_view_route_fragement, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()

        recyclerView = view.findViewById(R.id.recyclerviewViewRouteFragment)

//        addData()

//        start = view.findViewById(R.id.origin)
        initRecyclerView()
        initViewModel()





        return view
    }

    private fun initViewModel() {
        val viewmodel: ViewRouteFragementViewModel =
            ViewModelProvider(this).get(ViewRouteFragementViewModel::class.java)
        viewmodel.getLiveDataObserver()?.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                listAdapter?.setCountryList(it)
                listAdapter?.notifyDataSetChanged()
            }
        })
        viewmodel.onRouteListRequest(requireContext())
    }

    private fun initRecyclerView() {
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        listAdapter = AdapterViewRouteList(requireContext())
        recyclerView?.adapter = listAdapter
    }

//
//    private fun addData() {
//
//
//        mylist.add(
//            MyRoutesDataClass(
//                "Ahmedabad",
//                "Banaskantha",
//                LatLng(23.0225, 72.5714),
//                LatLng(24.3455, 71.7622)
//            )
//        )
//        mylist.add(
//            MyRoutesDataClass(
//                "Banaskantha",
//                "Kheda",
//
//                LatLng(24.3455, 71.7622),
//                LatLng(22.7507, 72.6847)
//            )
//        )
//        mylist.add(
//            MyRoutesDataClass(
//                "Limdi",
//                "Botad",
//
//                LatLng(22.5688, 71.8019),
//                LatLng(22.1723, 71.6636)
//            )
//        )
//        mylist.add(
//            MyRoutesDataClass(
//                "Rajkot",
//                "Bhavnagar",
//
//                LatLng(22.3039, 70.8022),
//                LatLng(21.7645, 72.1519)
//            )
//        )
//        mylist.add(
//            MyRoutesDataClass(
//                "Mumbai",
//                "Pune",
//
//                LatLng(19.0760, 72.8777),
//                LatLng(18.5204, 73.8567)
//            )
//        )
//        mylist.add(
//            MyRoutesDataClass(
//                "Delhi",
//                "Udaipur",
//
//                LatLng(28.7041, 77.1025),
//                LatLng(24.5854, 73.7125)
//            )
//        )
//        mylist.add(
//            MyRoutesDataClass(
//                "Udaipur",
//                "Delhi",
//
//                LatLng(24.5854, 73.7125),
//                LatLng(28.7041, 77.1025)
//            )
//        )
//        mylist.add(
//            MyRoutesDataClass(
//                "Surat",
//                "Vadodara",
//
//                LatLng(21.1702, 72.8311),
//                LatLng(22.3072, 73.1812)
//            )
//        )

//    }

    companion object {


        @JvmStatic
        fun newInstance(origin: String, desti: String, startlatlong: String, endlatlong: String) =
            ViewRouteFragement().apply {
                arguments = Bundle().apply {
                    putString(START, origin)
                    putString(END, destonation)
                    putString(START, startlatlong)
                    putString(END, endlatlong)
                }
            }
    }

    override fun onRecyclerViewItemClick(view: View, analystModel: Users) {

    }
}
