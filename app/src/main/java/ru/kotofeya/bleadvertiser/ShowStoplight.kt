package ru.kotofeya.bleadvertiser

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ShowStoplight (navController: NavController,
                   viewModel: PacksViewModel,
                   packId: Int?){
    val selectedPack = viewModel.selectedPack.value
    selectedPack?.let { StoplightPackage(pack = it, viewModel = viewModel, navController = navController)
    }

}