package com.pbt.cogni.fragment.Upcoming

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pbt.cogni.R
import com.pbt.cogni.activity.MapsActivity
import com.pbt.cogni.activity.TabLayout.TabLayoutFragment
import com.pbt.cogni.fragment.Current.CurrentFragment
import com.pbt.cogni.fragment.Current.CurrentTripViewModel
import com.pbt.cogni.model.Users
import com.pbt.cogni.util.RecyclerviewClickLisetner
import kotlinx.android.synthetic.main.fragment_upcoming.*

import com.pbt.cogni.fragment.Finish.FinishMapsFragment
import com.pbt.cogni.fragment.ViewRoute.AdapterViewRouteList
import com.pbt.cogni.model.Routes
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.RoutesViewRecyclerViewItemClick
import kotlinx.android.synthetic.main.fragment_current.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class UpcomingFragment : Fragment(), RoutesViewRecyclerViewItemClick {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var listAdapter: AdapterViewRouteList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View= inflater.inflate(R.layout.fragment_upcoming, container, false)
        initViewModel();
        return  view
    }

    private fun initViewModel() {
        val viewmodel: UpCommingTripViewModel = ViewModelProvider(this).get(UpCommingTripViewModel::class.java)

        viewmodel.onRouteListRequest(requireContext())

        viewmodel?.routesList.observe(viewLifecycleOwner, Observer { routes ->
            recyclerviewUpcoming?.layoutManager = LinearLayoutManager(requireContext())
            listAdapter = AdapterViewRouteList(routes, this)
            recyclerviewUpcoming?.adapter = listAdapter

            if(routes.isNullOrEmpty()) {
                rlNoData.visibility =  View.VISIBLE
            }
        })
    }

    override fun onRecyclerViewItemClick(view: View, routes: Routes) {

        val intent = Intent(activity, MapsActivity::class.java)
        intent.putExtra(AppConstant.CONST_TO_ADDRESS, routes.StartLocation)
        intent.putExtra(AppConstant.CONST_FROM_ADDRESS, routes.EndLocation)
        intent.putExtra(AppConstant.CONST_STATUS, routes.status)
        intent.putExtra(AppConstant.CONST_TO_ORIGIN_LAT, routes.startLat.toDouble())
        intent.putExtra(AppConstant.CONST_TO_ORIGIN_LONG, routes.startLong.toDouble())
        intent.putExtra(AppConstant.CONST_TO_DESTINATION_LAT, routes.endLat.toDouble())
        intent.putExtra(AppConstant.CONST_TO_DESTINATION_LONG, routes.endLong.toDouble())
        intent.putExtra(AppConstant.CONST_ROUTE_ID, routes.MSTRouteId)
        intent.putExtra(AppConstant.CONST_ASSIGN_ID, routes.assignId)

        startActivity(intent)
    }


}