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
import ru.kotofeya.bleadvertiser.getTimeFromByteArray

@Composable
fun StoplightPackage(pack: PackModel,
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
    val streetId = ((byte12 shl 8)) + (byte13)
    val streetIdState = remember { mutableStateOf(streetId.toString()) }

    val time = getTimeFromByteArray(packArray)
    val timeState = remember { mutableStateOf(time.toString()) }

    val streetSideState = remember { mutableStateOf(packArray[16].toString()) }

    val s = remember { mutableStateOf("0") }

    val changeTimeState = remember{ mutableStateOf(false)}
    val changeIncrState = remember{ mutableStateOf(false)}

    DataRowString(text = "????????????????", state = packNameState)
    DataRow("?????? ????????????????????", deviceNameState)
    DataRow(text = "(10) ???????????? bt ????????????", state = btVersionState)
    DataRow(text = "(11) ????????????", state = s)
    DataRow(text = "(12-14) ???????????????? ??????????", state = serialState)
    DataRow(text = "(15) ?????? ????????????????????", state = transTypeState)
    DataRow(text = "(16) ?????????? ???????????? ??????????????????", state = buzzersState)
    DataRow(text = "(17) ???????????????????? ??????????????????/????????????", state = incrementState)
    DataRow(text = "(18-21) ??????????", state = timeState)
    DataRow(text = "(22-23) Id ??????????", state = streetIdState)
    DataRow(text = "(24-25) ????????????", state = s)
    DataRow(text = "(26) ?????????????? ??????????", state = streetSideState)
    DataRow(text = "(27-31) ????????????", state = s)

    CheckBoxRow(text = "???????????? ?????????? ???????????? 1 ??????", state = changeTimeState)
    CheckBoxRow(text = "???????????? counterStatus ???????????? 1 ??????", state = changeIncrState)

        fun setPackValues(){
            pack.setPackName(packNameState.value)
            pack.setStoplightArrayValues(
                btVersionState.value.toInt(),
                serialState.value.toInt(),
                transTypeState.value.toInt(),
                buzzersState.value.toInt(),
                incrementState.value.toInt(),
                timeState.value.toLong(),
                streetIdState.value.toInt(),
                streetSideState.value.toInt()
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
                changeTimeState.value,
                changeIncrState.value)}
        }
    
        SaveOrUpdateButton(setPackValues = { setPackValues()}, saveOrUpdate = { saveOrUpdate()})
        StartAdvertisingButton(setPackValues = {setPackValues()}, startAdv = {startAdv()})
        StopAdvertisingButton(clickListener = clickListener)
        ReturnButton(navController = navController)
    }
}