    package com.pbt.cogni.fragment.audioVideoCall

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.pbt.cogni.R
import com.pbt.cogni.fragment.audioVideoCall.AudioVideoViewModel.Companion.myResult
import com.pbt.cogni.model.UserDetailsData
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import kotlinx.android.synthetic.main.fragment_audio_video_fragement.*


    class AudioVideoFragement() : Fragment() {

    var viewModel: AudioVideoViewModel? = null
    val context  = activity
    var audioVideCallAdapter: AudioVideCallAdapter? = null
    var recyclerViewAnalyst: RecyclerView? = null
    var TAG="AudioVideoFragement"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_audio_video_fragement, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        recyclerViewAnalyst = view.findViewById(R.id.recyclerAnalyst)

        initViewModel()
        initRecyclerView()

        return view
    }

    private fun initRecyclerView() {
        recyclerViewAnalyst?.layoutManager = LinearLayoutManager(requireContext())
        audioVideCallAdapter = AudioVideCallAdapter(requireContext()) { i, view ,result,sendername->
            val userdataDetails: UserDetailsData? = MyPreferencesHelper.getUser(requireContext())

            when (view.id) {
                R.id.rlVideoCall -> {
                    val roomid=getRandomString(7)
//                    val userdataDetails: UserDetailsData? = MyPreferencesHelper.getUser(requireContext())
                    AppUtils.logDebug(TAG,"userDetails==>"+ Gson().toJson(userdataDetails).toString())
                    AppUtils.logDebug(TAG,"myResult==>"+ myResult)
                    myResult?.mydata?.get(i)?.name.toString()

                   AudioVideoViewModel().sendCall(requireContext(),
                       userdataDetails?.id.toString(), myResult?.mydata?.get(i)?.id.toString(),
                       roomid,"StartMeeting","video")

                }
//                R.id.test_jitsi_videCall ->{
//                                        AudioVideoViewModel().JitsiCall(
//                                            requireContext())
//
//                }
                R.id.rlVoiceCall -> {
                    val roomid=getRandomString(7)
                    AudioVideoViewModel().sendCall(requireContext(),
                        userdataDetails?.id.toString(), myResult?.mydata?.get(i)?.id.toString(),
                        roomid,"StartMeeting","audio")
                }
            }
        }
        recyclerViewAnalyst?.adapter = audioVideCallAdapter
    }
    private fun initViewModel() {
        val viewmodel: AudioVideoViewModel =
            ViewModelProvider(this).get(AudioVideoViewModel::class.java)
        viewmodel.getLiveDataObserver().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                progressbar_audiovideo.visibility=View.GONE
                audioVideCallAdapter?.setCountryList(it)
                audioVideCallAdapter?.notifyDataSetChanged()
            }
        })
        viewmodel.oncall(requireContext())
    }
    fun getRandomString(length: Int) : String {
//        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        val allowedChars = ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    }




