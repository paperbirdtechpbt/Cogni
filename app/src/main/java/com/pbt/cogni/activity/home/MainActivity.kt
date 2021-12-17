package com.pbt.cogni.activity.home


import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pbt.cogni.R
import com.pbt.cogni.activity.TabLayout.TabTripStatusFragment
import com.pbt.cogni.activity.login.LoginActivity
import com.pbt.cogni.fragment.Chat.UserChatListFragment
import com.pbt.cogni.fragment.Current.GPSTracker
import com.pbt.cogni.fragment.Profile.ProfileFragment
import com.pbt.cogni.fragment.audioVideoCall.AudioVideoFragement
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.model.UserDetailsData
import com.pbt.cogni.util.AppConstant.Companion.PREFF_OVERYLAY_PERMISSION
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), Callback<HttpResponse>, CoroutineScope {


    var bottomNavigation: BottomNavigationView? = null
    lateinit var chatsFragement: UserChatListFragment
    lateinit var audioVideoFragement: AudioVideoFragement
    lateinit var tabTripStatusFragment: TabTripStatusFragment
    lateinit var profileFragement: ProfileFragment
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var user: UserDetailsData? = null
    private var job: Job = Job()

    lateinit var locationrequest: LocationRequest




    companion object {
        var instance: MainActivity? = null
            get() = instance
        var lat = 1.0
        var long = 1.0

        const val TAG = "MainActivity"
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)

    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppUtils.logDebug(TAG,"On Creast")


        user = MyPreferencesHelper.getUser(this)

        setContentView(R.layout.activity_main)
        val manufacturer = "xiaomi"

        if (manufacturer.equals(Build.MANUFACTURER, ignoreCase = true)) {
            //this will open auto start screen where user can enable permission for your app
            val intent1 = Intent(  )

            intent1.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity",
            )
            intent1.putExtra("extra_pkgname", getPackageName())
            startActivity(intent1)

            Toast.makeText(this,"Please Allow Show on LockScreen in Others Permission Option",Toast.LENGTH_LONG).show()

        }
        val manufactureVIVO = "vivo"
        if (manufactureVIVO.equals(Build.MANUFACTURER, ignoreCase = true)) {
            //this will open auto start screen where user can enable permission for your app
            val intent1 = Intent(  )

            intent1.setClassName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager")
            intent1.putExtra("packagename", packageName)
            startActivity(intent1)

            Toast.makeText(this,"Please Allow Show on LockScreen in Others Permission Option",Toast.LENGTH_LONG).show()

        }
        val manufactureOPPO = "oppo"
        if (manufactureOPPO.equals(Build.MANUFACTURER, ignoreCase = true)) {
            //this will open auto start screen where user can enable permission for your app
            val intent1 = Intent(  )

            intent1.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionAppListActivity")
            intent1.putExtra("packagename", getPackageName())
            startActivity(intent1)

            Toast.makeText(this,"Please Allow Show on LockScreen in Others Permission Option",Toast.LENGTH_LONG).show()

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(this)){
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + applicationContext.packageName))
                        startActivity(intent)
                    }
                }


            MyPreferencesHelper.setStringValue(this, PREFF_OVERYLAY_PERMISSION, "true")

//        }
        getSupportActionBar()?.setTitle("Analyst Routes List ")

        if (savedInstanceState == null) {
            if (chechpermission()) {
AppUtils.logDebug(TAG,"Checking Permission")
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.framelayout, TabTripStatusFragment()).commit()
            bottomNavigation = findViewById(R.id.bottom_navigation)
            bottomNavigation?.getMenu()?.get(0)?.setChecked(true)
        }
        bottomNavigation?.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.profile -> {
                    getSupportActionBar()?.setTitle("Profile")

                    profileFragement = ProfileFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.framelayout, profileFragement)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

                R.id.chat -> {

                    getSupportActionBar()?.setTitle("Chats")
                    supportActionBar?.show()


                    chatsFragement = UserChatListFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.framelayout, chatsFragement)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.audiovideo -> {
                    getSupportActionBar()?.setTitle("Audio Video ")

                    supportActionBar?.show()
                    audioVideoFragement = AudioVideoFragement()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.framelayout, audioVideoFragement)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
//                R.id.viewroute -> {
//                    getSupportActionBar()?.setTitle("Analyst Routes List ")
//                    supportActionBar?.show()
//
//                    viewRouteFragement = ViewRouteFragement()
//                    supportFragmentManager
//                        .beginTransaction()
//                        .replace(R.id.framelayout, viewRouteFragement)
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                        .commit()
//                }
                R.id.test -> {

                    getSupportActionBar()?.setTitle("Analyst Routes List ")
                    supportActionBar?.show()

                    tabTripStatusFragment = TabTripStatusFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.framelayout, tabTripStatusFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
            }
            true
        }




    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        menu.findItem(R.id.ic_menu_logout).setVisible(false)
        return true
    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.ic_menu_logout -> {
showAlertDialog()
                true
            }
          else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialogTitle)
        builder.setMessage(R.string.dialogMessage)
        builder.setIcon(android.R.drawable.ic_dialog_alert)



        builder.setPositiveButton("Yes") { dialog, which ->
            GPSTracker.stopService(this)

            setVisible(false)
            MyPreferencesHelper.clearPref(this)
            startActivity(Intent(this,LoginActivity::class.java))
            this.finishAffinity()
        }

        builder.setNegativeButton("No") { dialog, which ->
            Toast.makeText(applicationContext,
                android.R.string.no, Toast.LENGTH_SHORT).show()
        }

        builder.setNeutralButton("Cancle") { dialog, which ->
            Toast.makeText(applicationContext,
                "Cancle", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }


    private fun chechpermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                android.Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.VIBRATE,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.CAPTURE_AUDIO_OUTPUT,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CALL_PHONE,
                    ), 100)

        }
        return true
    }


    override fun onResponse(call: Call<HttpResponse>, response: Response<HttpResponse>) {
//        Toast.makeText(this, "ResponseSucessFull", Toast.LENGTH_LONG).show()
        AppUtils.logDebug(TAG,"Response Successfull")
    }


    override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
        Toast.makeText(this, "${t.message}", Toast.LENGTH_LONG).show()
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.ic_menu_logout)?.setVisible(false)
        return super.onPrepareOptionsMenu(menu)
    }

    override val coroutineContext: CoroutineContext
        get() =  Dispatchers.Main + job

    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {

//        GPSTracker.startService(this, "Foreground Service is running...")
        //for Stop service
//        GPSTracker.stopService(this)

        super.onDestroy()

    }
}