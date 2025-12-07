package org.example.servlets

import jakarta.inject.Inject
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.beans.AreaCheckBean
import org.example.models.PointCheck
import org.example.repositories.ResultRepository
import java.util.Locale

@WebServlet(name = "PointApiServlet", urlPatterns = ["/api/point"])
class PointApiServlet : HttpServlet() {

    @Inject
    private lateinit var areaCheckBean: AreaCheckBean

    @Inject
    private lateinit var resultRepository: ResultRepository

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.contentType = "application/json;charset=UTF-8"
        resp.characterEncoding = "UTF-8"

        val xStr = req.getParameter("x") ?: ""
        val yStr = req.getParameter("y") ?: ""
        val rStr = req.getParameter("r") ?: ""

        if (!areaCheckBean.validateInputStrings(xStr, yStr, rStr)) {
            resp.status = 400
            resp.writer.write("""{"error":"Некорректные параметры"}""")
            return
        }

        val x = xStr.trim().replace(',', '.').toDouble()
        val y = yStr.trim().replace(',', '.').toDouble()
        val r = rStr.trim().replace(',', '.').toDouble()

        val start = System.nanoTime()
        val basePoint = areaCheckBean.checkPoint(x, y, r)
        val execMs = (System.nanoTime() - start) / 1_000_000.0
        val point: PointCheck = basePoint.copy(execTimeMs = execMs)

        resultRepository.saveFromPoint(point)

        // ВАЖНО: использовать точку как разделитель
        val json = """
            {
              "hit": ${point.result},
              "x": ${"%.3f".format(Locale.US, point.x)},
              "y": ${"%.3f".format(Locale.US, point.y)},
              "r": ${"%.1f".format(Locale.US, point.r)},
              "execMs": ${"%.3f".format(Locale.US, point.execTimeMs)},
              "timestamp": "${point.getFormattedTimestamp()}"
            }
        """.trimIndent()

        resp.writer.write(json)
    }
}
