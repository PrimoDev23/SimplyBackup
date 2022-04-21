package com.simplyteam.simplybackup.presentation.views.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.receiver.BackupReceiver
import com.simplyteam.simplybackup.data.utils.ActivityUtil
import com.simplyteam.simplybackup.presentation.viewmodels.main.ConnectionOverviewViewModel
import com.simplyteam.simplybackup.presentation.views.ConnectionIcon
import com.simplyteam.simplybackup.presentation.views.SearchBox
import kotlinx.coroutines.launch
import timber.log.Timber


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ConnectionOverviewView(
    paddingValues: PaddingValues,
    viewModel: ConnectionOverviewViewModel
) {
    val activity = LocalContext.current as ComponentActivity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("ConnectionList"),
            state = viewModel.ListState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SearchBox(
                    searchText = viewModel.GetSearchText(),
                    search = {
                        viewModel.Search(it)
                    },
                    resetSearch = {
                        viewModel.ResetSearch()
                    }
                )
            }
            items(viewModel.GetConnections()) { item ->
                ConnectionItem(
                    item = item,
                    openConfiguration = {
                        ActivityUtil.StartConfigurationActivity(
                            activity = activity,
                            connection = item
                        )
                    },
                    delete = {
                        viewModel.DeleteConnection(
                            item
                        )
                    },
                    backup = {
                        try {
                            val intent = Intent(
                                activity,
                                BackupReceiver::class.java
                            )

                            val bundle = Bundle()
                            bundle.putSerializable(
                                "Connection",
                                item
                            )
                            intent.putExtra(
                                "Bundle",
                                bundle
                            )

                            activity.sendBroadcast(intent)

                            viewModel.ShowBackupSnackbar(item)
                        } catch (ex: Exception) {
                            Timber.e(ex)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ConnectionItem(
    item: Connection,
    openConfiguration: () -> Unit,
    delete: () -> Unit,
    backup: () -> Unit
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                8.dp,
                4.dp
            )
            .clickable(
                onClick = openConfiguration
            )
            .testTag(item.Id.toString()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .width(65.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ConnectionIcon(
                connectionType = item.ConnectionType
            )
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(
                    8.dp,
                    0.dp
                )
                .weight(1f)
        ) {
            Text(
                modifier = Modifier
                    .padding(
                        8.dp,
                        8.dp,
                        8.dp,
                        0.dp
                    ),
                text = item.Name,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
            )

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    modifier = Modifier
                        .padding(
                            8.dp,
                            0.dp,
                            8.dp,
                            8.dp
                        ),
                    text = item.Username,
                    style = MaterialTheme.typography.body2,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        Column {
            IconButton(
                modifier = Modifier
                    .testTag("More"),
                onClick = {
                    menuExpanded = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = ""
                )
            }

            DropdownMenu(
                modifier = Modifier
                    .width(224.dp),
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                }
            ) {
                DropdownMenuItem(
                    modifier = Modifier
                        .testTag("DeleteMenuItem"),
                    onClick = {
                        delete()

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
                        .testTag("BackupMenuItem"),
                    onClick = {
                        backup()
                        menuExpanded = false
                    },
                    contentPadding = PaddingValues(
                        24.dp,
                        8.dp
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_backup_24),
                        contentDescription = stringResource(
                            id = R.string.Backup
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
                            id = R.string.Backup
                        )
                    )
                }
            }
        }
    }
}