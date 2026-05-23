package org.example.beans

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Named
import java.io.Serializable
import java.time.LocalTime

/**
 * Бин отвечающий за часы на сайте. Просто собирает время по часам, минутам, секундам.
 */
@Named("clockBean")
@ApplicationScoped
class ClockBean : Serializable {
    val hours: Int
        get() = LocalTime.now().hour

    val minutes: Int
        get() = LocalTime.now().minute

    val seconds: Int
        get() = LocalTime.now().second
}
