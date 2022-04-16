package com.simplyteam.simplybackup.presentation.views.main

import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.presentation.viewmodels.main.HistoryViewModel
import com.simplyteam.simplybackup.presentation.views.ConnectionIcon

@Composable
fun HistoryView(
    paddingValues: PaddingValues,
    viewModel: HistoryViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("History"),
            state = viewModel.ListState
        ) {
            item {
                SearchBox(
                    viewModel = viewModel
                )
            }
            items(viewModel.GetConnections()) { connection ->
                HistoryCard(
                    viewModel = viewModel,
                    connection = connection
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchBox(viewModel: HistoryViewModel) {
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

@Composable
private fun HistoryCard(
    viewModel: HistoryViewModel,
    connection: Connection
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                viewModel.OpenHistory(context as ComponentActivity, connection)
            }
            .testTag(connection.Name),
        elevation = 0.dp,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colors.onBackground.copy(0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val data = viewModel.BuildHistoryDataForConnection(connection)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .width(65.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ConnectionIcon(
                        connectionType = data.Type
                    )
                }

                Text(
                    modifier = Modifier
                        .padding(
                            12.dp,
                            8.dp,
                            8.dp,
                            0.dp
                        ),
                    text = data.Name,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HistoryInformation(
                    modifier = Modifier
                        .weight(1f),
                    titleId = R.string.LastBackup,
                    text = data.LastBackup
                )

                HistoryInformation(
                    modifier = Modifier
                        .weight(1f),
                    titleId = R.string.LastBackupSize,
                    text = data.LastBackupSize
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HistoryInformation(
                    modifier = Modifier
                        .weight(1f),
                    titleId = R.string.NextBackup,
                    text = data.NextBackup
                )

                HistoryInformation(
                    modifier = Modifier
                        .weight(1f),
                    titleId = R.string.TotalBackupSize,
                    text = data.TotalBackedUpSize
                )
            }
        }
    }
}

@Composable
private fun HistoryInformation(
    modifier: Modifier,
    titleId: Int,
    text: String
) {
    Column(
        modifier = modifier
            .padding(8.dp)
    ) {
        Text(
            text = stringResource(
                id = titleId
            ),
            style = MaterialTheme.typography.subtitle1
        )

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = text,
                style = MaterialTheme.typography.body2
            )
        }
    }
}