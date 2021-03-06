package ru.kotofeya.bleadvertiser

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
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
import androidx.compose.runtime.Composable
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
import ru.kotofeya.bleadvertiser.create.CreateNew
import java.util.*


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

    private var startAdvTime = 0L

    private var advSetCallBackList: MutableList<AdvertisingSetCallback> = mutableListOf()
    private var advTimerList: MutableList<Timer> = mutableListOf()



    @RequiresApi(Build.VERSION_CODES.S)
    private val permissions_s = arrayOf(
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.BLUETOOTH_ADVERTISE)

    private fun hasPermissions(context: Context,
                               vararg permissions: String):
        Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it)== PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        packsViewModel = ViewModelProvider((this), PacksViewModel.Factory(this))
            .get(PacksViewModel::class.java)
        packsViewModel.loadAllPacks()

        setContent {
            BtPackage(this, packsViewModel)
        }
        btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager.adapter

        var permissions = emptyArray<String>()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            permissions = permissions_s
        }

        hasPermissions = hasPermissions(this, permissions = permissions)
        if (!hasPermissions) {
            ActivityCompat.requestPermissions(this,
                permissions, permissionsRequestCode)
        }
    }

    private fun getAdvertiseData(data: ByteArray): AdvertiseData? {
        return AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .addManufacturerData(0xffff, data)
            .build()
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun startAdvertising(adv: ByteArray,
                                  changeTime: Boolean,
                                  changeCounterIncr: Boolean) {
        Log.d("TAG", "startAdvertising(): ${adv.contentToString()}")


        if(hasPermissions) {

            startAdvTime = System.currentTimeMillis()
            btAdapter.name = "stp"
            btAdvertiser = btAdapter.bluetoothLeAdvertiser
            isBleAdvInit = true
            val advData = getAdvertiseData(adv)
            var currentAdvSet: AdvertisingSet? = null

            val params = AdvertisingSetParameters.Builder()
                .setLegacyMode(true)
                .setConnectable(false)
                .build()

            val advSetCallBack = object : AdvertisingSetCallback() {
                override fun onAdvertisingSetStarted(
                    advertisingSet: AdvertisingSet?,
                    txPower: Int,
                    status: Int
                ) {
                    Log.d("TAG", "onAdvSetStarted()")
                    currentAdvSet = advertisingSet
                }
            }

            advSetCallBackList.add(advSetCallBack)

            btAdvertiser.startAdvertisingSet(
                params, advData,
                null, null,
                null, advSetCallBack
            )

            if (changeTime || changeCounterIncr) {
                val advTimer = Timer()
                advTimerList.add(advTimer)
                advTimer.schedule(object : TimerTask() {
                    override fun run() {
                        if (changeTime) {
                            val time = getTimeFromByteArray(adv) + 1000L
                            val timeArray = getByteArrayFromTime(time = time)
                            Log.d("TAG", "time: $time")
                            adv[8] = timeArray[0]
                            adv[9] = timeArray[1]
                            adv[10] = timeArray[2]
                            adv[11] = timeArray[3]
                        }

                        if (changeCounterIncr) {
                            adv[7] = adv[7].inc()
                            Log.d("TAG", "increment: ${adv[7]}")
                        }
                        val newAdvData = getAdvertiseData(adv)
                        currentAdvSet?.setAdvertisingData(newAdvData)
                    }
                }, 0, 1000)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun stopAdvertising(){
        if(isBleAdvInit) {
            Log.d("TAG", "stopAdvertising()")
            advTimerList.forEach(::cancelTimer)
            advSetCallBackList.forEach(btAdvertiser::stopAdvertisingSet)
            isBleAdvInit = false
        }
    }

    private fun cancelTimer(timer: Timer){
        timer.cancel()
    }
}



interface ClickListener{
    fun startAdvertising(adv: ByteArray,
                         changeTime: Boolean,
                         changeCounterIncr: Boolean)
    fun stopAdvertising()
}



@Composable
fun Main(navController: NavController){
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .verticalScroll(
                state = ScrollState(0),
                enabled = true
            )
    ) {
        Btn(navController = navController, "showallpacks", "Show all packs")
        Btn(navController = navController, "create/${32}", "Create new triol")
        Btn(navController = navController, "create/${64}", "Create new stationary")
        Btn(navController = navController, "create/${-128}", "Create new transport")
    }
}



@Composable
fun Btn(navController: NavController, route: String, btnText: String){
    val green = Color(red = 0x4D, green = 0xB6, blue = 0xAC, alpha = 0xFF)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = green),
            onClick = {
                navController.navigate(route)
            }
        ) {
            Text(
                text = btnText,
                color = Color.White
            )
        }
    }
}

@Composable
fun BtPackage(clickListener: ClickListener, viewModel: PacksViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main"){
        composable("main"){ Main(navController = navController)}

        composable("showallpacks"){
            ShowAllPacks(navController = navController, viewModel = viewModel)}

        composable("create/{transType}",
            arguments = listOf(navArgument("transType"){type = NavType.IntType})){
                backStackEntry ->
                CreateNew(
                    navController = navController,
                    viewModel = viewModel,
                    clickListener = clickListener,
                    backStackEntry.arguments?.getInt("transType")!!
                )
        }

        composable("show"){
            ShowPack(
                navController = navController,
                viewModel = viewModel,
                clickListener = clickListener
            )
        }
    }
}
