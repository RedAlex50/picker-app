package com.picker.app.domain.route

import com.picker.app.domain.model.Cell
import com.picker.app.domain.model.PickItem
import com.picker.app.domain.model.Sku
import com.picker.app.domain.model.TempZone
import javax.inject.Inject

/**
 * Ф3. Построение маршрута сборки s-shape (змейка) внутри склада,
 * с группировкой по температурным зонам (сначала AMBIENT, затем CHILLED,
 * затем FROZEN — чтобы холод не успел нагреться в корзине). Длина — до 100
 * ячеек на наряд.
 *
 * Алгоритм: ячейки группируются по rowNo; чётные ряды обходятся по
 * возрастанию level, нечётные — по убыванию.
 */
class SShapeRouter @Inject constructor() {

    data class RouteStep(val item: PickItem, val cell: Cell, val order: Int)

    fun build(items: List<PickItem>, cells: Map<String, Cell>, skus: Map<String, Sku>): List<RouteStep> {
        val byZone = items
            .filter { it.cellId != null }
            .groupBy { skus[it.skuId]?.tempZone ?: TempZone.AMBIENT }

        val ordered = listOf(TempZone.AMBIENT, TempZone.CHILLED, TempZone.FROZEN)
            .flatMap { zone ->
                val inZone = byZone[zone].orEmpty()
                serpentine(inZone, cells)
            }

        return ordered.mapIndexed { idx, (item, cell) -> RouteStep(item, cell, idx + 1) }
    }

    private fun serpentine(items: List<PickItem>, cells: Map<String, Cell>): List<Pair<PickItem, Cell>> {
        return items
            .mapNotNull { item -> cells[item.cellId]?.let { item to it } }
            .groupBy { (_, c) -> c.rowNo }
            .toSortedMap()
            .flatMap { (rowNo, pairs) ->
                if (rowNo % 2 == 0) pairs.sortedBy { it.second.level }
                else pairs.sortedByDescending { it.second.level }
            }
    }
}
