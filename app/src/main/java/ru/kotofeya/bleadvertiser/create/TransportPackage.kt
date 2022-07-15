package ru.kotofeya.bleadvertiser.create

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import ru.kotofeya.bleadvertiser.ClickListener
import ru.kotofeya.bleadvertiser.PackModel
import ru.kotofeya.bleadvertiser.PacksViewModel

@Composable
fun TransportPackage(pack: PackModel,
                     viewModel: PacksViewModel,
                     navController: NavController,
                     clickListener: ClickListener
) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .verticalScroll(
                state = ScrollState(0),
                enabled = true
            )
    ){
        val packName = pack.name
        val packNameState = remember { mutableStateOf(packName) }

        val packArray = pack.pack
        val deviceNameState = remember { mutableStateOf("stp") }
        val btVersionState = remember { mutableStateOf(packArray?.get(0).toString()) }

        val byte2 = packArray!![2].toInt()
        val byte3 = packArray[3].toInt()
        val byte4 = packArray[4].toInt()
        val serial = (byte2 and 0xff shl 16) or (byte3 and 0xff shl 8) or (byte4 and 0xff)
        val serialState = remember { mutableStateOf(serial.toString()) }
        val transTypeState = remember { mutableStateOf(packArray[5].toString()) }

        val buzzersState = remember { mutableStateOf(packArray[6].toString()) }
        val incrementState = remember { mutableStateOf(packArray[7].toString()) }

        val transportTypeState = remember { mutableStateOf(packArray[14].toString()) }
        val litera3State = remember { mutableStateOf(packArray[15].toString()) }

        val byte16 = packArray[16].toInt()
        val byte17 = packArray[17].toInt()
        val route = ((byte16 shl 8)) + (byte17)
        val routeState = remember { mutableStateOf(route.toString()) }

        val litera1State = remember { mutableStateOf(packArray[18].toString()) }
        val litera2State = remember { mutableStateOf(packArray[19].toString()) }

        val s = remember { mutableStateOf("0") }

        DataRowString(text = "Название", state = packNameState)
        DataRow("Имя устройства", deviceNameState)
        DataRow(text = "(10) Версия bt пакета", state = btVersionState)
        DataRow(text = "(11) Резерв", state = s)
        DataRow(text = "(12-14) Серийный номер", state = serialState)
        DataRow(text = "(15) Тип трансивера", state = transTypeState)
        DataRow(text = "(16) Состояние звуковых маяков", state = buzzersState)
        DataRow(text = "(17) Инкременты вызова", state = incrementState)
        DataRow(text = "(18-23) Резерв", state = s)
        DataRow(text = "(24) Тип транспортного средства", state = transportTypeState)
        DataRow(text = "(25) Литера 3", state = litera3State)
        DataRow(text = "(26-27) Номер маршрута", state = routeState)
        DataRow(text = "(28) Литера 1", state = litera1State)
        DataRow(text = "(29) Литера 2", state = litera2State)
        DataRow(text = "(30-31) Резерв", state = s)

        fun setPackValues(){
            pack.setPackName(packNameState.value)
            pack.setTransportArrayValues(
                btVersionState.value.toInt(),
                serialState.value.toInt(),
                transTypeState.value.toInt(),
                buzzersState.value.toInt(),
                incrementState.value.toInt(),
                transportTypeState.value.toInt(),
                litera3State.value.toInt(),
                routeState.value.toInt(),
                litera1State.value.toInt(),
                litera2State.value.toInt()
            )
        }

        fun saveOrUpdate(){
            if(pack.uid == null || pack.uid == 0){
                viewModel.saveNewPack(pack)
            } else {
                viewModel.updatePack(pack)
            }
            navController.popBackStack()
        }

        fun startAdv(){
            val byteArr = pack.pack
            byteArr?.let { it1 -> clickListener.startAdvertising(it1,
                changeTime = false,
                changeCounterIncr = false
            )}
        }

        SaveOrUpdateButton(setPackValues = {setPackValues()}, saveOrUpdate = {saveOrUpdate()})
        StartAdvertisingButton(setPackValues = {setPackValues()}, startAdv = {startAdv()})
        StopAdvertisingButton(clickListener = clickListener)
        ReturnButton(navController = navController)
    }
}