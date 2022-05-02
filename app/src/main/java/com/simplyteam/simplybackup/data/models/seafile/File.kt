package com.simplyteam.simplybackup.data.models.seafile

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class File(
    val id: String,
    val modifier_contact_email: String,
    val modifier_email: String,
    val modifier_name: String,
    val mtime: Long,
    val name: String,
    val permission: String,
    val size: Long,
    val starred: Boolean,
    val type: String
)