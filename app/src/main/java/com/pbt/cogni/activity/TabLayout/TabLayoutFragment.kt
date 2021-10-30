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
import com.pbt.cogni.fragment.Current.CurrentFragment
import com.pbt.cogni.fragment.Finish.FinishMapsFragment
import com.pbt.cogni.fragment.Upcoming.UpcomingFragment
import kotlinx.android.synthetic.main.fragment_tab_layout.*


class TabLayoutFragment : Fragment() {
    companion object{
        var tabLayout:TabLayout? = null
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

       fragment = CurrentFragment()
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
                    0 -> fragment = CurrentFragment()
                    1 -> fragment = UpcomingFragment()
                    2 -> fragment = FinishMapsFragment()

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


}