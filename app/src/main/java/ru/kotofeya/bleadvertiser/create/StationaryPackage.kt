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
import ru.kotofeya.bleadvertiser.getCRCFromByteArray

@Composable
fun StationaryPackage(pack: PackModel,
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

        val byte12 = packArray[12].toInt()
        val byte13 = packArray[13].toInt()
        val cityId = ((byte12 shl 8)) + (byte13)
        val cityIdState = remember { mutableStateOf(cityId.toString()) }

        val crc = getCRCFromByteArray(packArray)
        val crcState = remember { mutableStateOf(crc.toString()) }

        val byte14 = packArray[14].toInt()
        val byte15 = packArray[15].toInt()
        val stationaryType = ((byte14 shl 8)) + (byte15)
        val stationaryTypeState = remember { mutableStateOf(stationaryType.toString()) }

        val floorState = remember { mutableStateOf(packArray[16].toString()) }

        val s = remember { mutableStateOf("0") }

        DataRowString(text = "????????????????", state = packNameState)
        DataRow("?????? ????????????????????", deviceNameState)
        DataRow(text = "(10) ???????????? bt ????????????", state = btVersionState)
        DataRow(text = "(11) ????????????", state = s)
        DataRow(text = "(12-14) ???????????????? ??????????", state = serialState)
        DataRow(text = "(15) ?????? ????????????????????", state = transTypeState)
        DataRow(text = "(16) ?????????????????? ???????????????? ????????????", state = buzzersState)
        DataRow(text = "(17) ???????????????????? ????????????", state = incrementState)
        DataRow(text = "(18-21) CRC", state = crcState)
        DataRow(text = "(22-23) ???????????? ????????????", state = cityIdState)
        DataRow(text = "(24-25) ?????? ?????????????????????????? ??????????????", state = stationaryTypeState)
        DataRow(text = "(26) ????????", state = floorState)
        DataRow(text = "(27-31) ????????????", state = s)

        fun setPackValues(){
            pack.setPackName(packNameState.value)
            pack.setStationaryArrayValues(
                btVersionState.value.toInt(),
                serialState.value.toInt(),
                transTypeState.value.toInt(),
                buzzersState.value.toInt(),
                incrementState.value.toInt(),
                crcState.value.toLong(),
                cityIdState.value.toInt(),
                stationaryTypeState.value.toInt(),
                floorState.value.toInt()
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