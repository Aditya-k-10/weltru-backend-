package com.weltru.weltrubackend.googlehealth

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Service
class GoogleHealthOAuthService(
    private val config: GoogleHealthConfig
) {

    private val webClient = WebClient.create()

    fun buildAuthorizationUrl(state: String): String {
        val scopes = listOf(
            "https://www.googleapis.com/auth/googlehealth.sleep.readonly",
            "https://www.googleapis.com/auth/googlehealth.health_metrics_and_measurements.readonly",
            "https://www.googleapis.com/auth/googlehealth.activity_and_fitness.readonly"
        ).joinToString(" ")

        return org.springframework.web.util.UriComponentsBuilder
            .fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
            .queryParam("client_id", config.clientId)
            .queryParam("redirect_uri", config.redirectUri)
            .queryParam("response_type", "code")
            .queryParam("scope", scopes)
            .queryParam("access_type", "offline")
            .queryParam("prompt", "consent")
            .queryParam("state", state)
            .build()
            .encode()
            .toUriString()
    }

    fun exchangeAuthorizationCode(code: String): GoogleHealthTokenResponse {
        return webClient.post()
            .uri("https://oauth2.googleapis.com/token")
            .body(
                BodyInserters.fromFormData("code", code)
                    .with("client_id", config.clientId)
                    .with("client_secret", config.clientSecret)
                    .with("redirect_uri", config.redirectUri)
                    .with("grant_type", "authorization_code")
            )
            .retrieve()
            .bodyToMono(GoogleHealthTokenResponse::class.java)
            .block()
            ?: throw IllegalStateException("Google token exchange returned an empty response")
    }

    fun refreshAccessToken(refreshToken: String): GoogleHealthTokenResponse {
        return try {
            webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .body(
                    BodyInserters.fromFormData("refresh_token", refreshToken)
                        .with("client_id", config.clientId)
                        .with("client_secret", config.clientSecret)
                        .with("grant_type", "refresh_token")
                )
                .retrieve()
                .bodyToMono(GoogleHealthTokenResponse::class.java)
                .block()
                ?: throw IllegalStateException("Google token refresh returned an empty response")

        } catch (ex: org.springframework.web.reactive.function.client.WebClientResponseException) {
            println("Google refresh error status: ${ex.statusCode}")
            println("Google refresh error body: ${ex.responseBodyAsString}")
            throw ex
        }
    }
}