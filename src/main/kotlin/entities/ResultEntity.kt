package org.example.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "hit_results")
open class ResultEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var r: Double = 0.0,
    var hit: Boolean = false,
    var execMs: Double = 0.0,
    var ts: LocalDateTime = LocalDateTime.now()
) {
    constructor() : this(null, 0.0, 0.0, 0.0, false, 0.0, LocalDateTime.now())
}
