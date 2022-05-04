package com.simplyteam.simplybackup.presentation.views.connection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.simplyteam.simplybackup.data.models.events.connection.PathsConfigurationEvent
import com.simplyteam.simplybackup.presentation.viewmodels.connection.ConnectionConfigurationViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.connection.PathsConfigurationViewModel
import com.simplyteam.simplybackup.presentation.views.ErrorOutlinedTextField

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PathsConfigurationView(
    paddingValues: PaddingValues,
    viewModel: PathsConfigurationViewModel
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
                ErrorOutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("CurrentPath"),
                    value = viewModel.State.CurrentPath,
                    onValueChange = {
                        viewModel.OnEvent(
                            PathsConfigurationEvent.OnCurrentPathChange(
                                it
                            )
                        )
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
                            viewModel.OnEvent(PathsConfigurationEvent.OnAddPathClicked)
                        }
                    ),
                    errorModifier = Modifier
                        .testTag("CurrentPathError"),
                    isError = viewModel.State.CurrentPathError,
                    errorText = stringResource(
                        id = R.string.EnterPath
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
                        viewModel.OnEvent(PathsConfigurationEvent.OnAddPathClicked)
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
            items(viewModel.State.Paths) { path ->
                PathItem(
                    path = path.Path,
                    removePath = {
                        viewModel.OnEvent(
                            PathsConfigurationEvent.OnDeletePathClicked(
                                path
                            )
                        )
                    }
                )
            }
        }

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("Save"),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colors.primary
            ),
            onClick = {
                viewModel.OnEvent(PathsConfigurationEvent.OnSaveClicked)
            }
        ) {
            Text(
                text = stringResource(
                    id = R.string.Save
                )
            )
        }
    }
}

@Composable
fun PathItem(
    path: String,
    removePath: () -> Unit
) {
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
            text = path
        )

        IconButton(
            modifier = Modifier
                .testTag("DeletePath"),
            onClick = removePath
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