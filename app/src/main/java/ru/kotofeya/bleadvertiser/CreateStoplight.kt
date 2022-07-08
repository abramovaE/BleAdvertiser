package ru.kotofeya.bleadvertiser

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun CreateStoplight(navController: NavController, viewModel: PacksViewModel, clickListener: ClickListener) {
    val pack = PackModel(null, "name", ByteArray(22) { 0 })
    StoplightPackage(pack = pack, viewModel = viewModel, navController = navController, clickListener = clickListener)
}


@Composable
fun StoplightPackage(pack: PackModel,
                     viewModel: PacksViewModel,
                     navController: NavController,
                     clickListener: ClickListener) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .verticalScroll(
                state = ScrollState(0),
                enabled = true
            )
    ){
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

    CheckBoxRow(text = "Менять время каждую 1 сек", state = changeTimeState)
    CheckBoxRow(text = "Менять counterStatus каждую 1 сек", state = changeIncrState)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                pack.setArrayValues(
                    btVersionState.value.toInt(),
                    serialState.value.toInt(),
                    transTypeState.value.toInt(),
                    buzzersState.value.toInt(),
                    incrementState.value.toInt(),
                    timeState.value.toLong(),
                    streetIdState.value.toInt(),
                    streetSideState.value.toInt()
                )
                if(pack.id == null || pack.id == 0){
                    viewModel.saveNewPack(pack)
                } else {
                    viewModel.updatePack(pack)
                }
                navController.popBackStack()
            }
        ) {
            Text(
                text = "Save and return"
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
                    pack.setArrayValues(
                        btVersionState.value.toInt(),
                        serialState.value.toInt(),
                        transTypeState.value.toInt(),
                        buzzersState.value.toInt(),
                        incrementState.value.toInt(),
                        timeState.value.toLong(),
                        streetIdState.value.toInt(),
                        streetSideState.value.toInt()
                    )
                    val byteArr = pack.pack
                    byteArr?.let { it1 -> clickListener.startAdvertising(it1,
                        changeTimeState.value,
                        changeIncrState.value)}
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



@Composable
fun DataRow(text: String, state : MutableState<String>){
    val lightGreen = Color(red = 0xAE, green = 0xD5, blue = 0x81, alpha = 0xFF)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, start = 5.dp, end = 5.dp)
            .background(Color.White)
    ) {
        Text(
            modifier = Modifier
                .width(300.dp)
                .align(Alignment.CenterVertically)
                .padding(start = 10.dp, top = 5.dp, bottom = 5.dp),
            text = text
        )
        BasicTextField(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
                .background(lightGreen)
                .padding(5.dp),
            value = state.value,
            onValueChange = { state.value = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

@Composable
fun CheckBoxRow(text: String, state: MutableState<Boolean>){
    val lightGreen = Color(red = 0xAE, green = 0xD5, blue = 0x81, alpha = 0xFF)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, start = 5.dp, end = 5.dp)
            .background(Color.White)
    ) {
        Text(
            modifier = Modifier
                .width(300.dp)
                .align(Alignment.CenterVertically)
                .padding(start = 10.dp, top = 5.dp, bottom = 5.dp),
            text = text
        )
        Checkbox(modifier = Modifier.fillMaxSize()
            , colors = CheckboxDefaults.colors(checkedColor = lightGreen, checkmarkColor = Color.Black)
            , checked = state.value
            , onCheckedChange = {state.value = it}
        )
    }
}