package org.example.services

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Named
import java.io.Serializable
import org.example.models.PointCheck

@Named
@ApplicationScoped
open class AreaCheckBean : Serializable {

    fun checkPoint(x: Double, y: Double, r: Double): PointCheck {
        val result = when {
            x <= 0 && y >= 0 -> checkCircle(x, y, r)
            x >= 0 && y <= 0 -> checkRectangle(x, y, r)
            x >= 0 && y >= 0 -> checkTriangle(x, y, r)
            else -> false
        }
        return PointCheck(x, y, r, result)
    }

    private fun checkTriangle(x: Double, y: Double, r: Double): Boolean {
        return x <= (-2 * y + r)
    }

    private fun checkCircle(x: Double, y: Double, r: Double): Boolean {
        return (x * x + y * y) <= (r / 2 * r / 2)
    }

    private fun checkRectangle(x: Double, y: Double, r: Double): Boolean {
        return (x <= r) && (y >= -r)
    }

    fun validateInputStrings(xStr: String, yStr: String, rStr: String): Boolean {
        val x = parseWithScaleLimit(xStr, min = -4.5, max = 4.5, maxScale = 3) ?: return false
        val y = parseWithScaleLimit(yStr, min = -5.0, max = 5.0, maxScale = 3) ?: return false
        val r = parseWithScaleLimit(rStr, min = 1.0, max = 3.0, maxScale = 3) ?: return false
        return true
    }

    private fun parseWithScaleLimit(raw: String, min: Double, max: Double, maxScale: Int): Double? {
        val norm = raw.trim().replace(',', '.')
        if (!Regex("^[-+]?\\d+(?:[.]\\d+)?$").matches(norm)) return null
        val bd = norm.toBigDecimalOrNull() ?: return null
        if (bd.scale() > maxScale) return null
        val d = bd.toDouble()
        if (d < min || d > max) return null
        return d
    }
}
