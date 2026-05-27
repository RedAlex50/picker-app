package com.picker.app.domain.label

import com.picker.app.domain.model.TempZone
import javax.inject.Inject

/**
 * Ф8. Генерация ZPL II для этикетки 100×150 мм, разрешение 203 dpi (8 точек/мм).
 * QR-код box.id — не менее 15 мм (поле "magnification" 4 даёт ~15 мм при 203 dpi).
 */
class ZplBuilder @Inject constructor() {

    data class Label(
        val boxId: String,
        val orderNo: String,
        val tempZone: TempZone,
        val weightG: Int,
        val slotAt: String,
    )

    fun build(l: Label): String = buildString {
        appendLine("^XA")
        appendLine("^PW800")
        appendLine("^LL1200")
        appendLine("^CI28")
        // Заголовок: номер заказа
        appendLine("^FO40,40^A0N,60,60^FDЗаказ ${l.orderNo}^FS")
        // Температурная зона
        appendLine("^FO40,120^A0N,40,40^FD${zoneLabel(l.tempZone)}^FS")
        // Вес
        appendLine("^FO40,180^A0N,30,30^FDВес: ${"%.2f".format(l.weightG / 1000.0)} кг^FS")
        // Слот
        appendLine("^FO40,230^A0N,30,30^FDСлот: ${l.slotAt}^FS")
        // QR с box.id
        appendLine("^FO40,300^BQN,2,8^FDLA,${l.boxId}^FS")
        // Footer
        appendLine("^FO40,1100^A0N,28,28^FD${l.boxId}^FS")
        appendLine("^XZ")
    }

    private fun zoneLabel(z: TempZone): String = when (z) {
        TempZone.AMBIENT -> "AMBIENT (+15..+25)"
        TempZone.CHILLED -> "CHILLED (+2..+6)"
        TempZone.FROZEN -> "FROZEN (-18)"
    }
}
