package com.picker.app.presentation.queue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.picker.app.domain.model.PickOrder
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(onPick: (PickOrder) -> Unit, vm: QueueViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("Очередь нарядов · ${state.orders.size}") }) }) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize(), contentPadding = PaddingValues(12.dp)) {
            items(state.orders, key = { it.id }) { order ->
                OrderCard(order = order, onClick = { onPick(order) })
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

private val LOCAL_SLOT_FORMAT: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd.MM HH:mm", Locale("ru"))

private data class PriorityStyle(val label: String, val container: Color, val content: Color)

private fun styleFor(priority: Int): PriorityStyle = when {
    priority >= 8 -> PriorityStyle("Срочно", Color(0xFFD32F2F), Color.White)
    priority >= 4 -> PriorityStyle("Средний", Color(0xFFF9A825), Color.Black)
    else -> PriorityStyle("Обычный", Color(0xFF388E3C), Color.White)
}

@Composable
private fun OrderCard(order: PickOrder, onClick: () -> Unit) {
    val slotLocal = order.slotAt
        .atZone(ZoneId.systemDefault())
        .format(LOCAL_SLOT_FORMAT)
    val style = styleFor(order.priority)

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(
                    "Наряд № ${order.orderNo}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = { Text("${style.label} · P${order.priority}") },
                    colors = AssistChipDefaults.assistChipColors(
                        disabledContainerColor = style.container,
                        disabledLabelColor = style.content,
                    ),
                )
            }
            Spacer(Modifier.height(6.dp))
            Text("Слот выдачи: $slotLocal")
        }
    }
}
