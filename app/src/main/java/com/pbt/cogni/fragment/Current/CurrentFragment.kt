package com.pbt.cogni.fragment.Current

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pbt.cogni.R
import com.pbt.cogni.activity.MapsActivity
import com.pbt.cogni.fragment.Current.CurrentTripViewModel.Companion.handler
import com.pbt.cogni.fragment.ViewRoute.AdapterViewRouteList
import com.pbt.cogni.model.Routes
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.RoutesViewRecyclerViewItemClick
import kotlinx.android.synthetic.main.fragment_current.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CurrentFragment : Fragment(), RoutesViewRecyclerViewItemClick{

    private var param1: String? = null
    private var param2: String? = null

    lateinit var listAdapter: AdapterViewRouteList

    var viewmodel: CurrentTripViewModel ? = null
    var notData:RelativeLayout?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_current, container, false)
notData=view.findViewById(R.id.rlCurrentNoData)
            initViewModel()



        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Current"

    }



    private  fun initViewModel() {

         viewmodel = ViewModelProvider(this).get(CurrentTripViewModel::class.java)


      viewmodel?.onRouteListRequest(requireContext())

        viewmodel!!.relativelayout=notData



        viewmodel?.routesList?.observe(viewLifecycleOwner, Observer { routes ->
            recyclerViewCurrentTrip?.layoutManager = LinearLayoutManager(requireContext())
            listAdapter = AdapterViewRouteList(routes, this)
            recyclerViewCurrentTrip?.adapter = listAdapter

            if(!routes.isNullOrEmpty()) {
           viewmodel!!.fetchlocation(requireContext())


            }
//            else{
//
//                notData?.visibility =  View.GONE
//            }
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
        viewmodel?.routesList?.value = emptyList()
        listAdapter.notifyDataSetChanged()

        viewmodel?.onRouteListRequest(requireContext())

        AppUtils.logDebug(TAG, "onResume of CurrentTrip")
        super.onResume()
    }
}