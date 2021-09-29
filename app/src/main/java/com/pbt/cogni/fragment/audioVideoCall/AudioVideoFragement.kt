package com.pbt.cogni.fragment.audioVideoCall

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pbt.cogni.R
import com.pbt.cogni.repository.AnalystRepo


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AudioVideoFragement : Fragment() {


    val listAnalyst = ArrayList<AnalystRepo>()
    var audioVideCallAdapter: AudioVideCallAdapter ? = null
    var recyclerViewAnalyst: RecyclerView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_audio_video_fragement, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        recyclerViewAnalyst = view.findViewById(R.id.recyclerAnalyst)

        addData()

        recyclerViewAnalyst?.layoutManager = LinearLayoutManager(context)
        audioVideCallAdapter = AudioVideCallAdapter(listAnalyst, context)
        recyclerViewAnalyst?.setAdapter(audioVideCallAdapter)

        return view
    }


    private fun addData() {


        listAnalyst.add(
            AnalystRepo(
                "Criss",
                "10225",
            )
        )
        listAnalyst.add(
            AnalystRepo(
                "HemsWorth",
                "10220",
            )
        )
        listAnalyst.add(
            AnalystRepo(
                "Tom Cruise",
                "10231",

            )
        )
        listAnalyst.add(
            AnalystRepo(
                "Jack",
                "10232"
            )
        )
        listAnalyst.add(
            AnalystRepo(
                "Luca",
                "10233"
               )
            )

        listAnalyst.add(
            AnalystRepo(
                "Dywan Tonny",
                "10234"
            )
        )

        listAnalyst.add(
            AnalystRepo(
                "Huge Cristien",
                "10235"
            )
        )

        listAnalyst.add(
            AnalystRepo(
                "Lucy Daywn",
                "10238"
            )
        )

        listAnalyst.add(
            AnalystRepo(
                "James Luce",
                "10236"
            )
        )

        listAnalyst.add(
            AnalystRepo(
                "Nick Furry",
                "10230"
            )
        )


    }

}