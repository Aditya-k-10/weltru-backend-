package com.weltru.weltrubackend.googlehealth

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class GoogleHealthConfig(

    @Value("\${google.health.client-id}")
    val clientId: String,

    @Value("\${google.health.client-secret}")
    val clientSecret: String,

    @Value("\${google.health.redirect-uri}")
    val redirectUri: String
)