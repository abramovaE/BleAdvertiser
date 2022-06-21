package ru.kotofeya.bleadvertiser

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.kotofeya.bleadvertiser.ui.theme.BleAdvertiserTheme
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.StringBuilder
import java.lang.reflect.Array
import java.nio.charset.StandardCharsets
import java.security.Permission
import java.util.*
import java.util.jar.Manifest


class MainActivity : ComponentActivity(), ClickListener{

    lateinit var btManager: BluetoothManager
    lateinit var btAdapter: BluetoothAdapter
    lateinit var btAdvertiser: BluetoothLeAdvertiser
//    lateinit var adv: String
    private val permissionsRequestCode = 123
//    private lateinit var managePermissions: ManagePermissions

    var hasPermissions = false

    val permissions = arrayOf(android.Manifest.permission.BLUETOOTH_ADVERTISE,
        android.Manifest.permission.BLUETOOTH_CONNECT)
    val advPermission = android.Manifest.permission.BLUETOOTH_ADVERTISE
    val connectPermission = android.Manifest.permission.BLUETOOTH_CONNECT
    var isAdvPermissionGranted = false
    var isConnectPermissionGranted = false

//    private val requestPermissionLauncher =  registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ){ isGranted: Boolean ->
//            if(isGranted){
//                Log.d("TAG", "granted")
//            } else {
//                Log.d("TAG", "not granted")
//            }
//    }

    fun onClickRequestPermission(permission: String){
        when {
            ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED -> {
                        if(permission.equals(advPermission)){
                            isAdvPermissionGranted = true
                        }
                if(permission.equals(connectPermission)){
                    isConnectPermissionGranted = true
                }
            }
//            ActivityCompat.shouldShowRequestPermissionRationale(this, permission) -> {
//                Log.d("TAG", "not granted")
//            }
//            else -> {
//                requestPermissionLauncher.launch(permission)
//            }
        }
    }

    fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            btPackage(this)
        }
        btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager.adapter

        onClickRequestPermission(advPermission)
        onClickRequestPermission(connectPermission)

        hasPermissions = hasPermissions(this, permissions = arrayOf(advPermission, connectPermission))
        if (!hasPermissions) {
            ActivityCompat.requestPermissions(this,
                arrayOf(advPermission, connectPermission), permissionsRequestCode)
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

    private fun createAdvertisingCallBack(): AdvertiseCallback? {
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
        Log.d("TAG", "advData: " + Arrays.toString(bytes))
        return AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .addManufacturerData(0xffff, bytes)
            .build()
    }

//    fun startAdv(){
//        Log.d("TAG", "startAdv(): $adv")
//        btAdapter.setName("stp")
//        btAdvertiser = btAdapter.bluetoothLeAdvertiser
//        val advSettings = getAdvertiseSettings()
//        val callback = createAdvertisingCallBack()
//        val advData = getAdvertiseData(adv)
//        btAdvertiser.startAdvertising(advSettings, advData, callback)
//    }

    override fun startAdvertising(adv: String){
        Log.d("TAG", "startAdvertising(): $adv")
//        this.adv = adv
        btAdapter.setName("stp")
        btAdvertiser = btAdapter.bluetoothLeAdvertiser
        val advSettings = getAdvertiseSettings()
        val callback = createAdvertisingCallBack()
        val advData = getAdvertiseData(adv)
        btAdvertiser.startAdvertising(advSettings, advData, callback)
//        if(isAdvPermissionGranted && isConnectPermissionGranted){
//            startAdv()
//        } else {
//            onClickRequestPermission(advPermission)
//            onClickRequestPermission(connectPermission)
//        }
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

//interface PermissionListener{
//    fun granted()
//    fun notGranted()
//}


@Composable
fun btPackage(clickListener: ClickListener) {

    Column(
        modifier = Modifier
            .background(Color.Yellow)
            .fillMaxSize()
    ) {
        val state = remember {
//            mManufacturerSpecificData={65535=[1, 0, 0, 26, -45, 64, 85, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]}
            val bytes = byteArrayOf(1, 0, 0, 26, -45, 64, 85, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            var stringBuilder = StringBuilder()
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
