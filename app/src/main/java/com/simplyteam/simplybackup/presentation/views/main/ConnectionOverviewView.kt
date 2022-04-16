package com.simplyteam.simplybackup.presentation.views.main

import androidx.activity.ComponentActivity
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
import com.simplyteam.simplybackup.presentation.viewmodels.main.ConnectionOverviewViewModel
import com.simplyteam.simplybackup.presentation.views.ConnectionIcon
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ConnectionOverviewView(
    paddingValues: PaddingValues,
    viewModel: ConnectionOverviewViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f),
            state = viewModel.ListState
        ) {
            item {
                SearchBox(viewModel = viewModel)
            }
            items(viewModel.GetConnections()) { item ->
                ConnectionItem(
                    item = item,
                    viewModel = viewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchBox(viewModel: ConnectionOverviewViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Card(
        modifier = Modifier
            .padding(
                16.dp,
                0.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(
                        12.dp,
                        0.dp,
                        0.dp,
                        0.dp
                    ),
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(
                    id = R.string.Search
                )
            )

            BasicTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        8.dp,
                        0.dp,
                        0.dp,
                        0.dp
                    ),
                value = viewModel.GetSearchText(),
                onValueChange = {
                    viewModel.Search(it)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus(true)
                        keyboardController?.hide()
                    }
                ),
                singleLine = true,
                cursorBrush = Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colors.onBackground,
                        MaterialTheme.colors.onBackground,
                    )
                ),
                textStyle = MaterialTheme.typography.subtitle1.copy(MaterialTheme.colors.onBackground)
            )

            IconButton(
                onClick = {
                    viewModel.ResetSearch()

                    focusManager.clearFocus(true)
                    keyboardController?.hide()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(
                        id = R.string.Delete
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ConnectionItem(
    item: Connection,
    viewModel: ConnectionOverviewViewModel
) {
    val context = LocalContext.current as ComponentActivity

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.StartConfiguration(
                    context,
                    item
                )
            }
            .testTag(item.Id.toString())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    8.dp,
                    4.dp
                ),
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
                        .width(240.dp),
                    expanded = menuExpanded,
                    onDismissRequest = {
                        menuExpanded = false
                    }
                ) {
                    DropdownMenuItem(
                        modifier = Modifier
                            .testTag("DeleteMenuItem"),
                        onClick = {
                            viewModel.DeleteConnection(
                                context,
                                item
                            )
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
                            viewModel.RunBackup(
                                context,
                                item
                            )
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
}