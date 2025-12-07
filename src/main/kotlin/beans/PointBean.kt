package org.example.beans

import jakarta.enterprise.context.SessionScoped
import jakarta.inject.Inject
import jakarta.inject.Named
import org.example.models.PointCheck
import org.example.models.ResultBean
import org.example.beans.AreaCheckBean
import java.io.Serializable

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

        resultBean.add(pointCheck)
        resultBean.trimTo(20)
        lastPoint = pointCheck
        error = null

        return "result?faces-redirect=true"
    }

    fun getResults(): List<PointCheck> = resultBean.all()
}
