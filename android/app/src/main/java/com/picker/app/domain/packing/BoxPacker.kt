package com.picker.app.domain.packing

import com.picker.app.domain.model.PickItem
import com.picker.app.domain.model.Sku
import com.picker.app.domain.model.TempZone
import java.util.UUID
import javax.inject.Inject

/**
 * Ф7. Формирование боксов: один бокс — одна temp_zone, вес <= 15 кг.
 * Алгоритм — First-Fit Decreasing: позиции отсортированы по убыванию веса,
 * каждая укладывается в первый бокс зоны, куда помещается, иначе создаётся
 * новый бокс.
 */
class BoxPacker @Inject constructor() {

    data class Box(val id: String, val tempZone: TempZone, val items: List<PickedLine>, val weightG: Int)
    data class PickedLine(val itemId: String, val skuId: String, val qty: Double, val weightG: Int)

    fun pack(items: List<PickItem>, skus: Map<String, Sku>): List<Box> {
        val lines = items
            .mapNotNull { item ->
                val sku = skus[item.skuId] ?: return@mapNotNull null
                val weight = (sku.grossWeightG * item.qtyPicked).toInt()
                Triple(sku.tempZone, weight, PickedLine(item.id, sku.id, item.qtyPicked, weight))
            }
            .sortedByDescending { it.second }

        val openBoxes = mutableMapOf<TempZone, MutableList<MutableList<PickedLine>>>()

        for ((zone, weight, line) in lines) {
            val zoneBoxes = openBoxes.getOrPut(zone) { mutableListOf() }
            val target = zoneBoxes.firstOrNull { it.sumOf { l -> l.weightG } + weight <= MAX_WEIGHT_G }
            if (target != null) target.add(line)
            else zoneBoxes.add(mutableListOf(line))
        }

        return openBoxes.flatMap { (zone, boxes) ->
            boxes.map { contents ->
                Box(
                    id = UUID.randomUUID().toString(),
                    tempZone = zone,
                    items = contents.toList(),
                    weightG = contents.sumOf { it.weightG },
                )
            }
        }
    }

    private companion object { const val MAX_WEIGHT_G = 15_000 }
}
