package com.simplyteam.simplybackup.data.models.seafile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Token(
    @Json(name = "token")
    val Token: String
)
