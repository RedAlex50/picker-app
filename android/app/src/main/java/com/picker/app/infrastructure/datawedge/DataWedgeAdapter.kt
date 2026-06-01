package com.picker.app.infrastructure.datawedge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

data class ScanEvent(val data: String, val symbology: String, val timestampMs: Long)

/**
 * Адаптер событий сканера Zebra DataWedge через intent
 * com.symbol.datawedge.api.RESULT_ACTION. На устройствах Honeywell/Urovo
 * используется тот же контракт, имя intent перенастраивается профилем.
 *
 * Двойные срабатывания триггера на Zebra TC22 в пределах окна 300мс
 * (одинаковые data+symbology) фильтруются — иначе пикер получает два
 * SCAN_SKU подряд и количество улетает за qty_required.
 */
@Singleton
class DataWedgeAdapter @Inject constructor(@ApplicationContext private val ctx: Context) {

    fun scans(): Flow<ScanEvent> = callbackFlow {
        var last: ScanEvent? = null

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                val data = intent?.getStringExtra(EXTRA_DATA) ?: return
                val symbology = intent.getStringExtra(EXTRA_LABEL_TYPE) ?: "UNKNOWN"
                val now = System.currentTimeMillis()
                val prev = last
                if (prev != null && prev.data == data && prev.symbology == symbology &&
                    now - prev.timestampMs < DEDUP_WINDOW_MS) {
                    return
                }
                val event = ScanEvent(data, symbology, now)
                last = event
                trySend(event)
            }
        }
        ctx.registerReceiver(receiver, IntentFilter(ACTION_RESULT), Context.RECEIVER_EXPORTED)
        awaitClose { ctx.unregisterReceiver(receiver) }
    }

    private companion object {
        const val ACTION_RESULT = "com.symbol.datawedge.api.RESULT_ACTION"
        const val EXTRA_DATA = "com.symbol.datawedge.data_string"
        const val EXTRA_LABEL_TYPE = "com.symbol.datawedge.label_type"
        const val DEDUP_WINDOW_MS = 300L
    }
}
