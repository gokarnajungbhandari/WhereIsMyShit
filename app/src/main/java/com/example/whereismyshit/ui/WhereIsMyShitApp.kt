package com.example.whereismyshit.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.example.whereismyshit.data.Shit
import com.example.whereismyshit.viewmodel.ShitViewModel

@Composable
fun WhereIsMyShitApp(viewModel: ShitViewModel) {

    val navController = rememberNavController()

    val containerStack = remember {
        mutableStateListOf<Shit>()
    }

    fun changeStack(parentStack: List<Shit>){
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

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(viewModel = viewModel,
                changeStack = ::changeStack,
                navController = navController)
        }

        composable("containers") {
            ShitScreen(viewModel = viewModel,
                navController = navController,
                :: RemoveLastContainerFromTheStack,
                :: AddContainerInTheStack,
                getLastContainerOrNullFromStack = :: GetLastContainerOrNullFromStack)
        }

    }
}