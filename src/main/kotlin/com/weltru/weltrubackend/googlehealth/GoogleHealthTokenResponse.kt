package com.weltru.weltrubackend.googlehealth


import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleHealthTokenResponse(

    @JsonProperty("access_token")
    val accessToken: String? = null,

    @JsonProperty("refresh_token")
    val refreshToken: String? = null,

    @JsonProperty("expires_in")
    val expiresIn: Long? = null,

    @JsonProperty("refresh_token_expires_in")
    val refreshTokenExpiresIn: Long? = null,

    @JsonProperty("token_type")
    val tokenType: String? = null,

    @JsonProperty("scope")
    val scope: String? = null,

    @JsonProperty("error")
    val error: String? = null,

    @JsonProperty("error_description")
    val errorDescription: String? = null
)