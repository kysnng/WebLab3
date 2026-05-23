package org.example.models

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.inject.Named
import org.example.entities.ResultEntity
import org.example.repositories.ResultRepository
import java.io.Serializable
/**
 * Бин для работы с ResultEntity репозиторием этих энтити.
 * Содержит в себе метод для вывода последних 20 точек.
 * Содержит в себе метод для добавления точек в репозиторий точек.
 */
@Named
@ApplicationScoped
class ResultBean : Serializable {

    /**
     * Метод для вывода последних 20 точек.
     * @see ResultRepository
     */
    @Inject
    private lateinit var resultRepository: ResultRepository

    // 20 точек из бд с конца выидраем с корнем
    fun all(limit: Int = 20): List<PointCheck> =
        resultRepository.findLast(limit).map { it.toPointCheck() }


    /**
     * Метод для добавления точки.
     * @see ResultRepository
     */
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
