package com.weltru.weltrubackend.googlehealth


import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleHealthIdentityResponse(

    @JsonProperty("name")
    val name: String? = null,

    @JsonProperty("legacyUserId")
    val legacyUserId: String? = null,

    @JsonProperty("healthUserId")
    val healthUserId: String? = null
)