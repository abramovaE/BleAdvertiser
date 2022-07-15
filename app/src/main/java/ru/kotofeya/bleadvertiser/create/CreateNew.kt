package ru.kotofeya.bleadvertiser.create

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import ru.kotofeya.bleadvertiser.ClickListener
import ru.kotofeya.bleadvertiser.PackModel
import ru.kotofeya.bleadvertiser.PacksViewModel

@Composable
fun CreateNew(navController: NavController,
              viewModel: PacksViewModel,
              clickListener: ClickListener,
              transType: Int) {
    val byteArray = ByteArray(22) { 0 }
    byteArray[0] = 1
    byteArray[5] = transType.toByte()

    val transpPack = PackModel(null, "Transp", byteArray)
    val stoplightPack = PackModel(null, "Triol", byteArray)
    val stationaryPack = PackModel(null, "Stationary", byteArray)

    when(transType){
        32 -> StoplightPackage(
            pack = stoplightPack,
            viewModel = viewModel,
            navController = navController,
            clickListener = clickListener)

        64 -> StationaryPackage(
            pack = stationaryPack,
            viewModel = viewModel,
            navController = navController,
            clickListener = clickListener)

        -128 -> TransportPackage(
            pack = transpPack,
            viewModel = viewModel,
            navController = navController,
            clickListener = clickListener)
    }
}