package com.example.yaghotpractic

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Button
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var wifiManager: WifiManager
    lateinit var myBlueTooth: BluetoothAdapter

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        myBlueTooth = BluetoothAdapter.getDefaultAdapter()
        setContentView(binding.root)

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
        TODO("Not yet implemented")
    }
}
