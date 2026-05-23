package org.example.beans

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Named
import java.io.Serializable
import org.example.models.PointCheck

@Named
@ApplicationScoped
/**
 * Это бобик служит верой и правдой проверяя и валидуя попадание точки.
 */
class AreaCheckBean : Serializable {
    /**
     * Служит регулировщиком в данном бине. Перенаправляет на соответствующие методы (фигуры)
     * при попадании точки в одну из четвертей графика.
     * При попадании в пустую четверть графика сразу возвращает промах.
     * @param x координата по оси x (да ну насмерть)
     * @param y координата по y
     * @param r масштаб координатной плоскости
     * @return PointCheck(x, y, r, result) возвращает результат "выстрела" с параметрами.
     * @see PointCheck
     */
    fun checkPoint(x: Double, y: Double, r: Double): PointCheck {
        val result = when {
            x >= 0 && y >= 0 -> checkCircle(x, y, r)
            x <= 0 && y <= 0 -> checkRectangle(x, y, r)
            x <= 0 && y >= 0 -> checkTriangle(x, y, r)
            else -> false
        }
        return PointCheck(x, y, r, result)
    }
    /**
     * Проверяет попала ли точка в треугольник из ТЗ.
     * @param x координата по оси x (да ну насмерть)
     * @param y координата по y
     * @param r масштаб координатной плоскости
     * @return true если попала
     * @return false если мимо
     */
    private fun checkTriangle(x: Double, y: Double, r: Double): Boolean {
        return y <= (x + 0.5*r)
    }
    /**
     * Проверяет попала ли точка в круг из ТЗ.
     * @param x координата по оси x (да ну насмерть)
     * @param y координата по y
     * @param r масштаб координатной плоскости
     * @return true если попала
     * @return false если мимо
     */
    private fun checkCircle(x: Double, y: Double, r: Double): Boolean {
        return (x * x + y * y) <= (r * r)
    }
    /**
     * Проверяет попала ли точка в прямоугольник из ТЗ.
     * @param x координата по оси x (да ну насмерть)
     * @param y координата по y
     * @param r масштаб координатной плоскости
     * @return true если попала
     * @return false если мимо
     */
    private fun checkRectangle(x: Double, y: Double, r: Double): Boolean {
        return (x >= -r) && (y >= -0.5*r)
    }
    /**
     * Валидатор строк, который проверяет не вылетела ли точка за границы отрисованного графика.
     * @param xStr строчное представление x
     * @param yStr строчное представление y
     * @param rStr строчное представление r
     * @return true если вся валидация пройдена
     * @return false если хоть какой-то параметр Out Of Bounce
     */
    fun validateInputStrings(xStr: String, yStr: String, rStr: String): Boolean {
        val x = parseWithScaleLimit(xStr, min = -5.0, max = 5.0, maxScale = 3) ?: return false
        val y = parseWithScaleLimit(yStr, min = -5.0, max = 5.0, maxScale = 3) ?: return false
        val r = parseWithScaleLimit(rStr, min = 1.0, max = 3.0, maxScale = 3) ?: return false
        return true
    }
    /**
     * Форматирует и дополнительно проверяет входящие данные.
     * 1. Форматирует входящую строку меняя ',' на '.', чтобы пользователь мог вводить число как ему удобно.
     * 2. Проверяет число через regex на то что это реально число, а не текст либо примесь текста к числу. (Защита от дурака).
     * 3. Переводит строчное число в реальное число
     * 4. Проверяет максимальный размер числа по символам, чтобы не было всяких 1.9999999999999.
     * 5. Проверяет еще раз (на всякий) границы графика через min и max
     * @param raw Входящая строка, которая подлежит форматированию и обработке.
     * @param min минимально возможное число
     * @param max максимально возможное число
     * @param maxScale максимум по символам в числе
     */
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
