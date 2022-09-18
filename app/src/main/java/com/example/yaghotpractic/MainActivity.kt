package com.example.yaghotpractic

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.TaskStackBuilder.create
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.wifi.WifiManager
import android.os.Build
import android.widget.Toast
import com.example.yaghotpractic.databinding.ActivityMainBinding
import android.provider.Settings

import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.hardware.HardwareBuffer.create
import android.location.Geocoder
import android.location.Location
import android.location.LocationRequest
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate.create
import androidx.appcompat.graphics.drawable.AnimatedStateListDrawableCompat.create
import androidx.core.app.ActivityCompat
import androidx.core.widget.EdgeEffectCompat.create
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.material.color.HarmonizedColorAttributes.create
import java.io.IOException
import java.net.URI.create
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var wifiManager: WifiManager
    lateinit var myBlueTooth: BluetoothAdapter
    lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        myBlueTooth = BluetoothAdapter.getDefaultAdapter()
        setContentView(binding.root)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        myBlueTooth = BluetoothAdapter.getDefaultAdapter()
        val bluetoothManager =
            applicationContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        with(binding)
        {
            enableWIFI.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val intent = Intent(Settings.Panel.ACTION_WIFI)
                    startActivityForResult(intent, 1)
                } else {
                    val wifiManager =
                        applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                    wifiManager.isWifiEnabled = true
                }
            }
            disableWIFI.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val intent = Intent(Settings.Panel.ACTION_WIFI)
                    startActivityForResult(intent, 1)
                } else {
                    val wifiManager =
                        applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                    wifiManager.isWifiEnabled = false
                }
            }
            enableBluetooth.setOnClickListener {
                var intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivity(intent)
                //todo = blotooth dosn't work for 12 and 13 android
            }
            disableBluetooth.setOnClickListener {
                if (myBlueTooth.isEnabled) {
                    //Turn Off bluetooth
                    myBlueTooth.disable()
                    toast(this@MainActivity, "bluetooth turned off")
                } else {
                    //Show this toast if bluetooth is already OFF
                    toast(this@MainActivity, "Bluetooth is already off")
                }
            }
            binding.getLocation.setOnClickListener {
                checkLocationPermission()
                checkGPS()
                getUserLocation()
            }

        }
    }

    private fun toast(activity: Activity, txt: String) {
        Toast.makeText(activity, txt, Toast.LENGTH_SHORT).show()
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //when permission already granted
            checkGPS()
        } else {
            //when permission denied
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )

            }
        }

    private fun checkGPS() {
        locationRequest = com.google.android.gms.location.LocationRequest.create()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            locationRequest.priority = LocationRequest.QUALITY_HIGH_ACCURACY
        }else{
            locationRequest.priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationRequest.interval = 5000
            locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            builder.setAlwaysShow(true)

        getUserLocation()

        val result = LocationServices.getSettingsClient(this.applicationContext).checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                //when the gps id on
                val response = task.getResult(ApiException::class.java)
            }catch ( e : ApiException )
            {
                //when the gps is off
                e.printStackTrace()
                when (e.statusCode){
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    //here we send request to enable gps
                        val resoApiException = e as ResolvableApiException
                        resoApiException.startResolutionForResult(this,200)
                    }catch (sendIntendExeption : IntentSender.SendIntentException){
                            //when settings are not available
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener{ task ->
            val location = task.getResult()
            if (location != null)
            {
                try {
                val geocoder = Geocoder(this , Locale.getDefault())
                    val address = geocoder.getFromLocation( location.latitude , location.latitude , 1 )
                    //here we set the address to text view
                    val address_Line = address[0].getAddressLine(0)
                    binding.textView.text = address_Line.toString()
                }catch (e : IOException)
                {

                }
            }
        }

    }

}
