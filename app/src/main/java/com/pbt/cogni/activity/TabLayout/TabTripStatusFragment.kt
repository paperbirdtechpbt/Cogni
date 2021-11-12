package com.pbt.cogni.activity.TabLayout


import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentTransaction

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout
import com.pbt.cogni.R
import com.pbt.cogni.activity.home.MainActivity
import com.pbt.cogni.fragment.Current.CurrentFragment
import com.pbt.cogni.fragment.Upcoming.UpcomingFragment
import com.pbt.cogni.fragment.finishTrip.FinishTripFragment
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppConstant.Companion.CONST_CHECK_STATUS
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import kotlinx.android.synthetic.main.fragment_tab_layout.*


class TabTripStatusFragment : Fragment() {
    companion object{
        var tabLayout:TabLayout? = null
        const val TAG = "TabTripStatusFragment"
    }
    var frameLayout: FrameLayout? = null
    var fragment: Fragment? = null

    var fragmentTransaction: FragmentTransaction? = null

    private val callback = OnMapReadyCallback { googleMap ->

        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view:View= inflater.inflate(R.layout.fragment_tab_layout, container, false)

        tabLayout = view.findViewById(R.id.tablayout_home)
        frameLayout = view.findViewById(R.id.frameLayout_tab)

       fragment = UpcomingFragment()
      val  fragmentManager = requireActivity().supportFragmentManager
       fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction?.replace(R.id.frameLayout_tab, fragment!!)
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
        tabLayout!!.getTabAt(0)?.getIcon()?.setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_IN);
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.getIcon()?.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)

               when (tab?.position) {
                    0 -> fragment = UpcomingFragment()
                    1 -> fragment = CurrentFragment()
                    2 -> fragment = FinishTripFragment()
                }
                val fm = requireActivity().supportFragmentManager
                val ft = fm.beginTransaction()
                ft.replace(R.id.frameLayout_tab, fragment!!)
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ft.commit()

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.getIcon()?.setColorFilter(Color.parseColor("#D3C8C8"), PorterDuff.Mode.SRC_IN)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onResume() {
        var checkStatus =  MyPreferencesHelper.getStringValue(requireContext(),AppConstant.CONST_CHECK_STATUS,"")
        if(checkStatus?.isEmpty() == false){
            AppUtils.logDebug(TAG," MainActivity Data is Not Empty "+checkStatus)
            if(checkStatus.equals("3")){
                AppUtils.logDebug(TAG," if status is  3  ")
                tabLayout?.getTabAt(2)?.select();
            }else if(checkStatus.equals("2")){
                tabLayout?.getTabAt(1)?.select();
            }
            MyPreferencesHelper.setStringValue(requireContext(),CONST_CHECK_STATUS,"")
        }
        super.onResume()
    }


}