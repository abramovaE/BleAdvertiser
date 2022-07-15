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

    var state = remember{
        mutableStateListOf<PackModel>()
    }

    viewModel.loadAllPacks()
    var list = viewModel.packsList.value
    state.clear()
    list?.let { state.addAll(it) }


    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        Log.d("TAG", "LazyColumn(), state: ${state.size}")

        itemsIndexed(
            items = state,
            key = {
                index, item ->
                item.uid!!
            })
        { index, item ->
            PackRow(packModel = item, navController, viewModel, state, index, item.uid!!)
        }

    }
}


@Composable
fun PackRow(
    packModel: PackModel,
    navController: NavController,
    viewModel: PacksViewModel,
    state: SnapshotStateList<PackModel>,
    index: Int,
    uid: Int
){
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
                            Log.d("TAG", "onLongPress, id: ${uid}")
                            viewModel.deletePack(uid)
//                            viewModel.loadAllPacks()
                            state.remove(packModel)
                        },
                        onLongPress = {

                        },
                        onTap = {
                            packModel.uid?.let {
                                viewModel.getPackById(it)
                                navController.navigate("showpack/${packModel.uid}")
                            }
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

