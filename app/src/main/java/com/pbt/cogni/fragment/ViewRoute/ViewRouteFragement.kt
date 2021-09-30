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
import com.google.gson.Gson
import com.pbt.cogni.activity.MapsActivity


private const val START = "origin"
private const val END = "destination"
private const val STARTLATLONG = "originlatlong"
private const val ENDLATLONG = "destinationlatlong"

class ViewRouteFragement : Fragment() {
    val mylist = ArrayList<MyRoutesDataClass>()
    lateinit var listAdapter: MyRoutesListAdapter
    var recyclerView: RecyclerView? = null
    var origin: String? = null
    var destonation: String? = null
    var oringinlatlong: LatLng? = null
    var destinationlatlong: LatLng? = null


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

        addData()

//        start = view.findViewById(R.id.origin)

        recyclerView?.layoutManager = LinearLayoutManager(context)
        listAdapter = MyRoutesListAdapter(mylist, context) { i, view ->

            when (view.id) {
                R.id.btnVIewRoute -> {
                    Log.e("##Intent", " Pass Data : " + Gson().toJson(mylist.get(i)))
                }
            }
        }
        recyclerView?.setAdapter(listAdapter)

        return view
    }

    private fun passIntenet(
        orgin: String,
        Destination: String,
        originlat: LatLng,
        destinatio: LatLng
    ) {
        val intent = Intent(activity, MapsActivity::class.java)
        intent.putExtra(START, orgin)
        intent.putExtra(END, Destination)
        intent.putExtra(STARTLATLONG, originlat.toString())
        intent.putExtra(ENDLATLONG, destinatio.toString())
        (activity as MainActivity?)!!.startActivity(intent)
    }

    private fun addData() {


        mylist.add(
            MyRoutesDataClass(
                "Ahmedabad",
                "Banaskantha",
                LatLng(23.0225, 72.5714),
                LatLng(24.3455, 71.7622)
            )
        )
        mylist.add(
            MyRoutesDataClass(
                "Banaskantha",
                "Kheda",

                LatLng(24.3455, 71.7622),
                LatLng(22.7507, 72.6847)
            )
        )
        mylist.add(
            MyRoutesDataClass(
                "Limdi",
                "Botad",

                LatLng(22.5688, 71.8019),
                LatLng(22.1723, 71.6636)
            )
        )
        mylist.add(
            MyRoutesDataClass(
                "Rajkot",
                "Bhavnagar",

                LatLng(22.3039, 70.8022),
                LatLng(21.7645, 72.1519)
            )
        )
        mylist.add(
            MyRoutesDataClass(
                "Mumbai",
                "Pune",

                LatLng(19.0760, 72.8777),
                LatLng(18.5204, 73.8567)
            )
        )
        mylist.add(
            MyRoutesDataClass(
                "Delhi",
                "Udaipur",

                LatLng(28.7041, 77.1025),
                LatLng(24.5854, 73.7125)
            )
        )
        mylist.add(
            MyRoutesDataClass(
                "Udaipur",
                "Delhi",

                LatLng(24.5854, 73.7125),
                LatLng(28.7041, 77.1025)
            )
        )
        mylist.add(
            MyRoutesDataClass(
                "Surat",
                "Vadodara",

                LatLng(21.1702, 72.8311),
                LatLng(22.3072, 73.1812)
            )
        )

    }

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
}
