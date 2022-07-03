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
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


/*
General:
1-9: Байты отведены под имя устройства
10: Версия BLE пакета (внутренняя)
11: Резерв
12-14: Объявление серийного номера трансивера (0 -  16 777 216)
15: Тип трансивера: 20 - Триоль (УЗГС); 40 -Стационар; 80 - Транспорт


Структура пакета:
stationars:


16: Объявление состояния звуковых маяков, по два бита слева направо 1, 2, 3, 4.
17: Флаги нулевого звукового маяка(0 ВЗ), инкремента вызова
18-21: CRC сумма - считывается из JSON файла расположенного в /var/www/html/data/text_files/*.json
22-23: Индекс номер города - считывается из JSON файла расположенного в /var/www/html/data/city.json на основании имени файла с транспортным контентом в пункте описанном выше
24-25: Тип стационарного объекта (подробнее по ссылке.) получается при инициализации системы.
26: Этаж - назначен STM (получается при инициализации системы
27-31: Резервные байты

transport:

16: Объявление состояния звуковых маяков, по два бита слева направо 1, 2, 3, 4.
17: Флаги - состояние дверей, направление движения, инкрементное число вызова (изменяется при каждом вызове, увеличиваясь на единицу)
24: Тип транспортного средства (подробнее по ссылке).
25: Третья литера
26-27: Номер маршрута
28: Первая литера
29: Вторая литера
30-31: Резервные байты

triol:

16: Режим работы светофора:
1) День, зеленый свет (Молчим)
2) День, красный свет ("Название улицы")
3) День, красный свет, с кнопкой ("Название улицы. Для включения зеленого сигнала светофора нажмите кнопку вызов")
4) День/Ночь, выключен ("Название улицы. Светофор временно отключен. Будьте осторожны."
5) Ночь, ("Название улицы. Звуковое сопровождение сигналов светофора выключено. Для включения нажмите кнопку вызов")
17: Инкременты: (слева направо)
4 бита - counter_status (0-15) (инкремент состояния, изменяется раз в секунду - отслеживаем зависания)
4 бита - counter_call (0-15) (инкремент вызова, изменяется при успешном вызове)
18-21: Время в формате отсчета от начала эпохи
22-23: Id номер имени улицы (0 -65534)
24-25: Резерв
26: side_street _ сторона улицы
0) Не установлено
1) нечетная сторона
2) Четная сторона
27-31: Резерв
*/

*/






class MainActivity : ComponentActivity(), ClickListener{

    private lateinit var btManager: BluetoothManager
    private lateinit var btAdapter: BluetoothAdapter
    private lateinit var btAdvertiser: BluetoothLeAdvertiser

    private val permissionsRequestCode = 123

    private var hasPermissions = false

    private lateinit var packsViewModel: PacksViewModel

    private var isBleAdvInit = false

    @RequiresApi(Build.VERSION_CODES.S)
    private val permissions = arrayOf(android.Manifest.permission.BLUETOOTH_ADVERTISE,
        android.Manifest.permission.BLUETOOTH_CONNECT)

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        packsViewModel = ViewModelProvider((this), PacksViewModel.Factory(this))
            .get(PacksViewModel::class.java)


        setContent {
            BtPackage(this, packsViewModel)
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

    private fun getAdvertiseData(data: ByteArray): AdvertiseData? {
        Log.d("TAG", "getAdvertiseData(String data): ${data.contentToString()}")
        return AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .addManufacturerData(0xffff, data)
            .build()
    }

    override fun startAdvertising(adv: ByteArray){
        Log.d("TAG", "startAdvertising(): $adv")
        btAdapter.name = "stp"
        btAdvertiser = btAdapter.bluetoothLeAdvertiser
        isBleAdvInit = true
        val advSettings = getAdvertiseSettings()
        val callback = createAdvertisingCallBack()
        val advData = getAdvertiseData(adv)
        btAdvertiser.startAdvertising(advSettings, advData, callback)
    }

    override fun stopAdvertising(){
        Log.d("TAG", "stopAdvertising()")
        if(isBleAdvInit) {
            val callback = createAdvertisingCallBack()
            btAdvertiser.stopAdvertising(callback)
        }
    }
}

interface ClickListener{
    fun startAdvertising(adv: ByteArray)
    fun stopAdvertising()
}



@Composable
fun Main(clickListener: ClickListener, navController: NavController){
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .verticalScroll(
                state = ScrollState(0),
                enabled = true
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val byteArr = ByteArray(22)
                    clickListener.startAdvertising(byteArr)
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
        Btn(navController = navController, "showallpacks", "Show all packs")
        Btn(navController = navController, "createtriol", "Create new triol")
    }
}



@Composable
fun Btn(navController: NavController, route: String, btnText: String){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(route)
            }
        ) {
            Text(
                text = btnText
            )
        }
    }
}

@Composable
fun BtPackage(clickListener: ClickListener, viewModel: PacksViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main"){
        composable("main"){ Main(clickListener = clickListener,
            navController = navController)}
        composable("createtriol"){CreateStoplight(
            navController = navController, viewModel)}
        composable("showallpacks"){ShowAllPacks(
            navController = navController, viewModel = viewModel)}
        composable("showpack/{packId}",
            arguments = listOf(navArgument("packId"){type = NavType.IntType})){
            backStackEntry -> ShowStoplight(
            navController = navController,
            viewModel = viewModel,
            packId = backStackEntry.arguments?.getInt("packId"))}
    }
}