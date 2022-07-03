package ru.kotofeya.bleadvertiser

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CreateStoplight(navController: NavController, viewModel: PacksViewModel) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .verticalScroll(
                state = ScrollState(0),
                enabled = true
            )
    ) {
        val deviceNameState = remember { mutableStateOf("stp") }
        val btVersionState = remember { mutableStateOf("0") }
        val serialState = remember{ mutableStateOf("0") }
        val transTypeState = remember{ mutableStateOf("0") }
        val buzzersState = remember{ mutableStateOf("0") }
        val incrementState = remember{ mutableStateOf("0") }
        val streetIdState = remember{ mutableStateOf("0") }
        val timeState = remember{ mutableStateOf("0") }
        val streetSideState = remember{ mutableStateOf("0") }

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
                    val packageModel = PackModel(null, "name", null)
                    packageModel.setArrayValues(
                        btVersionState.value.toByte(),
                        serialState.value.toInt(),
                        transTypeState.value.toByte(),
                        buzzersState.value.toByte(),
                        incrementState.value.toByte(),
                        timeState.value.toInt(),
                        streetIdState.value.toInt(),
                        streetSideState.value.toByte())

                    viewModel.saveNewPack(packageModel)
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