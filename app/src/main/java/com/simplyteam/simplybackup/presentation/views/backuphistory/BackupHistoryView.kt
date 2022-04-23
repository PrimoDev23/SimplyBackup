package com.simplyteam.simplybackup.presentation.views.backuphistory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.BackupDetail
import com.simplyteam.simplybackup.data.models.events.backuphistory.BackupHistoryEvent
import com.simplyteam.simplybackup.presentation.viewmodels.backuphistory.BackupHistoryViewModel

@Composable
fun BackupHistoryView(
    paddingValues: PaddingValues,
    viewModel: BackupHistoryViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (viewModel.State.Loading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ProgressIndicator")
            )
        }

        when {
            viewModel.State.LoadingError -> {
                ErrorLabel(R.string.ErrorLoadingFiles)
            }
            !viewModel.State.Loading && viewModel.State.Backups.isEmpty() -> {
                ErrorLabel(R.string.NoFiles)
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .testTag("HistoryList"),
                    state = viewModel.ListState
                ) {
                    items(viewModel.State.Backups) { detail ->
                        FileItem(
                            detail = detail,
                            deleteItem = {
                                viewModel.OnEvent(
                                    BackupHistoryEvent.OnDeleteBackup(
                                        detail
                                    )
                                )
                            },
                            restoreItem = {
                                viewModel.OnEvent(
                                    BackupHistoryEvent.OnRestoreBackup(
                                        detail
                                    )
                                )
                            }
                        )
                    }
                }

                DeleteAlert(
                    isShown = viewModel.State.BackupToDelete != null,
                    dismissDialog = {
                        viewModel.OnEvent(BackupHistoryEvent.OnDeleteDialogDismiss)
                    },
                    confirmDialog = {
                        viewModel.OnEvent(BackupHistoryEvent.OnDeleteConfirmed)
                    }
                )

                RestoreAlert(
                    isShown = viewModel.State.BackupToRestore != null,
                    dismissDialog = {
                        viewModel.OnEvent(BackupHistoryEvent.OnRestoreDialogDismiss)
                    },
                    confirmDialog = {
                        viewModel.OnEvent(BackupHistoryEvent.OnRestoreConfirmed)
                    }
                )

                RestoringDialog(
                    isShown = viewModel.State.CurrentlyRestoring
                )
            }
        }
    }
}

@Composable
private fun FileItem(
    detail: BackupDetail,
    deleteItem: () -> Unit,
    restoreItem: () -> Unit
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(
                    8.dp,
                    8.dp,
                    4.dp,
                    8.dp
                ),
            painter = painterResource(
                id = R.drawable.ic_baseline_folder_zip_24
            ),
            contentDescription = "ZIP"
        )

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(
                    4.dp,
                    8.dp,
                    8.dp,
                    8.dp
                ),
            text = detail.Date,
            style = MaterialTheme.typography.subtitle1
        )

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                modifier = Modifier
                    .padding(
                        4.dp,
                        8.dp,
                        0.dp,
                        8.dp
                    ),
                text = detail.Size,
                style = MaterialTheme.typography.body2
            )
        }

        Column {
            IconButton(
                modifier = Modifier
                    .testTag("More"),
                onClick = {
                    menuExpanded = true
                }) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_baseline_more_vert_24
                    ),
                    contentDescription = ""
                )
            }

            DropdownMenu(
                modifier = Modifier
                    .width(224.dp),
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                },
            ) {
                DropdownMenuItem(
                    modifier = Modifier
                        .testTag("DeleteMenuItem"),
                    onClick = {
                        deleteItem()

                        menuExpanded = false
                    },
                    contentPadding = PaddingValues(
                        24.dp,
                        8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(
                            id = R.string.Delete
                        )
                    )
                    Text(
                        modifier = Modifier
                            .padding(
                                20.dp,
                                0.dp,
                                0.dp,
                                0.dp
                            ),
                        text = stringResource(
                            id = R.string.Delete
                        )
                    )
                }

                DropdownMenuItem(
                    modifier = Modifier
                        .testTag("RestoreMenuItem"),
                    onClick = {
                        restoreItem()

                        menuExpanded = false
                    },
                    contentPadding = PaddingValues(
                        24.dp,
                        8.dp
                    )
                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_baseline_restore_24
                        ),
                        contentDescription = stringResource(
                            id = R.string.Restore
                        )
                    )
                    Text(
                        modifier = Modifier
                            .padding(
                                20.dp,
                                0.dp,
                                0.dp,
                                0.dp
                            ),
                        text = stringResource(
                            id = R.string.Restore
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteAlert(
    isShown: Boolean,
    dismissDialog: () -> Unit,
    confirmDialog: () -> Unit
) {
    if (isShown) {
        AlertDialog(
            modifier = Modifier
                .testTag("DeleteDialog"),
            onDismissRequest = dismissDialog,
            title = {
                Text(
                    text = stringResource(
                        id = R.string.SureQuestion
                    )
                )
            },
            text = {
                Text(
                    text = stringResource(
                        id = R.string.DeleteAlertText
                    )
                )
            },
            dismissButton = {
                TextButton(
                    modifier = Modifier
                        .testTag("DeleteDialogCancel"),
                    onClick = dismissDialog
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.Cancel
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    modifier = Modifier
                        .testTag("DeleteDialogYes"),
                    onClick = confirmDialog
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.Yes
                        )
                    )
                }
            }
        )
    }
}

@Composable
private fun RestoreAlert(
    isShown: Boolean,
    dismissDialog: () -> Unit,
    confirmDialog: () -> Unit
) {
    if (isShown) {
        AlertDialog(
            modifier = Modifier
                .testTag("RestoreDialog"),
            onDismissRequest = dismissDialog,
            title = {
                Text(
                    text = stringResource(
                        id = R.string.SureQuestion
                    )
                )
            },
            text = {
                Text(
                    text = stringResource(
                        id = R.string.RestoreAlertText
                    )
                )
            },
            dismissButton = {
                TextButton(
                    modifier = Modifier
                        .testTag("RestoreDialogCancel"),
                    onClick = dismissDialog
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.Cancel
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    modifier = Modifier
                        .testTag("RestoreDialogYes"),
                    onClick = confirmDialog
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.Yes
                        )
                    )
                }
            }
        )
    }
}

@Composable
private fun RestoringDialog(
    isShown: Boolean
) {
    if (isShown) {
        Dialog(
            onDismissRequest = {}
        ) {
            Card(
                modifier = Modifier
                    .testTag("CurrentlyRestoringDialog"),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(
                                24.dp,
                                24.dp,
                                8.dp,
                                24.dp
                            )
                    )

                    Text(
                        modifier = Modifier
                            .padding(
                                8.dp,
                                24.dp,
                                24.dp,
                                24.dp
                            ),
                        text = stringResource(id = R.string.RestoringBackup)
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorLabel(
    resId: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .testTag("ErrorLabel"),
            text = stringResource(
                id = resId
            ),
            textAlign = TextAlign.Center
        )
    }
}