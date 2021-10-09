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
import com.pbt.cogni.R
import org.apache.http.HttpResponse


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AudioVideoFragement() : Fragment() {
    var viewModel: AudioVideoViewModel? = null
    val context  = activity


    val listAnalyst = ArrayList<HttpResponse>()
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

        initViewModel()
        initRecyclerView()




        return view
    }

    private fun initRecyclerView() {
        recyclerViewAnalyst?.layoutManager = LinearLayoutManager(requireContext())
        audioVideCallAdapter = AudioVideCallAdapter(requireContext()) { i, view ,result,sendername->
            when (view.id) {
                R.id.rlVideoCall -> {
                   AudioVideoViewModel().sendCall(true,result.id.toString(),sendername,requireContext())
                }
                R.id.rlVoiceCall -> {
                    AudioVideoViewModel().sendCall(false,result.id.toString(),sendername,requireContext())
                }
            }
        }

        recyclerViewAnalyst?.adapter = audioVideCallAdapter
    }
    private fun initViewModel() {
        val viewmodel: AudioVideoViewModel =
            ViewModelProvider(this).get(AudioVideoViewModel::class.java)
        viewmodel.getLiveDataObserver()?.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                audioVideCallAdapter?.setCountryList(it)
                audioVideCallAdapter?.notifyDataSetChanged()
            }
        })
        viewmodel.oncall()
    }

    }




