package org.example.models

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.inject.Named
import org.example.entities.ResultEntity
import org.example.repositories.ResultRepository
import java.io.Serializable

@Named
@ApplicationScoped
class ResultBean : Serializable {

    @Inject
    private lateinit var resultRepository: ResultRepository

    // Берём последние N точек прямо из БД
    fun all(limit: Int = 20): List<PointCheck> =
        resultRepository.findLast(limit).map { it.toPointCheck() }

    // Для обычной формы (submit из JSF)
    fun add(point: PointCheck) {
        resultRepository.saveFromPoint(point)
    }

    // На будущее, если где-то вызывается
    fun trimTo(limit: Int) {
        // можно ничего не делать, т.к. all(limit) и так режет
    }
}

// маппер сущности в PointCheck
fun ResultEntity.toPointCheck(): PointCheck =
    PointCheck(
        x = this.x,
        y = this.y,
        r = this.r,
        result = this.hit,
        execTimeMs = this.execMs,
        timestamp = this.ts
    )
