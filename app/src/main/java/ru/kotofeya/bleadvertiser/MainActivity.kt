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
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat

/*
Структура пакета:
stationars:
1-9: Байты отведены под имя устройства
10: Версия BLE пакета (внутренняя)
11: Резерв
12-14: Объявление серийного номера трансивера (0 -  16 777 216)
15: Тип трансивера: 20 - Триоль (УЗГС); 40 -Стационар; 80 - Транспорт
16: Объявление состояния звуковых маяков, по два бита слева направо 1, 2, 3, 4.
17: Флаги нулевого звукового маяка(0 ВЗ), инкремента вызова
18-21: CRC сумма - считывается из JSON файла расположенного в /var/www/html/data/text_files/*.json
22-23: Индекс номер города - считывается из JSON файла расположенного в /var/www/html/data/city.json на основании имени файла с транспортным контентом в пункте описанном выше
24-25: Тип стационарного объекта (подробнее по ссылке.) получается при инициализации системы.
26: Этаж - назначен STM (получается при инициализации системы
27-31: Резервные байты

transport:

1-9: Байты отведены под имя устройства
10: Версия BLE пакета (внутренняя)
11: Резерв
12-14: Объявление серийного номера трансивера (0 -  16 777 216)
15: Тип трансивера: 20 - Триоль (УЗГС); 40 -Стационар; 80 - Транспорт
16: Объявление состояния звуковых маяков, по два бита слева направо 1, 2, 3, 4.
17: Флаги - состояние дверей, направление движения, инкрементное число вызова (изменяется при каждом вызове, увеличиваясь на единицу)
24: Тип транспортного средства (подробнее по ссылке).
25: Третья литера
26-27: Номер маршрута
28: Первая литера
29: Вторая литера
30-31: Резервные байты

triol:

 1-9: Байты отведены под имя устройства
10: Версия BLE пакета (внутренняя)
11: Резерв
12-14: Объявление серийного номера трансивера (0 - 16 777 216)
15: Тип трансивера: 20 - Триоль (УЗГС); 40 -Стационар; 80 - Транспорт
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
fun DataRow(text: String, state : MutableState<String>){
    val lightGreen: Color = Color(red = 0xAE, green = 0xD5, blue = 0x81, alpha = 0xFF)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, start = 5.dp, end = 5.dp)
            .background(Color.White)
    ) {
        Text(
            modifier = Modifier
                .width(300.dp)
                .align(CenterVertically)
                .padding(start = 10.dp, top = 5.dp, bottom = 5.dp),
            text = text
        )
        BasicTextField(
            modifier = Modifier
                .fillMaxHeight()
                .align(CenterVertically)
                .background(lightGreen)
                .padding(5.dp),
            value = state.value,
            onValueChange = { state.value = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}




@Composable
//@Preview
fun BtPackage(clickListener: ClickListener) {
    Column(
        modifier = Modifier
            .background(Color.Yellow)
            .fillMaxSize()
            .verticalScroll(
                state = ScrollState(0),
                enabled = true
            )
    ) {
        val state = remember {
//            mManufacturerSpecificData={65535=[1, 0, 0, 26, -45, 64, 85, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]}
            val bytes = byteArrayOf(1, 0, 0, 26, -45, 64, 85, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            val stringBuilder = StringBuilder()
            bytes.forEach { stringBuilder.append(it).append(" ") }
            mutableStateOf(stringBuilder.toString())
        }
        val deviceNameState = remember { mutableStateOf("stp") }
        val btVersionState = remember { mutableStateOf("") }
        val serialState = remember{ mutableStateOf("") }
        val transTypeState = remember{ mutableStateOf("") }
        val buzzersState = remember{ mutableStateOf("") }
        val incrementState = remember{ mutableStateOf("") }
        val s = remember{ mutableStateOf("") }


        DataRow("Имя устройства", deviceNameState)
        DataRow(text = "(10) Версия bt пакета", state = btVersionState)
        DataRow(text = "(11) ", state = s)
        DataRow(text = "(12-14) Серийный номер", state = serialState)
        DataRow(text = "(15) Тип трансивера", state = transTypeState)
        DataRow(text = "(16) Состояние маяков", state = buzzersState)
        DataRow(text = "(17) Инкремент вызова", state = incrementState)
        DataRow(text = "(18) ", state = s)
        DataRow(text = "(19) ", state = s)
        DataRow(text = "(20) ", state = s)
        DataRow(text = "(21) ", state = s)
        DataRow(text = "(22) ", state = s)
        DataRow(text = "(23) ", state = s)
        DataRow(text = "(24) ", state = s)
        DataRow(text = "(25) ", state = s)
        DataRow(text = "(26) ", state = s)
        DataRow(text = "(27) ", state = s)
        DataRow(text = "(28) ", state = s)
        DataRow(text = "(29) ", state = s)
        DataRow(text = "(30) ", state = s)
        DataRow(text = "(31) ", state = s)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    var sb = java.lang.StringBuilder()
                    sb.append(btVersionState.value).append(" ")
                    sb.append(s.value).append(" ")
                    sb.append(serialState.value).append(" ")
                    sb.append(transTypeState.value).append(" ")
                    sb.append(buzzersState.value).append( " ")
                    sb.append(incrementState.value).append(" ")
                    sb.append(s.value).append(" ")
                    sb.append(s.value).append(" ")
                    sb.append(s.value).append(" ")
                    sb.append(s.value).append(" ")
                    sb.append(s.value).append(" ")
                    sb.append(s.value).append(" ")
                    sb.append(s.value).append(" ")
                    sb.append(s.value).append(" ")
                    sb.append(s.value).append(" ")
                    sb.append(s.value).append(" ")
                    sb.append(s.value).append(" ")
                    sb.append(s.value).append(" ")
                    sb.append(s.value).append(" ")
                    sb.append(s.value).append(" ")
                    val pack = sb.toString()
                    clickListener.startAdvertising(pack)
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
//                    clickListener.stopAdvertising()
                }
            ) {
                Text(
                    text = "Stop advertising"
                )
            }
        }
    }
}
