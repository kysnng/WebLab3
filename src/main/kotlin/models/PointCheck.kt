package org.example.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class PointCheck(
    val x: Double,
    val y: Double,
    val r: Double,
    val result: Boolean,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val execTimeMs: Double = 0.0
) {
    fun getFormattedTimestamp(): String {
        return timestamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
    }

    fun getResultText(): String {
        return if (result) "Попадание" else "Промах"
    }
}
