package com.pbt.cogni.fragment.Upcoming

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
import com.google.android.gms.maps.SupportMapFragment
import com.pbt.cogni.R
import com.pbt.cogni.activity.MapsActivity
import com.pbt.cogni.fragment.Current.CurrentFragment
import com.pbt.cogni.fragment.ViewRoute.AdapterViewRouteList
import com.pbt.cogni.fragment.finishTrip.FinishTripFragment
import com.pbt.cogni.model.Routes
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.RoutesViewRecyclerViewItemClick
import kotlinx.android.synthetic.main.fragment_upcoming.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class UpcomingFragment : Fragment(), RoutesViewRecyclerViewItemClick {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var listAdapter: AdapterViewRouteList
    var viewmodel: UpCommingTripViewModel ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "UpComing"

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_upcoming, container, false)

        initViewModel()
        return view
    }

    private fun initViewModel() {

         viewmodel = ViewModelProvider(this).get(UpCommingTripViewModel::class.java)


    viewmodel?.onRouteListRequest(requireContext())



        viewmodel?.routesList?.observe(viewLifecycleOwner, Observer { routes ->
            recyclerviewUpcoming?.layoutManager = LinearLayoutManager(requireContext())
            listAdapter = AdapterViewRouteList(routes, this)
            recyclerviewUpcoming?.adapter = listAdapter

            AppUtils.logDebug(TAG,"Data REsponse : ")

            if(routes.isNullOrEmpty()) {
                rlNoData.visibility =  View.VISIBLE
            }else{
                rlNoData.visibility =  View.GONE
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
        AppUtils.logDebug(TAG,"Assign id---${routes.assignId}")
        startActivity(intent)
    }

    companion object {
        const val TAG = "UpcommingFragment"
    }

    override fun onResume() {


    viewmodel?.onRouteListRequest(requireContext())


        super.onResume()
    }


}