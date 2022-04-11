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
import com.simplyteam.simplybackup.data.models.RestoreStatus
import com.simplyteam.simplybackup.presentation.viewmodels.backuphistory.BackupHistoryViewModel
import kotlinx.coroutines.launch

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
        if (viewModel.Loading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ProgressIndicator")
            )
        }

        when {
            viewModel.ShowErrorLoading -> {
                ErrorLabel(R.string.ErrorLoadingFiles)
            }
            !viewModel.Loading && viewModel.BackupDetails.isEmpty() -> {
                ErrorLabel(R.string.NoFiles)
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .testTag("HistoryList")
                ) {
                    items(viewModel.BackupDetails) { detail ->
                        FileCard(
                            detail = detail,
                            viewModel = viewModel
                        )
                    }
                }

                DeleteAlert(
                    viewModel = viewModel
                )

                RestoreControls(
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun FileCard(
    detail: BackupDetail,
    viewModel: BackupHistoryViewModel
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
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
                        .width(240.dp),
                    expanded = menuExpanded,
                    onDismissRequest = {
                        menuExpanded = false
                    },
                ) {
                    DropdownMenuItem(
                        modifier = Modifier
                            .testTag("DeleteMenuItem"),
                        onClick = {
                            viewModel.ShowDeleteAlert(detail)
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
                            viewModel.ShowRestoreAlert(detail)
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
}

@Composable
private fun DeleteAlert(viewModel: BackupHistoryViewModel) {
    val scope = rememberCoroutineScope()

    if (viewModel.BackupToDelete != null) {
        AlertDialog(
            modifier = Modifier
                .testTag("DeleteDialog"),
            onDismissRequest = {
                viewModel.HideDeleteAlert()
            },
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
                    onClick = {
                        viewModel.HideDeleteAlert()
                    }
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
                    onClick = {
                        scope.launch {
                            viewModel.DeleteBackup()
                        }
                    }
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
private fun RestoreControls(viewModel: BackupHistoryViewModel) {
    RestoreAlert(
        viewModel = viewModel
    )

    RestoringDialog(
        viewModel = viewModel
    )

    RestoreFinishedAlert(
        viewModel
    )
}

@Composable
private fun RestoreAlert(viewModel: BackupHistoryViewModel) {
    val scope = rememberCoroutineScope()

    if (viewModel.BackupToRestore != null) {
        AlertDialog(
            modifier = Modifier
                .testTag("RestoreDialog"),
            onDismissRequest = {
                viewModel.HideRestoreAlert()
            },
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
                    onClick = {
                        viewModel.HideRestoreAlert()
                    }
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
                    onClick = {
                        scope.launch {
                            viewModel.RestoreBackup()
                        }
                    }
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
private fun RestoringDialog(viewModel: BackupHistoryViewModel) {
    if (viewModel.CurrentRestoreStatus == RestoreStatus.RESTORING) {
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
private fun RestoreFinishedAlert(viewModel: BackupHistoryViewModel) {
    if (viewModel.CurrentRestoreStatus == RestoreStatus.SUCCESS
        || viewModel.CurrentRestoreStatus == RestoreStatus.ERROR
    ) {
        AlertDialog(
            modifier = Modifier
                .testTag("RestoreFinishedDialog"),
            onDismissRequest = {
                viewModel.HideRestoreFinishedAlert()
            },
            title = {
                Text(
                    text = stringResource(
                        id = if (viewModel.CurrentRestoreStatus == RestoreStatus.SUCCESS) R.string.SuccessNotificationTitle else R.string.ErrorNotificationTitle
                    )
                )
            },
            text = {
                Text(
                    text = stringResource(
                        id = if (viewModel.CurrentRestoreStatus == RestoreStatus.SUCCESS) R.string.RestoringBackupSucceed else R.string.RestoringBackupError
                    )
                )
            },
            dismissButton = {
                TextButton(
                    modifier = Modifier
                        .testTag("OkRestoreFinishedDialog"),
                    onClick = {
                        viewModel.HideRestoreFinishedAlert()
                    }
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.OK
                        )
                    )
                }
            },
            confirmButton = {

            }
        )
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