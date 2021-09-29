package com.pbt.cogni.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.internal.OnConnectionFailedListener

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.pbt.cogni.activity.map.PlaceAutoSuggestAdapter
import com.pbt.cogni.activity.map.Data
import com.pbt.cogni.activity.map.ResponseDataClass
import com.pbt.cogni.Parse.DirectionsJSONParser
import com.pbt.cogni.R
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.Fragements.ViewRoute.MyRoutesDataClass
import com.pbt.cogni.activity.MapsActivity.Companion.mMap
import com.pbt.cogni.activity.MapsActivity.Companion.mPolyline
import com.pbt.cogni.util.AppConstant.VEW_ROUT
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.HashMap

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnConnectionFailedListener {


    private var markerPoints = ArrayList<Any>()
    private var origin: LatLng? = null
    private var dest: LatLng? = null
    private val options = MarkerOptions()
    private var apiInterface: ApiInterface? = null
    private var startcity: String? = ""
    private var endcity: String? = ""
    private var startcitylatlong: LatLng? = null
    private var endcitylatlng: LatLng? = null
    private var startlocation: AutoCompleteTextView? = null
    private var endlocation: AutoCompleteTextView? = null
    private var seachbutton: Button? = null
    private var bottomNavigation: BottomNavigationView? = null

    companion object {
        var mMap: GoogleMap? = null
        var mPolyline: Polyline? = null
        val coordinates: ArrayList<LatLng> = ArrayList()
        var startpoint: LatLng? = null
        var lastpoint: LatLng? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



        drawRoute()


        startlocation = findViewById(R.id.startLocation)
        endlocation = findViewById(R.id.endLocation)
        seachbutton = findViewById(R.id.btn_searLocation)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        bottomNavigation!!.setOnNavigationItemReselectedListener {
            when (it.itemId) {
                R.id.viewroute -> {
//                    Toast.makeText(
//                        applicationContext,
//                        getString("R.string.msg_View_Routes"),
//                        Toast.LENGTH_SHORT
//                    ).show()

                }
                R.id.audiovideo -> {
//                    Toast.makeText(
//                        applicationContext,
//                        getString(R.string.msg_audio_video),
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
                R.id.chat -> {
//                    Toast.makeText(
//                        applicationContext,
//                        getString(R.string.msg_chats),
//                        Toast.LENGTH_SHORT
//                    ).show()

                }

            }
        }

        if (startlocation!!.text.isEmpty()) {

            markerPoints.clear()
        }
        setAdapterForPredictions()


        startlocation?.setOnItemClickListener { parent, view, position, id ->

            if (markerPoints.size > 1) {
                markerPoints.clear()
                mMap?.clear()
            }

            val latLng = getLatLngFromAddress(startlocation?.text.toString())
            startcitylatlong = latLng
            setMarker(latLng!!)


            if (latLng != null) {
                val address = getAddressFromLatLng(latLng)
                startcity = address?.subAdminArea
            }
        }


        endlocation?.setOnItemClickListener { parent, view, position, id ->

            var latLng = getLatLngFromAddress(endlocation!!.text.toString())
            endcitylatlng = latLng
            setMarker(latLng!!)


            if (latLng != null) {
                val address = getAddressFromLatLng(latLng)
                endcity = address?.subAdminArea
            }
        }


        seachbutton?.setOnClickListener {

            if (startlocation!!.text.isEmpty() && endlocation!!.text.isEmpty()) {
                Toast.makeText(
                    this,
                    "R.string.msg_Please_select_start_and_end_location",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                if (markerPoints.size >= 2) {
                    origin = markerPoints[0] as LatLng
                    dest = markerPoints[1] as LatLng
//                    callApi()
                }
            }

        }
    }

    private fun setAdapterForPredictions() {
        endlocation?.setAdapter(
            PlaceAutoSuggestAdapter(
                this,
                android.R.layout.simple_list_item_1
            )
        )
        startlocation?.setAdapter(
            PlaceAutoSuggestAdapter(
                this,
                android.R.layout.simple_list_item_1
            )
        )
    }

    private fun callApi() {
        apiInterface = ApiClient.client.create(ApiInterface::class.java)

        val call: Call<ResponseDataClass> = apiInterface!!.sentOriginDest(startcity!!, endcity!!)

        call.enqueue(object : Callback<ResponseDataClass> {
            override fun onResponse(
                call: Call<ResponseDataClass>,
                response: Response<ResponseDataClass>
            ) {

                if (response.body()?.data == null) {
                    Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG)
                        .show()
                } else {
                    hidekeyboard()

                    coordinates.clear()
                    mMap?.clear()


                    val responseDataClass: ResponseDataClass? = response.body()
                    val myResult: Data? =
                        Gson().fromJson(Gson().toJson(responseDataClass!!.data), Data::class.java)

                    saveLatlongPoints(myResult)

                    drawPolyline()
                    startNavigation()

                }
            }

            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable?) {
                Toast.makeText(
                    applicationContext,
                    "getString(R.string.msg_Sorry_No_Routes_Found)",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        })
    }

    private fun startNavigation() {
        val pareseuri = "google.navigation:q=$endcity,$endcity&mode=l"
//                    val gmmIntentUri = Uri.parse("google.navigation:q=Rajkot,Botad&mode=l")
        val gmmIntentUri = Uri.parse(pareseuri)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun drawPolyline() {
        val options = PolylineOptions().width(8f).color(Color.BLUE).geodesic(true)
        for (z in 0 until coordinates.size) {
            val point: LatLng = coordinates.get(z)
            options.add(point)
        }
        mPolyline = mMap?.addPolyline(options)
    }

    private fun saveLatlongPoints(myResult: Data?) {
        myResult?.mydata?.forEach {
            val latitude = it.lat
            val longitude = it.lng
            coordinates.add(LatLng(latitude, longitude))
        }
        startpoint = LatLng(coordinates.get(0).latitude, coordinates.get(0).longitude)
        lastpoint = LatLng(
            coordinates.get(coordinates.lastIndex).latitude,
            coordinates.get(coordinates.lastIndex).longitude
        )
        setMarkerPoints(startpoint!!, lastpoint!!)
    }

    private fun setMarkerPoints(start: LatLng, end: LatLng) {
        if (startpoint == start) {
            markerPoints.clear()
            mMap?.clear()
            markerPoints.add(start)
            options.position(start)
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            mMap?.addMarker(options)
            if (lastpoint == end) {
                markerPoints.add(end)
                options.position(end)
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                mMap?.addMarker(options)
            }
        }

        val builder = LatLngBounds.Builder()

        for (marker in markerPoints) {
            builder.include(marker as LatLng)
        }
        val bounds = builder.build()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, 450)
        mMap?.animateCamera(cu)
    }

    private fun hidekeyboard() {
        val inputManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.SHOW_FORCED
        )
    }

    private fun getAddressFromLatLng(latLng: LatLng): Address? {
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        return try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5)
            addresses?.get(0)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun setMarker(latLng: LatLng) {
        markerPoints.add(latLng)

        options.position(latLng)

        if (markerPoints.size == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        } else if (markerPoints.size == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        }
        mMap?.addMarker(options)
    }

    private fun getLatLngFromAddress(address: String): LatLng? {
        val geocoder = Geocoder(this)
        val addressList: List<Address>?
        return try {
            addressList = geocoder.getFromLocationName(address, 1)
            if (addressList != null) {
                val singleaddress = addressList[0]
                LatLng(singleaddress.latitude, singleaddress.longitude)
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        mMap?.setOnMapClickListener {
            if (markerPoints.size > 1) {
                markerPoints.clear()
                mMap?.clear()
            }

            markerPoints.add(it)

            options.position(it)
            if (markerPoints.size == 1) {

                val addresses = getAddressFromLatLng(it)

                Log.d("adresss", "----" + addresses)
                val state = addresses?.adminArea
                val socityr = addresses?.premises
                val area = addresses?.subLocality
                startcity = addresses?.locality
                val district = addresses?.subAdminArea
                val country = addresses?.countryName
                var add = ""
                Log.d(
                    "Myaddress", "start---$state \n " +
                            "society---$socityr  \n   " +
                            "area---$area \n  ciyt---$startcity \n" +
                            "distric----$district  \n country--$country"
                )

                if (socityr != null) {
                    add += socityr + ", "

                }

                if (area != null) {
                    add += area + ", "

                }
                if (startcity != null) {
                    add += startcity + ", "

                }
                if (district != null) {
                    add += district + ", "

                }
                if (state != null) {
                    add += state + ", "

                }
                if (country != null) {
                    add += country + ", "

                }
                if (country == null) {
                    add += "Sea"
                }



                startlocation?.setText(add)

                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))


            } else if (markerPoints.size == 2) {

                val addresses = getAddressFromLatLng(it)
                val state = addresses?.adminArea
                val socityr = addresses?.premises
                val area = addresses?.subLocality

                endcity = addresses?.locality
                val country = addresses?.countryName
                var add = ""
                if (socityr != null) {
                    add += socityr + ", "

                }
                if (area != null) {
                    add += area + ", "

                }
                if (endcity != null) {
                    add += endcity + ", "

                }
                if (state != null) {
                    add += state + ", "

                }
                if (country == null) {
                    add += "Sea"
                }
                if (country != null) {
                    add += country + ", "

                }
                endlocation?.setText(add)
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

            }

            mMap?.addMarker(options)

            if (markerPoints.size >= 2) {
                origin = markerPoints[0] as LatLng
                dest = markerPoints[1] as LatLng


                val builder = LatLngBounds.Builder()
                for (marker in markerPoints) {
                    builder.include(marker as LatLng)
                }
                val bounds = builder.build()
                val cu = CameraUpdateFactory.newLatLngBounds(bounds, 450)
                mMap?.animateCamera(cu)


//                drawRoute()
            }

        }
    }

    private fun drawRoute() {


//        val
//
//
//        val url: String = getDirectionsUrl(data., mydesti!!)
//        Log.d("urlll", url)
//        val downloadTask = DownloadTask()
//
//        downloadTask.execute(url)
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {

        val str_originn = "origin=" + origin.latitude + "," + origin.longitude
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
//        val waypointssss = "waypoints=" + "via:22.7788%2C73.6143%7Cvia:23.686720%2C73.383644"
        val key = "key=" + getString(R.string.google_maps_key)
        val parameters = "$str_originn&$str_dest&$key"
        val output = "json"
        val urll = "https://maps.googleapis.com/maps/api/directions/$output?$parameters"


        val jsonRequest = JsonObjectRequest(Request.Method.GET, urll, null, { response ->
            Log.d("onresponse", "Success Response----googleAPi" + response)
        })

        { error ->
            Log.d("onresponse", "Failure Responser-----googleAPi")
            error.printStackTrace()
        }

        Volley.newRequestQueue(this).add(jsonRequest)
        return urll

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }
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
            val points: ArrayList<LatLng?>? = ArrayList()
            var lineOptions: PolylineOptions? = PolylineOptions()

            for (i in result!!.indices) {


                val path = result[i]


                for (j in path.indices) {
                    val point = path[j]
                    val lat = point["lat"]!!.toDouble()
                    val lng = point["lng"]!!.toDouble()
                    val position = LatLng(lat, lng)
                    points?.add(position)
                }
                lineOptions?.addAll(points)
                lineOptions?.width(9f)
                lineOptions?.color(Color.BLUE)
            }

            if (lineOptions != null) {
                if (mPolyline != null) {
                    mPolyline!!.remove()
                }
                mPolyline = mMap?.addPolyline(lineOptions)
            }

        }

    }
}
