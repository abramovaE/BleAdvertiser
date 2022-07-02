package ru.kotofeya.bleadvertiser

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.*

@Composable
fun ShowAllPacks(navController: NavController, viewModel: PacksViewModel) {

    viewModel.loadAllPacks()
    var list = viewModel.packsList.value
    if(list == null){
        list = emptyList()
    }

    for (it in list){
        Log.d("TAG", it.name + " " + Arrays.toString(it.pack))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(
            list
        ) { _, item ->
            PackRow(packModel = item, navController, viewModel)
        }
    }
}


@Composable
fun PackRow(packModel: PackageEntity, navController: NavController, viewModel: PacksViewModel){
    Row(modifier = Modifier
        .fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
                .clickable {
                    packModel.id?.let {
                        viewModel.getPackById(it)
                        navController.navigate("showpack/${packModel.id}")
                    }
                }
        ) {
            packModel.name?.let {
                Text(
                    text = it,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp)
                        .background(Color.LightGray),
                )
            }
            Text(
                text = Arrays.toString(packModel.pack),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp),
            )
        }
    }
}

