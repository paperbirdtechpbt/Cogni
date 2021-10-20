package com.pbt.cogni.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.internal.OnConnectionFailedListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.pbt.cogni.Parse.DirectionsJSONParser
import com.pbt.cogni.R
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.MapsActivity.Companion.coordinates
import com.pbt.cogni.activity.MapsActivity.Companion.endLat
import com.pbt.cogni.activity.MapsActivity.Companion.endLong
import com.pbt.cogni.activity.MapsActivity.Companion.mMap
import com.pbt.cogni.activity.MapsActivity.Companion.mPolyline
import com.pbt.cogni.activity.MapsActivity.Companion.markerPoints
import com.pbt.cogni.activity.MapsActivity.Companion.startLat
import com.pbt.cogni.activity.MapsActivity.Companion.startLong
import com.pbt.cogni.model.BaseRoutLatLng
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.util.AppConstant.Companion.CONST_FROM_ADDRESS
import com.pbt.cogni.util.AppConstant.Companion.CONST_ROUTE_ID
import com.pbt.cogni.util.AppConstant.Companion.CONST_STATUS
import com.pbt.cogni.util.AppConstant.Companion.CONST_STATUS_APPROVED
import com.pbt.cogni.util.AppConstant.Companion.CONST_TO_ADDRESS
import com.pbt.cogni.util.AppConstant.Companion.CONST_TO_DESTINATION_LAT
import com.pbt.cogni.util.AppConstant.Companion.CONST_TO_DESTINATION_LONG
import com.pbt.cogni.util.AppConstant.Companion.CONST_TO_ORIGIN_LAT
import com.pbt.cogni.util.AppConstant.Companion.CONST_TO_ORIGIN_LONG
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.Config.BASE_GOOGLE_MAP_ROUTES
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
    Callback<HttpResponse> {

    private var startcitylatlng: String? = ""
    private var endcitylatlng: String? = ""
    private var startAddress: String = ""
    private var endAddress: String = ""
    private var myWaypoint: String = ""

    companion object {
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

        var floatButton : FloatingActionButton ? = null;

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        markerPoints.clear()

        floatButton = findViewById(R.id.floating_action_button)

        floatButton?.setOnClickListener {
//            23.638096496165915, 72.44615984161454
            var uri = "waypoints=22.7349,72.4402|22.7507,72.6847"
            val navigation = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    //pela ahiya end location mukine check karje
                    "google.navigation:q=" + "$startcitylatlng,$endcitylatlng"+"&"+uri)
//                    "google.navigation:q=" + "23.577571,72.352010"+"&"+uri)
            )
            navigation.setPackage("com.google.android.apps.maps")
            startActivity(navigation)

//            val strUri =
//            "http://maps.google.com/maps?q=loc:" + startcitylatlng.toString() + "," + endcitylatlng.toString() + " (" + "Sola".toString() + ")"
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(strUri))
//            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
////            startActivity(intent)
////
////            val navigation = Intent(
////            Intent.ACTION_VIEW,
////            Uri.parse(
////                "google.navigation:q=" + endLat+","+ endLong+"&"+ myWaypoint)
////        )
////        navigation.setPackage("com.google.android.apps.maps")
//        startActivity(intent)
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        startAddress = intent.getStringExtra(CONST_TO_ADDRESS)
        endAddress = intent.getStringExtra(CONST_FROM_ADDRESS)

        if (intent.getStringExtra(CONST_STATUS).equals(CONST_STATUS_APPROVED)) {
            startLat = intent.getDoubleExtra(CONST_TO_ORIGIN_LAT, 00.00)
            startLong = intent.getDoubleExtra(CONST_TO_ORIGIN_LONG, 00.00)
            endLat = intent.getDoubleExtra(CONST_TO_DESTINATION_LAT, 00.00)
            endLong = intent.getDoubleExtra(CONST_TO_DESTINATION_LONG, 00.00)
            routeId = intent.getIntExtra(CONST_ROUTE_ID, 0).toString()
//            drawRoute()
            getWayPoint()

            AppUtils.logDebug(TAG, " Routes Id " + routeId)
        }
    }

    public fun getWayPoint() {
        ApiClient.client.create(ApiInterface::class.java).getWayPoint(routeId!!).enqueue(this)
    }

    private fun drawPolyline() {
        val options = PolylineOptions().width(8f).color(Color.BLUE).geodesic(false)
        for (z in 0 until coordinates.size) {
            val point: LatLng = coordinates.get(z)
            options.add(point)
        }
        mPolyline = mMap?.addPolyline(options)
    }

    private fun hidekeyboard() {
        val inputManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.SHOW_FORCED
        )
    }


//    private fun setMarker(latLng: LatLng) {
//        markerPoints.add(latLng)
//
//        options.position(latLng)
//
//        if (markerPoints.size == 1) {
//            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//        } else if (markerPoints.size == 2) {
//            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//        }
//        mMap?.addMarker(options)
//    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


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
//            } else if (markerPoints.size == 2) {
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
//
//            mMap?.addMarker(options)
//
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
//
//        }
    }

    private fun drawRoute(org: LatLng, dest: LatLng) {
        val url: String = getDirectionsUrl(org,dest)

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
        startcitylatlng=dest.latitude.toString()
        endcitylatlng=dest.longitude.toString()



//        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
//        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
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
            var listLatLong: BaseRoutLatLng =
                Gson().fromJson(response?.body()?.data.toString(), BaseRoutLatLng::class.java)

            coordinates.clear()
            listLatLong.listLatLng?.forEach {
                val latitude = it.lat
                val longitude = it.long
                coordinates.add(LatLng(latitude, longitude))
            }

            var orgn = LatLng(coordinates.get(0).latitude, coordinates.get(0).longitude)
           var  dest = LatLng(
                coordinates.get(coordinates.lastIndex).latitude,
                coordinates.get(coordinates.lastIndex).longitude)



            var uri = "waypoints="
//            var k: Int = 1
//            var count: Int = 0
//            var size: Int = listLatLong.listLatLng!!.size / 22
            //            listLatLong.listLatLng?.forEach {
//                if (k == 1) {
//                    count++
//                    val latitude = it.lat
//                    val longitude = it.long
//                    uri = uri + latitude + "," + longitude + "|"
//                    Log.d("##mylatlong","lat--"+latitude+"long--"+longitude)
//                }
//                if (size == k) {
//                    k = 1
//                }
//                k++
//            }
            myWaypoint = uri
            AppUtils.logDebug(TAG, "Last Char " + uri)
            AppUtils.logDebug(TAG, "Last Char " + uri.dropLast(1))
//            drawRoute(uri.dropLast(1))
            drawRoute(orgn,dest)
        }
    }

    override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
        AppUtils.logError(TAG, "Error " + t.message)
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


//                val path = result[i]
//
//
//                for (j in path.indices) {
//                    val point = path[j]
//                    val lat = point["lat"]!!.toDouble()
//                    val lng = point["lng"]!!.toDouble()
//                    val position = LatLng(lat, lng)
//                    points?.add(position)
//                }
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
//                mMap?.moveCamera(
//                    CameraUpdateFactory.newLatLngZoom(
//                        LatLng(startLat, startLong),
//                        12F
//                    )
//                )
                val builder = LatLngBounds.Builder()
                for (marker in markerPoints) {
                    builder.include(marker as LatLng)
                }
                val bounds = builder.build()
                val cu = CameraUpdateFactory.newLatLngBounds(bounds, 200)
                mMap?.animateCamera(cu)
            }
            //var startLat: Double = 00.00
            //    var startLat: Double =  00.00
        }

        private fun setMarkerPoints(start: LatLng) {
//            mMap?.clear()
            MapsActivity.markerPoints.add(start)
            MapsActivity.options.position(start)
            MapsActivity.options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            mMap?.addMarker(MapsActivity.options)
        }
    }

}
