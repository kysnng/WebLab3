package org.example.repositories

import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.PersistenceUnit
import org.example.models.PointCheck
import org.example.entities.ResultEntity
/**
 * Сборник точек с которым работает emf.
 * Содержит в себе метод сохранения точки в списке.
 * Содержит в себе метод вывода последних точек.
 */
@ApplicationScoped
 class ResultRepository {


    @PersistenceUnit(unitName = "labPU")
    private lateinit var emf: EntityManagerFactory
    /**
     * Метод сохранения точки в список
     * @param point
     * @see PointCheck
     */
    fun saveFromPoint(point: PointCheck) {
        val em = emf.createEntityManager()
        try {
            val tx = em.transaction
            tx.begin()

            val entity = ResultEntity(
                x = point.x,
                y = point.y,
                r = point.r,
                hit = point.result,
                execMs = point.execTimeMs,
                ts = point.timestamp
            )

            em.persist(entity)
            tx.commit()
        } finally {
            if (em.isOpen) em.close()
        }
    }
    /**
     * Метод поиска последних добавленных точек.
     * @param limit
     */
    fun findLast(limit: Int): List<ResultEntity> {

        val em = emf.createEntityManager()
        return try {
            em.createQuery(
                "select r from ResultEntity r order by r.ts desc",
                ResultEntity::class.java
            )
                .setMaxResults(limit)
                .resultList
        } finally {
            if (em.isOpen) em.close()
        }
    }
}
