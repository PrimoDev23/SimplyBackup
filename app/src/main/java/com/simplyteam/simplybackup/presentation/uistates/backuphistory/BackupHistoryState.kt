package com.simplyteam.simplybackup.presentation.uistates.backuphistory

import com.simplyteam.simplybackup.data.models.BackupDetail

data class BackupHistoryState(
    val Loading: Boolean = false,
    val LoadingError: Boolean = false,
    val Backups: List<BackupDetail> = listOf(),
    val BackupToDelete: BackupDetail? = null,
    val BackupToRestore: BackupDetail? = null,
    val CurrentlyRestoring: Boolean = false
)
