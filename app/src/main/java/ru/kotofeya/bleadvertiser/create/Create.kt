package ru.kotofeya.bleadvertiser.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.kotofeya.bleadvertiser.ClickListener

val dataRowModifier = Modifier
    .fillMaxWidth()
    .padding(top = 5.dp, start = 5.dp, end = 5.dp)
    .background(Color.White)

val btnRowModifier = Modifier
    .fillMaxWidth()
    .padding(10.dp)

val textModifier = Modifier
    .width(250.dp)
    .padding(start = 10.dp, top = 5.dp, bottom = 5.dp)


val lightGreen = Color(red = 0xAE, green = 0xD5, blue = 0x81, alpha = 0xFF)
val green = Color(red = 0x4D, green = 0xB6, blue = 0xAC, alpha = 0xFF)

@Composable
fun DataRow(text: String, state : MutableState<String>){
    Row(modifier = dataRowModifier) {
        Text(
            modifier = textModifier
                .align(Alignment.CenterVertically),
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
fun DataRowString(text: String, state : MutableState<String>){
    Row(modifier = dataRowModifier) {
        Text(
            modifier = textModifier
                .align(Alignment.CenterVertically),
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
        )
    }
}

@Composable
fun CheckBoxRow(text: String, state: MutableState<Boolean>){
    Row(modifier = dataRowModifier) {
        Text(
            modifier = textModifier
                .align(Alignment.CenterVertically),
            text = text
        )
        Checkbox(modifier = Modifier.fillMaxSize()
            , colors = CheckboxDefaults.colors(checkedColor = lightGreen, checkmarkColor = Color.Black)
            , checked = state.value
            , onCheckedChange = {state.value = it}
        )
    }
}

@Composable
fun StartAdvertisingButton(setPackValues: () -> (Unit), startAdv: () -> (Unit)){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = green),
            onClick = {
                setPackValues()
                startAdv()
            }
        ) {
            Text(
                text = "Start advertising",
                color = Color.White
            )
        }
    }
}

@Composable
fun StopAdvertisingButton(clickListener: ClickListener){
    Row(modifier = btnRowModifier) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = green),
            onClick = {
                clickListener.stopAdvertising()
            }
        ) {
            Text(
                text = "Stop advertising",
                color = Color.White
            )
        }
    }
}

@Composable
fun SaveOrUpdateButton(setPackValues: () -> (Unit), saveOrUpdate: () -> (Unit)){
    Row(btnRowModifier) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = green),
            onClick = {
                setPackValues()
                saveOrUpdate()
            }
        ) {
            Text(
                text = "Save and return",
                color = Color.White
            )
        }
    }
}


@Composable
fun ReturnButton(navController: NavController){
    Row(modifier = btnRowModifier) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = green),
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text(
                text = "Return",
                color = Color.White
            )
        }
    }
}
