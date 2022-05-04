package com.pbt.cogni.fragment.finishTrip

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pbt.cogni.R
import com.pbt.cogni.activity.MapsActivity
import com.pbt.cogni.fragment.ViewRoute.AdapterViewRouteList
import com.pbt.cogni.model.Routes
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.RoutesViewRecyclerViewItemClick
import kotlinx.android.synthetic.main.fragment_current.rlCurrentNoData
import kotlinx.android.synthetic.main.fragment_finish_trip.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FinishTripFragment : Fragment(), RoutesViewRecyclerViewItemClick {

    lateinit var listAdapter: AdapterViewRouteList
    var viewmodel: FinishTripViewModel ? = null
    var noDataLayout:RelativeLayout?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_finish_trip, container, false)
   noDataLayout=view.findViewById(R.id.rlfinishNoData)
        initViewModel()
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Finish"

    }


    private fun initViewModel() {
         viewmodel =
            ViewModelProvider(this).get(FinishTripViewModel::class.java)

viewmodel!!.notDataLayout=noDataLayout

            viewmodel?.onRouteListRequest(requireContext())


        viewmodel?.routesList?.observe(viewLifecycleOwner, Observer { routes ->
            if (!routes.isNullOrEmpty()){
                progresbar_finish.visibility=View.GONE
            }
            else{
                progresbar_finish.visibility=View.GONE

            }
            recyclerViewFinishTrip?.layoutManager = LinearLayoutManager(requireContext())
            listAdapter = AdapterViewRouteList(routes, this)
            recyclerViewFinishTrip?.adapter = listAdapter

        })
    }

    companion object {

        private const val TAG = "FinishTrip"

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

    override fun onResume() {

            viewmodel?.onRouteListRequest(requireContext())




       AppUtils.logDebug(TAG, "onResume of FinishTrip")
        super.onResume()
    }
}
