package com.simplyteam.simplybackup.data.models.events.backuphistory

import com.simplyteam.simplybackup.data.models.BackupDetail

sealed class BackupHistoryEvent {
    data class OnDeleteBackup(val Backup: BackupDetail): BackupHistoryEvent()
    object OnDeleteDialogDismiss: BackupHistoryEvent()
    object OnDeleteConfirmed: BackupHistoryEvent()
    data class OnRestoreBackup(val Backup: BackupDetail): BackupHistoryEvent()
    object OnRestoreDialogDismiss: BackupHistoryEvent()
    object OnRestoreConfirmed: BackupHistoryEvent()
}
