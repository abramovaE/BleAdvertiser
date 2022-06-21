package ru.kotofeya.bleadvertiser

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat


class MainActivity : ComponentActivity(), ClickListener{

    private lateinit var btManager: BluetoothManager
    private lateinit var btAdapter: BluetoothAdapter
    private lateinit var btAdvertiser: BluetoothLeAdvertiser

    private val permissionsRequestCode = 123

    private var hasPermissions = false

    @RequiresApi(Build.VERSION_CODES.S)
    private val permissions = arrayOf(android.Manifest.permission.BLUETOOTH_ADVERTISE,
        android.Manifest.permission.BLUETOOTH_CONNECT)

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BtPackage(this)
        }
        btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager.adapter
        hasPermissions = hasPermissions(this, permissions = permissions)
        if (!hasPermissions) {
            ActivityCompat.requestPermissions(this,
                permissions, permissionsRequestCode)
        }
    }

    private fun getAdvertiseSettings(): AdvertiseSettings? {
        Log.d("TAG", "getAdvertiseSettings()")
        return AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false)
            .build()
    }

    private fun createAdvertisingCallBack(): AdvertiseCallback {
        Log.d("TAG", "createAdvertisingCallBack()")
        return object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                Log.d("TAG","onStartSuccess()")
                super.onStartSuccess(settingsInEffect)
            }
            override fun onStartFailure(errorCode: Int) {
                Log.d("TAG","onStartFailure(): $errorCode")
                super.onStartFailure(errorCode)
            }
        }
    }

    private fun getAdvertiseData(data: String): AdvertiseData? {
        Log.d("TAG", "getAdvertiseData(String data): $data")
        val bytes = data.trim().split(" ")
            .map { it.toByte() }
            .toByteArray()
        Log.d("TAG", "advData: " + bytes.contentToString())
        return AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .addManufacturerData(0xffff, bytes)
            .build()
    }

    override fun startAdvertising(adv: String){
        Log.d("TAG", "startAdvertising(): $adv")
        btAdapter.name = "stp"
        btAdvertiser = btAdapter.bluetoothLeAdvertiser
        val advSettings = getAdvertiseSettings()
        val callback = createAdvertisingCallBack()
        val advData = getAdvertiseData(adv)
        btAdvertiser.startAdvertising(advSettings, advData, callback)
    }

    override fun stopAdvertising(){
        Log.d("TAG", "stopAdvertising()")
        val callback = createAdvertisingCallBack()
        btAdvertiser.stopAdvertising(callback)

    }
}

interface ClickListener{
    fun startAdvertising(adv: String)
    fun stopAdvertising()
}

@Composable
fun BtPackage(clickListener: ClickListener) {
    Column(
        modifier = Modifier
            .background(Color.Yellow)
            .fillMaxSize()
    ) {
        val state = remember {
//            mManufacturerSpecificData={65535=[1, 0, 0, 26, -45, 64, 85, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]}
            val bytes = byteArrayOf(1, 0, 0, 26, -45, 64, 85, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            val stringBuilder = StringBuilder()
            bytes.forEach { stringBuilder.append(it).append(" ") }
            mutableStateOf(stringBuilder.toString())
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(Color.White)
        ) {

                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.value,
                    onValueChange = { state.value = it }
                )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    clickListener.startAdvertising(state.value)
//                    startAdvertising(state.value)
                }
            ) {
                Text(
                    text = "Start advertising"
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    clickListener.stopAdvertising()
                }
            ) {
                Text(
                    text = "Stop advertising"
                )
            }
        }
    }
}
