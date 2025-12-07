package org.example.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "hit_results")
open class ResultEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "result_seq")
    @SequenceGenerator(
        name = "result_seq",
        sequenceName = "result_seq",
        allocationSize = 1
    )
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "x", nullable = false)
    var x: Double = 0.0,

    @Column(name = "y", nullable = false)
    var y: Double = 0.0,

    @Column(name = "r", nullable = false)
    var r: Double = 0.0,

    @Column(name = "hit", nullable = false)
    var hit: Boolean = false,

    @Column(name = "execMs", nullable = false)
    var execMs: Double = 0.0,

    @Column(name = "ts", nullable = false)
    var ts: LocalDateTime = LocalDateTime.now()
)
