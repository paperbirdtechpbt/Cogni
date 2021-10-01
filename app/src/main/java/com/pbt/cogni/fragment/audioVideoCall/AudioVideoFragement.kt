package com.pbt.cogni.fragment.audioVideoCall

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.pbt.cogni.R
import com.pbt.cogni.activity.home.MainActivity
import com.pbt.cogni.activity.login.LoginViewModel
import com.pbt.cogni.repository.AnalystRepo
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import es.dmoral.toasty.Toasty


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AudioVideoFragement() : Fragment() {
    var viewModel: AudioVideoViewModel? = null
    val context = Application()


    val listAnalyst = ArrayList<AnalystRepo>()
    var audioVideCallAdapter: AudioVideCallAdapter? = null
    var recyclerViewAnalyst: RecyclerView? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_audio_video_fragement, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        recyclerViewAnalyst = view.findViewById(R.id.recyclerAnalyst)

//        viewModel = ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(context)
//        ).get(AudioVideoViewModel::class.java)
//        addData()
        initViewModel()
        initRecyclerView()




        return view
    }

    private fun initRecyclerView() {
        recyclerViewAnalyst?.layoutManager = LinearLayoutManager(context)
        audioVideCallAdapter = AudioVideCallAdapter(context)
        recyclerViewAnalyst?.adapter = audioVideCallAdapter
    }
    private fun initViewModel() {
        val viewmodel: AudioVideoViewModel =
            ViewModelProvider(this).get(AudioVideoViewModel::class.java)
        viewmodel.  getLiveDataObserver()?.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                audioVideCallAdapter?.setCountryList(it)
                audioVideCallAdapter?.notifyDataSetChanged()
            }
        })
        viewmodel.oncall()
    }
//        viewModel?.audiovideousers?.observe(viewLifecycleOwner, Observer { response ->
//            if (response?.code == false) {
//                Toasty.success(this.requireActivity(),"${response?.message}", Toasty.LENGTH_SHORT).show()
//            } else
//                Toasty.warning(this.requireActivity(), "${response?.message}", Toasty.LENGTH_SHORT).show()
//        })
    }


//    private fun addData() {
//
//
//        listAnalyst.add(
//            AnalystRepo(
//                "Criss",
//                "10225",
//            )
//        )
//        listAnalyst.add(
//            AnalystRepo(
//                "HemsWorth",
//                "10220",
//            )
//        )
//        listAnalyst.add(
//            AnalystRepo(
//                "Tom Cruise",
//                "10231",
//
//                )
//        )
//        listAnalyst.add(
//            AnalystRepo(
//                "Jack",
//                "10232"
//            )
//        )
//        listAnalyst.add(
//            AnalystRepo(
//                "Luca",
//                "10233"
//            )
//        )
//
//        listAnalyst.add(
//            AnalystRepo(
//                "Dywan Tonny",
//                "10234"
//            )
//        )
//
//        listAnalyst.add(
//            AnalystRepo(
//                "Huge Cristien",
//                "10235"
//            )
//        )
//
//        listAnalyst.add(
//            AnalystRepo(
//                "Lucy Daywn",
//                "10238"
//            )
//        )
//
//        listAnalyst.add(
//            AnalystRepo(
//                "James Luce",
//                "10236"
//            )
//        )
//
//        listAnalyst.add(
//            AnalystRepo(
//                "Nick Furry",
//                "10230"
//            )
//        )
//
//
//    }

