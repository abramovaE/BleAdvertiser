package ru.kotofeya.bleadvertiser

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun ShowStoplight (
    navController: NavController,
    viewModel: PacksViewModel,
    clickListener: ClickListener
){
    val selectedPack = viewModel.selectedPack.value
    selectedPack?.let {

        Column(modifier = Modifier
            .fillMaxSize()) {
            StoplightPackage(pack = it,
                viewModel = viewModel,
                navController = navController, clickListener = clickListener)
        }
    }

}