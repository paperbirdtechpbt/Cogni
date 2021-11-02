package com.pbt.cogni.fragment.ViewRoute

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pbt.cogni.R
import com.pbt.cogni.activity.MapsActivity
import com.pbt.cogni.model.Routes
import com.pbt.cogni.util.AppConstant.Companion.CONST_ASSIGN_ID
import com.pbt.cogni.util.AppConstant.Companion.CONST_ASSIGN_TRIP
import com.pbt.cogni.util.AppConstant.Companion.CONST_FROM_ADDRESS
import com.pbt.cogni.util.AppConstant.Companion.CONST_ROUTE_ID
import com.pbt.cogni.util.AppConstant.Companion.CONST_STATUS
import com.pbt.cogni.util.AppConstant.Companion.CONST_STATUS_APPROVED
import com.pbt.cogni.util.AppConstant.Companion.CONST_TO_ADDRESS
import com.pbt.cogni.util.AppConstant.Companion.CONST_TO_DESTINATION_LAT
import com.pbt.cogni.util.AppConstant.Companion.CONST_TO_DESTINATION_LONG
import com.pbt.cogni.util.AppConstant.Companion.CONST_TO_ORIGIN_LAT
import com.pbt.cogni.util.AppConstant.Companion.CONST_TO_ORIGIN_LONG
import com.pbt.cogni.util.RoutesViewRecyclerViewItemClick


const val START = "origin"
const val END = "destination"


class ViewRouteFragement : Fragment(), RoutesViewRecyclerViewItemClick {
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

        recyclerView = view.findViewById(R.id.recyclerRoutes)


        initViewModel()
        return view
    }

    private fun initViewModel() {
        val viewmodel: ViewRouteFragementViewModel =
            ViewModelProvider(this).get(ViewRouteFragementViewModel::class.java)

        viewmodel.onRouteListRequest(requireContext())

        viewmodel?.routesList.observe(viewLifecycleOwner, Observer { routes ->
            recyclerView?.layoutManager = LinearLayoutManager(requireContext())
            listAdapter = AdapterViewRouteList(routes, this)
            recyclerView?.adapter = listAdapter
        })
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


    override fun onRecyclerViewItemClick(view: View, routes: Routes) {
        val intent = Intent(activity, MapsActivity::class.java)
            intent.putExtra(CONST_TO_ADDRESS, routes.StartLocation)
            intent.putExtra(CONST_FROM_ADDRESS, routes.EndLocation)
            intent.putExtra(CONST_STATUS, routes.status)

            intent.putExtra(CONST_TO_ORIGIN_LAT, routes.startLat.toDouble())
            intent.putExtra(CONST_TO_ORIGIN_LONG, routes.startLong.toDouble())
            intent.putExtra(CONST_TO_DESTINATION_LAT, routes.endLat.toDouble())
            intent.putExtra(CONST_TO_DESTINATION_LONG, routes.endLong.toDouble())
            intent.putExtra(CONST_ROUTE_ID, routes.MSTRouteId)
            intent.putExtra(CONST_ASSIGN_ID, routes.assignId)

        startActivity(intent)
    }
}
