package org.example.models

import jakarta.enterprise.context.SessionScoped
import jakarta.inject.Named
import java.io.Serializable
import java.util.Collections

@Named
@SessionScoped
open class ResultBean : Serializable {
    private val results = mutableListOf<PointCheck>()

    fun add(item: PointCheck) {
        results.add(item)
    }

    fun all(): List<PointCheck> = Collections.unmodifiableList(results)

    fun trimTo(max: Int) {
        if (results.size > max) repeat(results.size - max) { results.removeAt(0) }
    }

    fun clear() {
        results.clear()
    }
}
