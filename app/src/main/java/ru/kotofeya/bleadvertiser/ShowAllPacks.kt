package ru.kotofeya.bleadvertiser

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import java.util.*

@Composable
fun ShowAllPacks(navController: NavController, viewModel: PacksViewModel) {

    var state = remember{
        mutableStateOf(viewModel.packsList.value)
    }



    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(items = state.value!!) { index, item ->
            PackRow(packModel = item, navController, viewModel, state)
        }
    }
}


@Composable
fun PackRow(
    packModel: PackModel,
    navController: NavController,
    viewModel: PacksViewModel,
    state: MutableState<List<PackModel>?>
){
    Row(modifier = Modifier
        .fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
//                .clickable {
//                    packModel.id?.let {
//                        viewModel.getPackById(it)
//                        navController.navigate("showpack/${packModel.id}")
//                    }
//                }

                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { /* Called when the gesture starts */ },
                        onDoubleTap = {
                            Log.d("TAG", "onLongPress, id: ${packModel.id}")
                            viewModel.deletePack(packModel)
                            Log.d("TAG", "onLongPress, state1: ${state.value}")
                            var list = (state.value)?.filter { it.id != packModel.id }

                            state.value = list


                            Log.d("TAG", "onLongPress, state2: ${state.value}")
                        },
                        onLongPress = {

                        },
                        onTap = {
                            packModel.id?.let {
                                viewModel.getPackById(it)
                                navController.navigate("showpack/${packModel.id}")
                            }
                        }
                    )

                }


        ) {
            Text(
                text = packModel.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
                    .background(Color.LightGray),
            )
            Text(
                text = Arrays.toString(packModel.pack),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp),
            )
        }
    }
}

