package com.pbt.cogni.fragment.Current

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pbt.cogni.R
import com.pbt.cogni.activity.MapsActivity
import com.pbt.cogni.fragment.ViewRoute.AdapterViewRouteList
import com.pbt.cogni.fragment.ViewRoute.ViewRouteFragementViewModel
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.model.Routes
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.RoutesViewRecyclerViewItemClick
import kotlinx.android.synthetic.main.fragment_current.*
import kotlinx.android.synthetic.main.fragment_upcoming.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CurrentFragment : Fragment(), RoutesViewRecyclerViewItemClick {

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_current, container, false)
        initViewModel()
        return view
    }


    private fun initViewModel() {
        val viewmodel: CurrentTripViewModel = ViewModelProvider(this).get(CurrentTripViewModel::class.java)

        viewmodel.onRouteListRequest(requireContext())

        viewmodel?.routesList.observe(viewLifecycleOwner, Observer { routes ->
            recyclerViewCurrentTrip?.layoutManager = LinearLayoutManager(requireContext())
            listAdapter = AdapterViewRouteList(routes, this)
            recyclerViewCurrentTrip?.adapter = listAdapter

            if(routes.isNullOrEmpty()) {
                rlCurrentNoData.visibility =  View.VISIBLE
            }else{
                rlCurrentNoData.visibility =  View.GONE
            }
        })
    }

    companion object {

        private const val TAG = "CurrentFragment";
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CurrentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onRecyclerViewItemClick(view: View, routes: Routes) {

        val intent = Intent(activity, MapsActivity::class.java)
        intent.putExtra(AppConstant.CONST_TO_ADDRESS, routes.StartLocation)
        intent.putExtra(AppConstant.CONST_FROM_ADDRESS, routes.EndLocation)
        intent.putExtra(AppConstant.CONST_STATUS, routes.status)
        AppUtils.logDebug(TAG,"Status : "+ routes.status)

        intent.putExtra(AppConstant.CONST_TO_ORIGIN_LAT, routes.startLat.toDouble())
        intent.putExtra(AppConstant.CONST_TO_ORIGIN_LONG, routes.startLong.toDouble())
        intent.putExtra(AppConstant.CONST_TO_DESTINATION_LAT, routes.endLat.toDouble())
        intent.putExtra(AppConstant.CONST_TO_DESTINATION_LONG, routes.endLong.toDouble())
        intent.putExtra(AppConstant.CONST_ROUTE_ID, routes.MSTRouteId)
        intent.putExtra(AppConstant.CONST_ASSIGN_ID, routes.assignId)

        startActivity(intent)
    }
}