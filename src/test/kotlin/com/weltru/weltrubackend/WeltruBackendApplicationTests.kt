package com.weltru.weltrubackend

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest
class WeltruBackendApplicationTests {

    @Autowired
    private lateinit var testController: TestController

    @Test
    fun contextLoads() {
    }

    @Test
    fun testApiConnection() {
        val response = testController.testConnection()
        println("API Test Result: $response")
        assertEquals("success", response["status"])
        assertEquals("running", response["backend"])
        assertEquals("connected", response["database"])
    }
}
