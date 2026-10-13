package com.weltru.weltrubackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.sql.DataSource

@SpringBootApplication
class WeltruBackendApplication

fun main(args: Array<String>) {
    runApplication<WeltruBackendApplication>(*args)
}

@RestController
class TestController(
    private val dataSource: DataSource
) {
    @GetMapping("/api/test")
    fun testConnection(): Map<String, String> {
        dataSource.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT 1").use { result ->
                    result.next()
                    return mapOf(
                        "status" to "success",
                        "backend" to "running",
                        "database" to "connected"
                    )
                }
            }
        }
    }
}
