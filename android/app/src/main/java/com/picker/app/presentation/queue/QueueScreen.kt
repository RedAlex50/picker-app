package com.picker.app.presentation.queue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.picker.app.domain.model.PickOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(onPick: (PickOrder) -> Unit, vm: QueueViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("Очередь · ${state.orders.size}") }) }) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize(), contentPadding = PaddingValues(12.dp)) {
            items(state.orders, key = { it.id }) { order ->
                OrderCard(order = order, onClick = { onPick(order) })
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun OrderCard(order: PickOrder, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Заказ ${order.orderNo}", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text("Приоритет ${order.priority} · слот ${order.slotAt}")
        }
    }
}
