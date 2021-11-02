package com.pbt.cogni.activity.finishTrip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.internal.OnConnectionFailedListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.pbt.cogni.R
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.MapsActivity
import com.pbt.cogni.activity.map.AdapterExpense
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.util.ClickListener
import kotlinx.android.synthetic.main.fragment_finish_maps.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinishTripActivity : AppCompatActivity() , Callback<HttpResponse>, OnConnectionFailedListener,
    ClickListener {

    var mMap: GoogleMap?=null
    val coordinates: ArrayList<LatLng> = ArrayList()
    var start: LatLng?=null
    var end: LatLng?=null
    var markerPoints = ArrayList<Any>()
    var mPolyline: Polyline? = null
    val options = MarkerOptions()
    var recyclerview: RecyclerView?=null

    private val callback = OnMapReadyCallback { googleMap ->
        mMap=googleMap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_trip)

//        val mapFragment = findFragmentById(R.id.finish_mapview) as SupportMapFragment?
//        mapFragment?.getMapAsync(callback)

//        getWayPoint()
//
//
//        recyclerview = findViewById(R.id.finish_recyclerview)
//        recyclerview?.layoutManager = LinearLayoutManager(this)
//        var listAdapter = AdapterExpense(MapsActivity.list,this)
//        recyclerview?.adapter = listAdapter

//        if(param3.isNullOrEmpty()){
//            view.finish_totalmoney.visibility=View.VISIBLE
//            recyclerview?.visibility=View.VISIBLE
//            view.origin.setText("Startlocation   "+"Ahmedabad")
//            view.destination.setText("Endlocation     "+"Nadiad")
//        }
//        else {
////            origin.setText("Startlocation---" + param1)
////            destination.setText("Endlocation  ---" + param2)
//        }

    }

    private fun getWayPoint() {
        ApiClient.client.create(ApiInterface::class.java).getWayPoint("57").enqueue(this)
    }


    override fun onResponse(call: Call<HttpResponse>, response: Response<HttpResponse>) {
        TODO("Not yet implemented")
    }

    override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onItemClick(position: Int, v: View?) {
        TODO("Not yet implemented")
    }
}