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

    // 20 точек из бд с конца выидраем с корнем
    fun all(limit: Int = 20): List<PointCheck> =
        resultRepository.findLast(limit).map { it.toPointCheck() }

    // Для глупи submit
    fun add(point: PointCheck) {
        resultRepository.saveFromPoint(point)
    }
}

// Сущность делает гадости))))
fun ResultEntity.toPointCheck(): PointCheck =
    PointCheck(
        x = this.x,
        y = this.y,
        r = this.r,
        result = this.hit,
        execTimeMs = this.execMs,
        timestamp = this.ts
    )
