package org.example.models

import jakarta.enterprise.context.SessionScoped
import jakarta.inject.Inject
import jakarta.inject.Named
import org.example.entities.ResultEntity
import org.example.repositories.ResultRepository
import java.io.Serializable
import java.time.LocalDateTime
import java.util.Collections

@Named
@SessionScoped
 class ResultBean : Serializable {

    @Inject
    private lateinit var resultRepository: ResultRepository

    private var cache: MutableList<PointCheck> = mutableListOf()

    @jakarta.annotation.PostConstruct
    fun init(){
        cache = resultRepository.findLast(20)
            .map{it.toPointCheck()}
            .toMutableList()
    }

    fun add(point: PointCheck) {
        cache.add(0, point)
        resultRepository.saveFromPoint(point)
    }

    fun all(): List<PointCheck>  = cache

    fun trimTo(max: Int) {
        if (cache.size > max) {
            cache = cache.take(max).toMutableList()
        }
    }

    private fun ResultEntity.toPointCheck(): PointCheck =
        PointCheck(
            x = this.x,
            y = this.y,
            r = this.r,
            result = this.hit,
            execTimeMs = this.execMs,
            timestamp = this.ts ?: LocalDateTime.now(),
        )
}
