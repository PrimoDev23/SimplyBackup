package com.simplyteam.simplybackup.presentation.views.connection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.presentation.viewmodels.connection.ConnectionConfigurationViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PathsConfigurationView(
    paddingValues: PaddingValues,
    viewModel: ConnectionConfigurationViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = 0.dp,
            border = BorderStroke(
                1.dp,
                MaterialTheme.colors.onBackground.copy(0.12f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("CurrentPath"),
                    value = viewModel.CurrentPath,
                    onValueChange = {
                        viewModel.CurrentPath = it
                    },
                    label = {
                        Text(
                            text = stringResource(
                                id = R.string.Path
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.ic_baseline_folder_24
                            ),
                            contentDescription = stringResource(
                                id = R.string.RemotePath
                            )
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            viewModel.AddPath(viewModel.CurrentPath)
                        }
                    )
                )

                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("AddPath"),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colors.primary
                    ),
                    onClick = {
                        viewModel.AddPath(viewModel.CurrentPath)
                    }) {
                    Text(
                        text = stringResource(
                            id = R.string.AddPath
                        )
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(
                    8.dp,
                    0.dp
                )
                .testTag("Paths")
        ) {
            itemsIndexed(viewModel.Paths) { index, path ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            8.dp,
                            0.dp,
                            0.dp,
                            0.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp),
                        text = path.Path
                    )

                    IconButton(
                        modifier = Modifier
                            .testTag("DeletePath"),
                        onClick = {
                            viewModel.RemovePath(path)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(
                                id = R.string.Delete
                            )
                        )
                    }
                }
            }
        }
    }
}