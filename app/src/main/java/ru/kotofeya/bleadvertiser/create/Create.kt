package ru.kotofeya.bleadvertiser.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.kotofeya.bleadvertiser.ClickListener

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
fun DataRowString(text: String, state : MutableState<String>){
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

@Composable
fun StartAdvertisingButton(setPackValues: () -> (Unit), startAdv: () -> (Unit)){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                setPackValues()
                startAdv()
            }
        ) {
            Text(
                text = "Start advertising"
            )
        }
    }
}

@Composable
fun StopAdvertisingButton(clickListener: ClickListener){
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

@Composable
fun SaveOrUpdateButton(setPackValues: () -> (Unit), saveOrUpdate: () -> (Unit)){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                setPackValues()
                saveOrUpdate()
            }
        ) {
            Text(
                text = "Save and return"
            )
        }
    }
}

@Composable
fun ReturnButton(navController: NavController){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text(
                text = "Return"
            )
        }
    }
}