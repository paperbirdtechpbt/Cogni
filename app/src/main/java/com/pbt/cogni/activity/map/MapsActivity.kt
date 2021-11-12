package com.pbt.cogni.activity

import `in`.shadowfax.proswipebutton.ProSwipeButton
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.location.Location
import android.net.Uri
import android.opengl.Visibility
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.internal.OnConnectionFailedListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.pbt.cogni.Parse.DirectionsJSONParser
import com.pbt.cogni.R
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.expense.ExpenseActivity
import com.pbt.cogni.activity.map.AdapterExpense
import com.pbt.cogni.model.*
import com.pbt.cogni.util.AppConstant.Companion.CONST_ASSIGN_ID
import com.pbt.cogni.util.AppConstant.Companion.CONST_CHECK_STATUS
import com.pbt.cogni.util.AppConstant.Companion.CONST_FROM_ADDRESS
import com.pbt.cogni.util.AppConstant.Companion.CONST_ROUTE_ID
import com.pbt.cogni.util.AppConstant.Companion.CONST_START_TRIP
import com.pbt.cogni.util.AppConstant.Companion.CONST_STATUS
import com.pbt.cogni.util.AppConstant.Companion.CONST_STOP_TRIP
import com.pbt.cogni.util.AppConstant.Companion.CONST_TO_ADDRESS
import com.pbt.cogni.util.AppConstant.Companion.CONST_TO_DESTINATION_LAT
import com.pbt.cogni.util.AppConstant.Companion.CONST_TO_DESTINATION_LONG
import com.pbt.cogni.util.AppConstant.Companion.CONST_TO_ORIGIN_LAT
import com.pbt.cogni.util.AppConstant.Companion.CONST_TO_ORIGIN_LONG
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.ClickListener
import com.pbt.cogni.util.Config.BASE_GOOGLE_MAP_ROUTES
import com.pbt.cogni.util.MyPreferencesHelper
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnConnectionFailedListener,
    Callback<HttpResponse>, ClickListener {

    private var startcitylatlng: String? = ""
    private var endcitylatlng: String? = ""
    private var startcity: String? = ""
    private var endcity: String? = ""
    private var startAddress: String? = ""
    private var endAddress: String? = ""
    private var myWaypoint: String? = ""

    companion object {
        var list = ArrayList<Expense>()
        val options = MarkerOptions()
        var markerPoints = ArrayList<Any>()
        var mMap: GoogleMap? = null
        var mPolyline: Polyline? = null
        val coordinates: ArrayList<LatLng> = ArrayList()
        var TAG: String = "MapsActivity"
        var startLat: Double = 00.00
        var startLong: Double = 00.00
        var endLat: Double = 00.00
        var endLong: Double = 00.00
        var routeId: String = ""
        var status: String = ""
        var assignId: String = ""

        var floatButton: FloatingActionButton? = null

    }

    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        markerPoints.clear()
        coordinates.clear()

           runOnUiThread {


                   getExpense()

           }


        floatButton = findViewById(R.id.floating_action_button)
        bottomSheet.visibility=View.GONE




        btnBack?.setOnClickListener {
            onBackPressed()
        }


        floatButton?.setOnClickListener {
            var uri = "$myWaypoint"
            val navigation = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=" + "$startcitylatlng,$endcitylatlng" + "&" + uri)
            )
            navigation.setPackage("com.google.android.apps.maps")
            startActivity(navigation)
        }

        floatingAddExpense?.setOnClickListener {
            var intent = Intent(this, ExpenseActivity::class.java)
            intent.putExtra(CONST_ROUTE_ID, routeId)
            startActivity(intent)
        }


        btnStartTrip.setOnSwipeListener(object : ProSwipeButton.OnSwipeListener {
            override fun onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                Handler().postDelayed(Runnable {

                    btnStartTrip.showResultIcon(true) // false if task failed

                     updateTripStus(CONST_START_TRIP)

                }, 2000)
            }
        })

//        btnStartTrip.setOnClickListener {
//            updateTripStus(CONST_START_TRIP)
//        }
        btnEndTrip.setOnSwipeListener(object : ProSwipeButton.OnSwipeListener {
            override fun onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                Handler().postDelayed(Runnable {

                    btnStartTrip.showResultIcon(true) // false if task failed

                        updateTripStus(CONST_STOP_TRIP)


                }, 2000)
            }
        })

//        btnEndTrip.setOnClickListener {
//            updateTripStus(CONST_STOP_TRIP)
//        }





        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        startAddress = intent.getStringExtra(CONST_TO_ADDRESS)
        endAddress = intent.getStringExtra(CONST_FROM_ADDRESS)

        origin.text = startAddress
        destination.text = endAddress

        startLat = intent.getDoubleExtra(CONST_TO_ORIGIN_LAT, 00.00)
        startLong = intent.getDoubleExtra(CONST_TO_ORIGIN_LONG, 00.00)
        endLat = intent.getDoubleExtra(CONST_TO_DESTINATION_LAT, 00.00)
        endLong = intent.getDoubleExtra(CONST_TO_DESTINATION_LONG, 00.00)
        routeId = intent.getStringExtra(CONST_ROUTE_ID)
        status = intent.getStringExtra(CONST_STATUS)
        assignId = intent.getStringExtra(CONST_ASSIGN_ID)
        if (status=="1"){
            from(bottomSheet).apply {
                peekHeight = 0
                this.state = STATE_COLLAPSED
            }
        }
        else{
            from(bottomSheet).apply {
            peekHeight = 80
            this.state = STATE_COLLAPSED

        }}

        AppUtils.logDebug(TAG, "routes status : " + status)

        if (status.equals(CONST_START_TRIP)) {
            //in currnet trip fragment
            floatingAddExpense.visibility = View.VISIBLE
            floating_action_button.visibility = View.VISIBLE


                    getExpense()



            btnEndTrip.visibility = View.VISIBLE
            btnStartTrip.visibility = View.GONE
        }
        else if(status.equals(CONST_STOP_TRIP)){
            bottomSheet.visibility=View.GONE
            btnStartTrip.visibility = View.GONE
            btnEndTrip.visibility = View.GONE
        }

    getWayPoint()





    }

       fun getExpense() {

        AppUtils.logDebug(TAG, " routeId  : " + routeId)
        val apiclient = ApiClient.getClient()
        val apiInterface = apiclient?.create(ApiInterface::class.java)
        val call = apiInterface?.getExpense(routeId)

        call?.enqueue(object : retrofit2.Callback<HttpResponse> {
            override fun onResponse(call: Call<HttpResponse>, response: Response<HttpResponse>) {
                AppUtils.logDebug(TAG, " response  : " + response.body())
                try {
                    var listLatLong: BaseExpense =
                        Gson().fromJson(response?.body()?.data.toString(), BaseExpense::class.java)
                    recyclerViewExpense?.layoutManager = LinearLayoutManager(applicationContext)
                    var listAdapter = AdapterExpense(listLatLong.listExpense, this@MapsActivity)
                    recyclerViewExpense.adapter = listAdapter
                    bottomSheet.visibility = View.VISIBLE
                    if (!listLatLong.listExpense.isNullOrEmpty()) {
//                        toStartStopRoutes()
                        bottomSheet.visibility = View.VISIBLE
                        imgNoData.visibility = View.GONE
                        txtNoData.visibility = View.GONE
//                        floatingAddExpense.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    AppUtils.logError(TAG, "Network Error : " + e.message)
                }

            }

            override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
                AppUtils.logError(TAG, " server Error: " + t.message)
            }

        })
    }

     fun updateTripStus(status: String) {

        val apiclient = ApiClient.getClient()
        val apiInterface = apiclient?.create(ApiInterface::class.java)
        val call = apiInterface?.updateTripStatus(assignId, status)

        call?.enqueue(object : retrofit2.Callback<HttpResponse> {
            override fun onResponse(call: Call<HttpResponse>, response: Response<HttpResponse>) {
                AppUtils.logDebug(TAG, "Status Response : " + response.body().toString())
                btnStartTrip.visibility = View.GONE
                bottomSheet.visibility = View.VISIBLE
//                floatingAddExpense.visibility = View.VISIBLE

                if(CONST_STOP_TRIP.equals(status)){
                    MyPreferencesHelper.setStringValue(this@MapsActivity,CONST_CHECK_STATUS,status)
                    finish()
                    Toasty.success(this@MapsActivity, "Trip Finished Successfully", Toasty.LENGTH_SHORT).show()
                }else if(CONST_START_TRIP.equals(status)){
                    floating_action_button.visibility = View.VISIBLE
                    MyPreferencesHelper.setStringValue(this@MapsActivity,CONST_CHECK_STATUS,status)
                }
            }
            override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
                AppUtils.logError(TAG, " server Error: " + t.message)
            }
        })
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (from(bottomSheet)!!.state === STATE_EXPANDED) {
                val outRect = Rect()
                bottomSheet.getGlobalVisibleRect(outRect)
                if (!outRect.contains(
                        event.rawX.toInt(),
                        event.rawY.toInt()
                    )
                ) from(bottomSheet).state = STATE_COLLAPSED
            }
        }
        return super.dispatchTouchEvent(event)
    }

     fun getWayPoint() {

        ApiClient.client.create(ApiInterface::class.java).getWayPoint(routeId).enqueue(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    private fun drawRoute(org: LatLng, dest: LatLng) {
        val url: String = getDirectionsUrl(org, dest)

        val downloadTask = DownloadTask()
        AppUtils.logDebug(TAG, " url  " + url)
        downloadTask.execute(url)
    }

    private fun getDirectionsUrl(org: LatLng, dest: LatLng): String {

//        var origin = LatLng(startLat, startLong)//startLatLng
//        var dest = LatLng(endLat, endLong)//endLatLng
//        val waypointssss = "waypoints=" + "6.140432,-75.185903"
//        val waypointssss = "waypoints=" + "23.0225,72.5714"
        val waypointssss = "waypoints=" + "via:22.7349%2C72.4402"
        startcitylatlng = dest.latitude.toString()
        endcitylatlng = dest.longitude.toString()

        val str_origin = "origin=" + org.latitude + "," + org.longitude
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude


        val key = "key=" + getString(R.string.google_maps_key)

//        "via:22.1723%2C71.6636%7Cvia:23.686720%2C73.383644"\

//        val parameters = "$str_origin&$str_dest&$waypointssss&$key"
        val parameters = "$str_origin&$str_dest&$key"


        val startPoint = Location("locationA")
        startPoint.latitude = org.latitude
        startPoint.longitude = org.longitude

        val endPoint = Location("locationA")
        endPoint.latitude = dest.latitude
        endPoint.longitude = dest.longitude


        return BASE_GOOGLE_MAP_ROUTES + "json?$parameters"

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onResponse(call: Call<HttpResponse>, response: Response<HttpResponse>) {

        if (response?.body()?.code == false) {
            AppUtils.logError(TAG,"onresponse ${response.body()}")

            val listLatLong: BaseRoutLatLng =
                Gson().fromJson(response.body()?.data.toString(), BaseRoutLatLng::class.java)

            if (listLatLong.listLatLng.isNullOrEmpty()){
                Toast.makeText(this,"No routes Found",Toast.LENGTH_LONG).show()
            }
            else{

            listLatLong.listLatLng.forEach {
                val latitude = it.lat
                val longitude = it.long
                coordinates.add(LatLng(latitude, longitude))
            }

            if (!coordinates.isEmpty()){


            var orgn = LatLng(coordinates.get(0).latitude, coordinates.get(0).longitude)
            var dest = LatLng(
                coordinates.get(coordinates.lastIndex).latitude,
                coordinates.get(coordinates.lastIndex).longitude
            )

            var uri = "waypoints="
            var k: Int = 1
            var count: Int = 0
            var size: Int = listLatLong.listLatLng.size / 22
            listLatLong.listLatLng.forEach {
                if (k == 1) {
                    count++
                    val latitude = it.lat
                    val longitude = it.long
                    uri = uri + latitude + "," + longitude
                }
                if (size == k) {
                    k = 1
                }
                k++
            }
            myWaypoint = uri
            drawRoute(orgn, dest)
        }}
        }
    }

    override fun onResume() {


             getExpense()



        super.onResume()
    }

    override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
        AppUtils.logError(TAG, "Error " + t.message)
    }

    override fun onItemClick(position: Int, v: View?) {
        AppUtils.logDebug(TAG, "Item click Call")
    }

    private class DownloadTask : AsyncTask<String?, Void?, String>() {

        override fun doInBackground(vararg url: String?): String {

            var data = ""
            try {
                data = downloadurl(url[0])
            } catch (e: Exception) {
            }
            return data
        }

        private fun downloadurl(strUrl: String?): String {

            var data = ""
            var iStream: InputStream? = null
            var urlConnection: HttpURLConnection? = null

            try {
                val url = URL(strUrl)

                urlConnection = url.openConnection() as HttpURLConnection

                urlConnection.connect()

                iStream = urlConnection.inputStream
                val br = BufferedReader(InputStreamReader(iStream))
                val sb = StringBuffer()
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                data = sb.toString()
                br.close()
            } catch (e: java.lang.Exception) {
                Log.d("Exception on download", e.toString())
            } finally {
                iStream!!.close()
                urlConnection!!.disconnect()
            }
            return data
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val parserTask = ParserTask()
            parserTask.execute(result)
        }

        class ParserTask : AsyncTask<String?, Int?, List<List<HashMap<String, String>>>?>() {

            override fun doInBackground(vararg jsonData: String?): List<List<HashMap<String, String>>>? {
                val jObject: JSONObject
                var routes: List<List<HashMap<String, String>>>? = null
                try {

                    jObject = JSONObject(jsonData[0])
                    val parser = DirectionsJSONParser()
                    routes = parser.parse(jObject)

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                return routes
            }


            override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
                val lineOptions: PolylineOptions? = PolylineOptions()


                for (i in result!!.indices) {
                    AppUtils.logDebug(TAG,"result---$result")

                    lineOptions?.addAll(coordinates)
                    lineOptions?.width(9f)
                    lineOptions?.color(Color.BLUE)
                }

                setMarkerPoints(LatLng(startLat, startLong))
                setMarkerPoints(LatLng(endLat, endLong))

                if (lineOptions != null) {
                    if (mPolyline != null) {
                        mPolyline!!.remove()
                    }
                    mPolyline = mMap?.addPolyline(lineOptions)

                    val builder = LatLngBounds.Builder()
                    for (marker in markerPoints) {
                        builder.include(marker as LatLng)
                    }
                    val bounds = builder.build()
                    val cu = CameraUpdateFactory.newLatLngBounds(bounds, 200)
                    mMap?.animateCamera(cu)
                }

            }

            private fun setMarkerPoints(start: LatLng) {
                markerPoints.add(start)
                MapsActivity.options.position(start)
                MapsActivity.options.icon(
                    BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED
                    )
                )
                mMap?.addMarker(MapsActivity.options)
            }
        }
    }
}
