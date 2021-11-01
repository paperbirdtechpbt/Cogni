package com.pbt.cogni.fragment.Upcoming

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pbt.cogni.R
import com.pbt.cogni.activity.TabLayout.TabLayoutFragment
import com.pbt.cogni.fragment.Current.CurrentFragment
import com.pbt.cogni.model.Users
import com.pbt.cogni.util.RecyclerviewClickLisetner
import kotlinx.android.synthetic.main.fragment_upcoming.*

import com.pbt.cogni.fragment.Finish.FinishMapsFragment


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class UpcomingFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    var upcomingeventes=ArrayList<UpcomingEvents>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View= inflater.inflate(R.layout.fragment_upcoming, container, false)

        addData(upcomingeventes)

        val recyclerView=view.findViewById<RecyclerView>(R.id.upcoming_recyclerview)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        val listAdapter = AdapterUpcomingList(upcomingeventes,{list->

            val fragment = FinishMapsFragment()
            val args = Bundle()
            args.putString("start",list.startlocation)
            args.putString("endddddd",list.endlocation)
            args.putString("tabbbbbbb","2")

            fragment.setArguments(args)

           sendDataForTabLayoutChane()

            var fragmentTransaction: FragmentTransaction

            val  fragmentManager = requireActivity().supportFragmentManager
            fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayout_tab, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

            val intent=Intent(requireContext(),TabLayoutFragment::class.java)
            intent.putExtra("tablayout","channge")

        })
        recyclerView?.adapter = listAdapter

        return  view
    }

    private fun addData(upcomingeventes: ArrayList<UpcomingEvents>) {

        upcomingeventes.add(UpcomingEvents("01-11-21","AHmedabad","Pune"))
        upcomingeventes.add(UpcomingEvents("02-11-21","Botad","Rajkot"))
        upcomingeventes.add(UpcomingEvents("02-11-21","Botad","Rajkot"))
        upcomingeventes.add(UpcomingEvents("03-11-21","Updaipur","Bhavngar"))
        upcomingeventes.add(UpcomingEvents("04-11-21","Bhavnagr","Pune"))
        upcomingeventes.add(UpcomingEvents("04-11-21","Bhavnagr","Pune"))
        upcomingeventes.add(UpcomingEvents("05-11-21","Botad","Pune"))
        upcomingeventes.add(UpcomingEvents("06-11-21","Delhi","Pune"))
        upcomingeventes.add(UpcomingEvents("04-11-21","Bhavnagr","Pune"))
    }

    private fun sendDataForTabLayoutChane() {
        val fragment = TabLayoutFragment()
        val args = Bundle()
        args.putString("tabchange","2")
        fragment.setArguments(args)
    }


//    companion object {
//
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            UpcomingFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }


}