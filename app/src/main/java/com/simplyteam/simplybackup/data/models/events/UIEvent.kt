package com.simplyteam.simplybackup.data.models.events

import com.simplyteam.simplybackup.data.models.UIText

sealed class UIEvent {
    data class ShowSnackbar(
        val Message: UIText,
        val Action: UIText? = null,
        val ActionClicked: (() -> Unit)? = null,
        val Dismissed: (() -> Unit)? = null
    )
}
