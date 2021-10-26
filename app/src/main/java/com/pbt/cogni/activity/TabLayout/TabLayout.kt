package com.pbt.cogni.activity.TabLayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.GRAVITY_FILL
import com.pbt.cogni.R
import com.pbt.cogni.fragment.Current.CurrentFragment
import com.pbt.cogni.fragment.Finish.FinishFragment
import com.pbt.cogni.fragment.Upcoming.UpcomingFragment
import kotlinx.android.synthetic.main.activity_tab_layout.*

class TabLayout : AppCompatActivity() {

    var tabLayout: com.google.android.material.tabs.TabLayout? = null
    var frameLayout: FrameLayout? = null
    var fragment: Fragment? = null
    var fragmentManager: FragmentManager? = null
    var fragmentTransaction: FragmentTransaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_layout)

        supportActionBar?.hide()

        tabLayout = findViewById(R.id.tablayout_home)
        frameLayout = findViewById(R.id.frameLayout_tab)

        fragment = CurrentFragment()
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.frameLayout_tab, fragment!!)
        fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentTransaction!!.commit()

tabLayout!!.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
    override fun onTabSelected(tab: TabLayout.Tab?) {

        when (tab?.position) {
            0 -> fragment = CurrentFragment()
            1 -> fragment = UpcomingFragment()
            2 -> fragment = FinishFragment()

        }
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(R.id.frameLayout_tab, fragment!!)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

})

    }
}