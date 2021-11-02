package com.pbt.cogni.fragment.finishTrip

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pbt.cogni.R
import com.pbt.cogni.fragment.Current.CurrentFragment
import com.pbt.cogni.fragment.Current.CurrentTripViewModel
import com.pbt.cogni.fragment.ViewRoute.AdapterViewRouteList
import com.pbt.cogni.model.Routes
import com.pbt.cogni.util.RoutesViewRecyclerViewItemClick
import kotlinx.android.synthetic.main.fragment_current.*
import kotlinx.android.synthetic.main.fragment_current.rlCurrentNoData
import kotlinx.android.synthetic.main.fragment_finish_trip.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FinishTripFragment : Fragment(), RoutesViewRecyclerViewItemClick {

    lateinit var listAdapter: AdapterViewRouteList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_finish_trip, container, false)

        return view
    }


    private fun initViewModel() {
        val viewmodel: FinishTripViewModel =
            ViewModelProvider(this).get(FinishTripViewModel::class.java)

        viewmodel.onRouteListRequest(requireContext())

        viewmodel?.routesList.observe(viewLifecycleOwner, Observer { routes ->
            recyclerViewFinishTrip?.layoutManager = LinearLayoutManager(requireContext())
            listAdapter = AdapterViewRouteList(routes, this)
            recyclerViewFinishTrip?.adapter = listAdapter

            if (routes.isNullOrEmpty()) {
                rlCurrentNoData.visibility = View.VISIBLE
            } else {
                rlCurrentNoData.visibility = View.GONE
            }
        })
    }

    companion object {

        private const val TAG = "FinishTrip";

    }

    override fun onRecyclerViewItemClick(view: View, routes: Routes) {

    }
}
