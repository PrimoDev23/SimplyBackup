package com.simplyteam.simplybackup.data.models

data class SnackbarInformation(val text: UIText, val actionText: UIText? = null, val dismissed: (() -> Unit)? = null, val action: (() -> Unit)? = null)
