package ru.kotofeya.bleadvertiser

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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.*

@Composable
fun ShowAllPacks(navController: NavController, viewModel: PacksViewModel) {

    viewModel.loadAllPacks()

    val state = remember{
        mutableStateListOf<PackModel>()
    }

    viewModel.loadAllPacks()
    val list = viewModel.packsList.value
    state.clear()
    list?.let { state.addAll(it) }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = state,
            key = { _, item ->
                item.uid!!
            }) { _, item ->
            PackRow(packModel = item, navController, viewModel, state)
        }
    }
}


@Composable
fun PackRow(
    packModel: PackModel,
    navController: NavController,
    viewModel: PacksViewModel,
    state: SnapshotStateList<PackModel>){
    Row(modifier = Modifier
        .fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)

                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { /* Called when the gesture starts */ },
                        onDoubleTap = {
                            packModel.uid?.let { it1 -> viewModel.deletePack(it1) }
                            state.remove(packModel)
                        },
                        onLongPress = {

                        },
                        onTap = {
                            packModel.uid?.let { it1 -> viewModel.getPackById(it1) }
                            navController.navigate("show/${packModel.uid}")
                        }
                    )
                }
        ) {
            Text(
                text = packModel.uid.toString(),
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

