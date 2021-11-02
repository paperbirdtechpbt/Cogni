package com.pbt.cogni.fragment.Finish

import android.graphics.Color
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.internal.OnConnectionFailedListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.pbt.cogni.R
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.MapsActivity
import com.pbt.cogni.activity.MapsActivity.Companion.list
import com.pbt.cogni.activity.TabLayout.TabLayoutFragment
import com.pbt.cogni.activity.map.AdapterExpense

import com.pbt.cogni.model.BaseRoutLatLng
import com.pbt.cogni.model.Expense
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.util.ClickListener
import kotlinx.android.synthetic.main.fragment_finish_maps.*
import kotlinx.android.synthetic.main.fragment_finish_maps.view.*
import kotlinx.android.synthetic.main.fragment_tab_layout.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinishMapsFragment : Fragment() , Callback<HttpResponse>, OnConnectionFailedListener,
    ClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private var param3: String? = null

    var mMap:GoogleMap?=null
    val coordinates: ArrayList<LatLng> = ArrayList()
    var start:LatLng?=null
    var end:LatLng?=null
    var markerPoints = ArrayList<Any>()
    var mPolyline: Polyline? = null
    val options = MarkerOptions()
    var recyclerview:RecyclerView?=null

    private val callback = OnMapReadyCallback { googleMap ->
        mMap=googleMap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString("start")
            param2 = it.getString("endddddd")
            param3 = it.getString("tabbbbbbb","")
            Log.d("##########passeddata",param1+"\n"+param2+"\n"+param3)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View= inflater.inflate(R.layout.fragment_finish_maps, container, false)

        getWayPoint()
        addListData()

       recyclerview = view.findViewById(R.id.finish_recyclerview)
        recyclerview?.layoutManager = LinearLayoutManager(context)
        var listAdapter = AdapterExpense(list,this)
        recyclerview?.adapter = listAdapter

        if(param3.isNullOrEmpty()){
            view.finish_totalmoney.visibility=View.VISIBLE
            recyclerview?.visibility=View.VISIBLE
            view.origin.setText("Startlocation   "+"Ahmedabad")
            view.destination.setText("Endlocation     "+"Nadiad")
        }
        else {
            view.origin.setText("Startlocation---" + param1)
            view.destination.setText("Endlocation  ---" + param2)
        }
        return view
    }

    private fun addListData() {
        list.clear()
        list.add(  Expense(
            10,
            "Toll tax receipt",
            "500",
            "https://www.consumercomplaints.in/thumb.php?complaints=2186655&src=51870105.jpg&wmax=900&hmax=900&quality=85&nocrop=1",
            "this ahemedabad toll text reciept"
        ))
        list.add(  Expense(
            10,
            "petrol",
            "500",
            "https://images.financialexpress.com/2018/05/petrol-30-may-2018.jpg",
            "Indian Oil petrol pump gota"
        ))

      val toll=  Expense(10, "Lunch", "500",
          "https://www.moneyunder30.com/images/2017/01/save_receipt.jpeg",
            " Dominos pizza"
        )
        for (i in 0 until 3 ){
            list.add(toll)
        }

    }

    private fun getWayPoint() {
        Log.d("##inmethod","ingetwaypoint")
        ApiClient.client.create(ApiInterface::class.java).getWayPoint("57").enqueue(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.finish_mapview) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onResponse(call: Call<HttpResponse>, response: Response<HttpResponse>) {
        Log.d("##myresponse", response.isSuccessful.toString())
        if (response.body()?.code == false) {
            val listLatLong: BaseRoutLatLng =
                Gson().fromJson(response.body()?.data.toString(), BaseRoutLatLng::class.java)

            Log.d("##myresponse", listLatLong.listLatLng.toString())


            coordinates.clear()
            markerPoints.clear()
            listLatLong.listLatLng.forEach {
                val latitude = it.lat
                val longitude = it.long
                coordinates.add(LatLng(latitude, longitude))
            }

            start=LatLng(coordinates.get(0).latitude,coordinates.get(0).longitude)
            end=LatLng(coordinates.get(coordinates.lastIndex).latitude,
                coordinates.get(coordinates.lastIndex).longitude)

            Log.d("####points","${start.toString()}"+"\n"+end.toString())
            markerPoints.add(start!!)
            markerPoints.add(end!!)
            addRouteOnMap()

    }
    }

    private fun addRouteOnMap() {
        val lineOptions = PolylineOptions()
        for (i in 0 until coordinates.size) {
            lineOptions.add(coordinates.get(i))
            lineOptions.width(9f)
            lineOptions.color(Color.BLUE)
        }

        setMarkerPoints(start!!,true)
        setMarkerPoints(end!!,false)

        options.icon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_RED
            )
        )
        mMap?.addMarker(options)


        if (lineOptions != null) {
            if (mPolyline != null) {
                mPolyline!!.remove()
            }
         mPolyline =mMap?.addPolyline(lineOptions)

            val builder = LatLngBounds.Builder()
            for (marker in markerPoints) {
                builder.include(marker as LatLng)
            }
            val bounds = builder.build()
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, 150)
            mMap?.animateCamera(cu)
        }
    }

    private fun setMarkerPoints(latLng: LatLng,check:Boolean) {
        markerPoints.add(latLng)
        options.position(latLng)
        if (check)
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        else
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        mMap?.addMarker(options)
    }

    override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
       Toast.makeText(context,"NO routes found ", Toast.LENGTH_SHORT).show()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onItemClick(position: Int, v: View?) {

    }
}