package com.simplyteam.simplybackup.data.models.seafile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "username")
    val Username: String,
    @Json(name = "password")
    val Password: String
)
