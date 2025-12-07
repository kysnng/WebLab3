package org.example.servlets

import jakarta.inject.Inject
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.beans.AreaCheckBean
import org.example.models.PointCheck
import org.example.repositories.ResultRepository

@WebServlet(name = "ControllerServlet", urlPatterns = ["/controller"])
class ControllerServlet : HttpServlet() {

    @Inject
    lateinit var areaCheckBean: AreaCheckBean

    @Inject
    lateinit var resultRepository: ResultRepository

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.characterEncoding = "UTF-8"
        resp.contentType = "application/json;charset=UTF-8"

        val xStr = req.getParameter("x") ?: ""
        val yStr = req.getParameter("y") ?: ""
        val rStr = req.getParameter("r") ?: ""

        if (!areaCheckBean.validateInputStrings(xStr, yStr, rStr)) {
            resp.status = 400
            resp.writer.write("""{"error":"Некорректные значения параметров"}""")
            return
        }

        val x = xStr.replace(',', '.').trim().toDouble()
        val y = yStr.replace(',', '.').trim().toDouble()
        val r = rStr.replace(',', '.').trim().toDouble()

        val start = System.nanoTime()
        val basePoint: PointCheck = areaCheckBean.checkPoint(x, y, r)
        val execMs = (System.nanoTime() - start) / 1_000_000.0
        val point = basePoint.copy(execTimeMs = execMs)

        resultRepository.saveFromPoint(point)

        val tsStr = point.timestamp.toString()
        val json = """
            {
              "hit": ${point.result},
              "x": ${point.x},
              "y": ${point.y},
              "r": ${point.r},
              "execMs": ${"%.3f".format(execMs)},
              "timestamp": "$tsStr"
            }
        """.trimIndent()

        resp.writer.write(json)
    }
}
