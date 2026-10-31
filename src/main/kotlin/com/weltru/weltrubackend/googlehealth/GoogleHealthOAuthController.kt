package com.weltru.weltrubackend.googlehealth

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/google-health/oauth")
class GoogleHealthOAuthController(
    private val oauthService: GoogleHealthOAuthService,
    private val googleHealthApiService: GoogleHealthApiService
){

    @GetMapping("/authorize")
    fun authorize(
        @RequestParam state: String
    ): Map<String, String> {
        return mapOf(
            "authorizationUrl" to oauthService.buildAuthorizationUrl(state)
        )
    }

    @PostMapping("/exchange")
    fun exchangeToken(
        @RequestParam code: String
    ): GoogleHealthTokenResponse {
        return oauthService.exchangeAuthorizationCode(code)
    }

    @PostMapping("/refresh")
    fun refreshToken(
        @RequestParam refreshToken: String
    ): GoogleHealthTokenResponse {
        return oauthService.refreshAccessToken(refreshToken)
    }
    @PostMapping("/identity")
    fun getIdentity(
        @RequestBody request: Map<String, String>
    ): GoogleHealthIdentityResponse {
        val accessToken = request["accessToken"]
            ?: throw IllegalArgumentException("accessToken is required")

        return googleHealthApiService.getIdentity(accessToken)
    }
    @PostMapping("/sleep")
    fun getSleepData(
        @RequestBody request: Map<String, String>
    ): GoogleHealthSleepResponse {
        val accessToken = request["accessToken"]
            ?: throw IllegalArgumentException("accessToken is required")

        return googleHealthApiService.getSleepData(accessToken)
    }
}