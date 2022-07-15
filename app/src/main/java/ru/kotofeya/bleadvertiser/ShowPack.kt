package ru.kotofeya.bleadvertiser

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import ru.kotofeya.bleadvertiser.create.StationaryPackage
import ru.kotofeya.bleadvertiser.create.StoplightPackage
import ru.kotofeya.bleadvertiser.create.TransportPackage

@Composable
fun ShowPack (
    navController: NavController,
    viewModel: PacksViewModel,
    clickListener: ClickListener){

    val selectedPack = viewModel.selectedPack.value
    selectedPack?.let {
        Column(modifier = Modifier
            .fillMaxSize()) {

            when(selectedPack.getTransType()?.toInt()){
                32 -> StoplightPackage(
                    pack = it,
                    viewModel = viewModel,
                    navController = navController,
                    clickListener = clickListener)

                64 -> StationaryPackage(
                    pack = it,
                    viewModel = viewModel,
                    navController = navController,
                    clickListener = clickListener)

                -128 -> TransportPackage(
                    pack = it,
                    viewModel = viewModel,
                    navController = navController,
                    clickListener = clickListener)
            }
        }
    }
}