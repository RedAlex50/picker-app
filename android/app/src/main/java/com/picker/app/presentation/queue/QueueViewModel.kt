package com.picker.app.presentation.queue

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picker.app.domain.model.PickOrder
import com.picker.app.domain.usecase.OrderQueueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QueueState(val orders: List<PickOrder> = emptyList(), val loading: Boolean = false)

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val queue: OrderQueueUseCase,
    saved: SavedStateHandle,
) : ViewModel() {
    private val shiftId: String = checkNotNull(saved["shiftId"])
    private val _state = MutableStateFlow(QueueState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            queue.refresh(shiftId)
            queue.observe(shiftId).collect { _state.value = _state.value.copy(orders = it) }
        }
    }
}
