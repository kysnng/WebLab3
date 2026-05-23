package org.example.beans

import jakarta.enterprise.context.SessionScoped
import jakarta.inject.Inject
import jakarta.inject.Named
import org.example.models.PointCheck
import org.example.models.ResultBean
import java.io.Serializable

/**
 * Бин - основный этап проверки точки. Собирает все данные в строковом представлении и пропускает через все
 * необходимые методы
 */
@Named
@SessionScoped
class PointBean : Serializable {

    var x: String = ""
    var y: String = ""
    var r: String = ""
    var error: String? = null
    var lastPoint: PointCheck? = null

    @field:Inject
    private lateinit var areaCheckBean: AreaCheckBean

    @field:Inject
    private lateinit var resultBean: ResultBean

    fun submit(): String? {
        /**
         * Метод отправки точки. Валидует точку с выводом ошибки при неудаче.
         * Дополнительно проводит чистку от ','.
         * Засекает текущее время, проводит операцию checkPoint и сравнивает текущее время со стартом
         * Записывает разницу в класс для отображения в entity.
         */
        val isValid = areaCheckBean.validateInputStrings(x, y, r)
        if (!isValid) {
            error = "Некорректные значения параметров"
            return null
        }

        val xVal = x.trim().replace(',', '.').toDouble()
        val yVal = y.trim().replace(',', '.').toDouble()
        val rVal = r.trim().replace(',', '.').toDouble()

        val start = System.nanoTime()
        val basePoint = areaCheckBean.checkPoint(xVal, yVal, rVal)
        val execMs = (System.nanoTime() - start) / 1_000_000.0
        val pointCheck = basePoint.copy(execTimeMs = execMs)

        // сохраняем в БД через ResultBean, потому что остальное работает не оч
        resultBean.add(pointCheck)

        lastPoint = pointCheck
        error = null
        return "result?faces-redirect=true"
    }

    fun getResults(): List<PointCheck> = resultBean.all(20)
    /**
     * Метод выводит 20 последних результатов "выстрелов".
     */
}

