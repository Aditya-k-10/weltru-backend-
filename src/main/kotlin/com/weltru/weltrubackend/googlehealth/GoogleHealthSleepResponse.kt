package com.weltru.weltrubackend.googlehealth

import com.fasterxml.jackson.annotation.JsonProperty
import tools.jackson.databind.JsonNode

data class GoogleHealthSleepResponse(
    @JsonProperty("dataPoints")
    val dataPoints: List<JsonNode> = emptyList(),

    @JsonProperty("nextPageToken")
    val nextPageToken: String? = null
)