package com.simplyteam.simplybackup.data.models

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Path(
    val Path: String,
    val Type: PathType
    ) : Serializable
