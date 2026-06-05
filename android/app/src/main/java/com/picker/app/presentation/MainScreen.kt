package com.picker.app.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            PickerNavGraph(navController = navController)
        }
        StatusBar()
    }
}

@Composable
private fun StatusBar() {
    Box(Modifier.fillMaxWidth().padding(8.dp)) {
        Text(
            text = "PickerApp v1.4.0 · Kotlin 2.0 · Compose 1.7 · Spring Boot 3.3 · PostgreSQL 16",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
