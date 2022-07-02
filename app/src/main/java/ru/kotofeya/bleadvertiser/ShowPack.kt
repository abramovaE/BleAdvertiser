package ru.kotofeya.bleadvertiser

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ShowPack (navController: NavController,
              viewModel: PacksViewModel,
              packId: Int?){

    val selectedPack = viewModel.selectedPack.value



    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .verticalScroll(
                state = ScrollState(0),
                enabled = true
            )
    ) {
        val packArray = selectedPack?.pack

        val deviceNameState = remember { mutableStateOf("stp") }
        val btVersionState = remember { mutableStateOf(packArray?.get(0).toString()) }

        val byte2 = packArray!!.get(2).toInt()
        val byte3 = packArray.get(3).toInt()
        val byte4 = packArray!!.get(4)
        val serial = byte2 shl 16 + byte3 shl 8 + byte4
        val serialState = remember{ mutableStateOf(serial.toString()) }

        val transTypeState = remember{ mutableStateOf(packArray?.get(5).toString()) }
        val buzzersState = remember{ mutableStateOf(packArray?.get(6).toString()) }
        val incrementState = remember{ mutableStateOf(packArray?.get(7).toString()) }

        val byte12 = packArray!!.get(12).toInt()
        val byte13 = packArray!!.get(13)
        val streetId = (byte12 shl 8) + byte13
        val streetIdState = remember{ mutableStateOf(streetId.toString()) }


        val byte8 = packArray!!.get(8).toInt()
        val byte9 = packArray!!.get(9).toInt()
        val byte10 = packArray!!.get(10).toInt()
        val byte11 = packArray!!.get(11)
        val time = byte8 shl 24 + byte9 shl 16 + byte10 shl 8 + byte11
        val timeState = remember{ mutableStateOf(time.toString()) }

        val streetSideState = remember{ mutableStateOf(packArray?.get(16).toString()) }

        val s = remember{ mutableStateOf("0") }

        DataRow("Имя устройства", deviceNameState)
        DataRow(text = "(10) Версия bt пакета", state = btVersionState)
        DataRow(text = "(11) Резерв", state = s)
        DataRow(text = "(12-14) Серийный номер", state = serialState)
        DataRow(text = "(15) Тип трансивера", state = transTypeState)
        DataRow(text = "(16) Режим работы светофора", state = buzzersState)
        DataRow(text = "(17) Инкременты состояния/вызова", state = incrementState)
        DataRow(text = "(18-21) Время", state = timeState)
        DataRow(text = "(22-23) Id улицы", state = streetIdState)
        DataRow(text = "(24-25) Резерв", state = s)
        DataRow(text = "(26) Сторона улицы", state = streetSideState)
        DataRow(text = "(27-31) Резерв", state = s)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    val byteArr = ByteArray(22)

                    byteArr[0] = btVersionState.value.toByte()
                    byteArr[1] = s.value.toByte()

                    val serial = serialState.value.toInt()
                    val serial0 = (serial shr 16).toByte()
                    val serial1 = (serial shr 8).toByte()
                    val serial2 = serial.toByte()
                    byteArr[2] = serial0
                    byteArr[3] = serial1
                    byteArr[4] = serial2

                    byteArr[5] = transTypeState.value.toByte()
                    byteArr[6] = buzzersState.value.toByte()
                    byteArr[7] = incrementState.value.toByte()

                    val time = timeState.value.toInt()
                    val time0 = (time shr 24).toByte()
                    val time1 = (time shr 16).toByte()
                    val time2 = (time shr 8).toByte()
                    val time3 = time.toByte()
                    byteArr[8] = time0
                    byteArr[9] = time1
                    byteArr[10] = time2
                    byteArr[11] = time3

                    val streetId = streetIdState.value.toInt()
                    val streetId0 = (streetId shr 8).toByte()
                    val streetId1= streetId.toByte()
                    byteArr[12] = streetId0
                    byteArr[13] = streetId1

                    byteArr[14] = s.value.toByte()
                    byteArr[15] = s.value.toByte()

                    byteArr[16] = streetSideState.value.toByte()

                    byteArr[17] = s.value.toByte()
                    byteArr[18] = s.value.toByte()
                    byteArr[19] = s.value.toByte()
                    byteArr[20] = s.value.toByte()
                    byteArr[21] = s.value.toByte()

                    selectedPack.pack = byteArr
                    viewModel.updatePack(selectedPack)
                    navController.popBackStack()
                }
            ) {
                Text(
                    text = "Save and return"
                )
            }
        }
    }

}