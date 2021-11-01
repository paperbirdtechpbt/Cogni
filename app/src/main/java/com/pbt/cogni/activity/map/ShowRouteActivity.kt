package com.pbt.cogni.activity.map

import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
import com.google.gson.Gson
import com.pbt.cogni.Parse.DirectionsJSONParser
import com.pbt.cogni.R
import com.pbt.cogni.activity.viewRoute.MyRoutesDataClass
import com.pbt.cogni.activity.map.ShowRouteActivity.Companion.mMap
import com.pbt.cogni.activity.map.ShowRouteActivity.Companion.mPolyline
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.HashMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLngBounds



class ShowRouteActivity : AppCompatActivity(), OnMapReadyCallback, OnConnectionFailedListener {

    companion object {
        var mMap: GoogleMap? = null
        var mPolyline: Polyline? = null
        var startpoint: LatLng? = null
        var lastpoint: LatLng? = null
        var routView:MyRoutesDataClass?=null

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_route)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)





        if(intent.extras!=null){
            routView =intent.getParcelableExtra<MyRoutesDataClass>("user")
        }
//        var routView =Gson().fromJson(intent.extras?.getString(VEW_ROUT), MyRoutesDataClass::class.java)


        Log.e("##Intent", " Lat Long " + routView?.startLatLong)

        val route = routView?.origin + " To " + routView?.destination
        supportActionBar?.setTitle(route)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)


        startpoint = routView?.startLatLong
        lastpoint = routView?.endLatLong

        val url: String = getDirectionsUrl(routView!!.startLatLong, routView!!.endLatLong)
//        val url: String = getDirectionsUrl(startlat,endlat)


        val downloadTask = DownloadTask()

        downloadTask.execute(url)


    }


//    private fun callApi() {
//        apiInterface = ApiClient.client.create(ApiInterface::class.java)
//
//        val call: Call<ResponseDataClass> = apiInterface!!.sentOriginDest(startcity!!, endcity!!)
//
//        call.enqueue(object : Callback<ResponseDataClass> {
//            override fun onResponse(
//                call: Call<ResponseDataClass>,
//                response: Response<ResponseDataClass>
//            ) {
//
//                if (response.body()?.data == null) {
//                    Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG)
//                        .show()
//                } else {
//                    hidekeyboard()
//
//                    coordinates.clear()
//                    mMap?.clear()
//
//
//                    val responseDataClass: ResponseDataClass? = response.body()
//                    val myResult: Data? = Gson().fromJson(Gson().toJson(responseDataClass!!.data), Data::class.java)
//
//                    saveLatlongPoints(myResult)
//
//                    drawPolyline()
//                    startNavigation()
//
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable?) {
//                Toast.makeText(
//                    applicationContext,
//                    "getString(R.string.msg_Sorry_No_Routes_Found)",
//                    Toast.LENGTH_LONG
//                )
//                    .show()
//            }
//        })
//    }


//    private fun drawPolyline() {
//        val options = PolylineOptions().width(8f).color(Color.BLUE).geodesic(true)
//        for (z in 0 until coordinates.size) {
//            val point: LatLng = coordinates.get(z)
//            options.add(point)
//        }
//        mPolyline = mMap?.addPolyline(options)
//    }

//    private fun saveLatlongPoints(myResult: Data?) {
//        myResult?.mydata?.forEach {
//            val latitude = it.lat
//            val longitude = it.lng
//            coordinates.add(LatLng(latitude, longitude))
//        }
//        startpoint = LatLng(coordinates.get(0).latitude, coordinates.get(0).longitude)
//        lastpoint = LatLng(
//            coordinates.get(coordinates.lastIndex).latitude,
//            coordinates.get(coordinates.lastIndex).longitude
//        )
//        setMarkerPoints(startpoint!!, lastpoint!!)
//    }

    private fun setMarkerPoints(start: LatLng) {


//
//        mMap?.addMarker(
//            MarkerOptions()
//                .position(start)
//                .title("Marker in Sydney")
//        )
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(start))


//        val builder = LatLngBounds.Builder()
//
//        for (marker in markerPoints) {
//            builder.include(marker as LatLng)
//        }
//        val bounds = builder.build()
//        val cu = CameraUpdateFactory.newLatLngBounds(bounds, 450)
//        MapsActivity.mMap?.animateCamera(cu)
    }


    //    private fun getAddressFromLatLng(latLng: LatLng): Address? {
//        val geocoder = Geocoder(this)
//        val addresses: List<Address>?
//        return try {
//            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5)
//            addresses?.get(0)
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
    private fun setMarker(latLng: LatLng) {

        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        mMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap?.addMarker(markerOptions)
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

        setMarker(startpoint!!)
        setMarker(lastpoint!!)

        val builder = LatLngBounds.Builder()
        builder.include(lastpoint!!).include(startpoint!!)


        val latLngBounds = builder.build()
        val cu = CameraUpdateFactory.newLatLngBounds(latLngBounds, 350)
        mMap?.animateCamera(cu)


//        mMap?.setOnMapClickListener {
//            if (markerPoints.size > 1) {
//                markerPoints.clear()
//                mMap?.clear()
//            }
//
//            markerPoints.add(it)
//
//            options.position(it)
//            if (markerPoints.size == 1) {
//
//                val addresses = getAddressFromLatLng(it)
//
//                Log.d("adresss", "----" + addresses)
//                val state = addresses?.adminArea
//                val socityr = addresses?.premises
//                val area = addresses?.subLocality
//                startcity = addresses?.locality
//                val district = addresses?.subAdminArea
//                val country = addresses?.countryName
//                var add = ""
//                Log.d(
//                    "Myaddress", "start---$state \n " +
//                            "society---$socityr  \n   " +
//                            "area---$area \n  ciyt---$startcity \n" +
//                            "distric----$district  \n country--$country"
//                )
//
//                if (socityr != null) {
//                    add += socityr + ", "
//
//                }
//
//                if (area != null) {
//                    add += area + ", "
//
//                }
//                if (startcity != null) {
//                    add += startcity + ", "
//
//                }
//                if (district != null) {
//                    add += district + ", "
//
//                }
//                if (state != null) {
//                    add += state + ", "
//
//                }
//                if (country != null) {
//                    add += country + ", "
//
//                }
//                if (country == null) {
//                    add += "Sea"
//                }
//
//
//
//                startlocation?.setText(add)
//
//                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//
//
//            }
//            else if (markerPoints.size == 2) {
//
//                val addresses = getAddressFromLatLng(it)
//                val state = addresses?.adminArea
//                val socityr = addresses?.premises
//                val area = addresses?.subLocality
//
//                endcity = addresses?.locality
//                val country = addresses?.countryName
//                var add = ""
//                if (socityr != null) {
//                    add += socityr + ", "
//
//                }
//                if (area != null) {
//                    add += area + ", "
//
//                }
//                if (endcity != null) {
//                    add += endcity + ", "
//
//                }
//                if (state != null) {
//                    add += state + ", "
//
//                }
//                if (country == null) {
//                    add += "Sea"
//                }
//                if (country != null) {
//                    add += country + ", "
//
//                }
//                endlocation?.setText(add)
//                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//
//            }

//        mMap?.addMarker(options)

//            if (markerPoints.size >= 2) {
//                origin = markerPoints[0] as LatLng
//                dest = markerPoints[1] as LatLng
//
//
//                val builder = LatLngBounds.Builder()
//                for (marker in markerPoints) {
//                    builder.include(marker as LatLng)
//                }
//                val bounds = builder.build()
//                val cu = CameraUpdateFactory.newLatLngBounds(bounds, 450)
//                mMap?.animateCamera(cu)
//
//
////                drawRoute()
//            }

//        }
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
