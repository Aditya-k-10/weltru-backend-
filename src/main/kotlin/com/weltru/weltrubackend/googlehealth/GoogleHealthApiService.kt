package com.weltru.weltrubackend.googlehealth



import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class GoogleHealthApiService {

    private val webClient = WebClient.create()

    fun getIdentity(accessToken: String): GoogleHealthIdentityResponse {
        return try {
            webClient.get()
                .uri("https://health.googleapis.com/v4/users/me/identity")
                .headers { it.setBearerAuth(accessToken) }
                .retrieve()
                .bodyToMono(GoogleHealthIdentityResponse::class.java)
                .block()
                ?: throw IllegalStateException("Google Health identity response is empty")

        } catch (ex: org.springframework.web.reactive.function.client.WebClientResponseException) {
            println("Google Identity Error Status: ${ex.statusCode}")
            println("Google Identity Error Body: ${ex.responseBodyAsString}")
            throw ex
        }
    }
    fun getSleepData(accessToken: String): GoogleHealthSleepResponse {
        return try {
            webClient.get()
                .uri("https://health.googleapis.com/v4/users/me/dataTypes/sleep/dataPoints:reconcile")
                .headers { it.setBearerAuth(accessToken) }
                .retrieve()
                .bodyToMono(GoogleHealthSleepResponse::class.java)
                .block()
                ?: throw IllegalStateException("Google Health sleep response is empty")

        } catch (ex: org.springframework.web.reactive.function.client.WebClientResponseException) {
            println("Google Sleep Error Status: ${ex.statusCode}")
            println("Google Sleep Error Body: ${ex.responseBodyAsString}")
            throw ex
        }
    }
}