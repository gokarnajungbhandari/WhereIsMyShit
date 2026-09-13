package com.example.whereismyshit.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.whereismyshit.data.Shit
import com.example.whereismyshit.viewmodel.ShitViewModel

@Composable
fun WhereIsMyShitApp(viewModel: ShitViewModel) {

    val navController = rememberNavController()

    val containerStack = remember {
        mutableStateListOf<Shit>()
    }

    var selectedContainerForQr by remember {
        mutableStateOf<Set<Shit>>(emptySet())
    }

    fun changeStack(parentStack: List<Shit>){
        containerStack.clear()
        containerStack.addAll(parentStack)
    }

    fun RemoveLastContainerFromTheStack(){
        containerStack.removeAt(
            containerStack.lastIndex
        )
    }

    fun AddContainerInTheStack(shit: Shit){
        containerStack.add(shit)

    }

    fun GetLastContainerOrNullFromStack() : Shit? {
        return containerStack.lastOrNull()
    }

    fun GetParentPath(): String{
        return containerStack.joinToString("/") {
            it.name
        }
    }

    fun removeQr(shit: Shit){
        selectedContainerForQr = selectedContainerForQr - shit
    }

    fun addQr(shit: Shit){
        selectedContainerForQr = selectedContainerForQr + shit
    }

    fun clearAllQr(){
        selectedContainerForQr = emptySet()
    }

    fun qrAvailableToPrint(): Boolean {
        return (selectedContainerForQr.size < 12)
    }

    fun qrAlreadyAdded(id: Int): Boolean{
        for (shit in selectedContainerForQr){
            if(shit.id == id){
                return true
            }
        }
        return false
    }

    fun getAllSelectedToPrintQrCodes(): Set<Shit>{
        return selectedContainerForQr
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(viewModel = viewModel,
                changeStack = ::changeStack,
                navController = navController,
                addQr = ::addQr,
                removeQr = ::removeQr,
                qrAvailableToPrint = ::qrAvailableToPrint,
                qrAlreadyAdded = ::qrAlreadyAdded)
        }

        composable("containers") {
            ShitScreen(viewModel = viewModel,
                navController = navController,
                getParentpath = :: GetParentPath,
                :: RemoveLastContainerFromTheStack,
                :: AddContainerInTheStack,
                getLastContainerOrNullFromStack = :: GetLastContainerOrNullFromStack,
                addQr = ::addQr,
                removeQr = ::removeQr,
                qrAvailableToPrint = ::qrAvailableToPrint,
                qrAlreadyAdded = ::qrAlreadyAdded)
        }

        composable("printQR") {
            QrPrintScreen(viewModel = viewModel,
                navController = navController,
                getParentpath = :: GetParentPath,
                :: AddContainerInTheStack,
                allSelectedToPrintQrCodes = selectedContainerForQr,
                getLastContainerOrNullFromStack = :: GetLastContainerOrNullFromStack,
                clearAllQrCodes = ::clearAllQr,
                addQr = ::addQr,
                removeQr = ::removeQr,
                qrAvailableToPrint = ::qrAvailableToPrint,
                qrAlreadyAdded = ::qrAlreadyAdded)
        }


    }
}