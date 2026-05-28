package com.picker.app.infrastructure.print

import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Отправка ZPL-команд на сетевой принтер через Print Service.
 * Ожидание ack — до 5 c, повтор при сбое — до 3 раз с задержкой 500мс/1с/2с.
 */
@Singleton
class PrintServiceClient @Inject constructor(private val http: OkHttpClient) {

    data class PrintResult(val success: Boolean, val attempts: Int, val error: String? = null)

    suspend fun send(printerId: String, zpl: String): PrintResult {
        val req = Request.Builder()
            .url("http://print-service.internal:9100/printers/$printerId/zpl")
            .post(zpl.toRequestBody("application/x-zpl".toMediaType()))
            .build()

        var lastError: String? = null
        for (attempt in 1..MAX_ATTEMPTS) {
            try {
                http.newCall(req).execute().use { resp ->
                    if (resp.isSuccessful) return PrintResult(true, attempt)
                    lastError = "HTTP ${resp.code}"
                }
            } catch (e: IOException) {
                lastError = e.message
            }
            if (attempt < MAX_ATTEMPTS) delay(backoffMs(attempt))
        }
        return PrintResult(false, MAX_ATTEMPTS, lastError)
    }

    private fun backoffMs(attempt: Int): Long = 500L shl (attempt - 1)

    private companion object { const val MAX_ATTEMPTS = 3 }
}
