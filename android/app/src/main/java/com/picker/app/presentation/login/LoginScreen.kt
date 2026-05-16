package com.picker.app.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(onSuccess: (shiftId: String) -> Unit, vm: LoginViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("PickerApp", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = state.login,
            onValueChange = vm::onLoginChange,
            label = { Text("Логин") },
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.pin,
            onValueChange = vm::onPinChange,
            label = { Text("PIN") },
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = { vm.submit(onSuccess) }, enabled = !state.loading) {
            Text(if (state.loading) "..." else "Войти")
        }
        state.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
