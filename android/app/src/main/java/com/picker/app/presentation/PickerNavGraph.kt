package com.picker.app.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

object Routes {
    const val LOGIN = "login"
    const val QUEUE = "queue"
    const val PICKING = "picking/{orderId}"
    const val PACKING = "packing/{orderId}"
    const val HANDOVER = "handover/{orderId}"
}

@Composable
fun PickerNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) { /* LoginScreen */ }
        composable(Routes.QUEUE) { /* QueueScreen */ }
        composable(Routes.PICKING) { /* PickingScreen */ }
        composable(Routes.PACKING) { /* PackingScreen */ }
        composable(Routes.HANDOVER) { /* HandoverScreen */ }
    }
}
