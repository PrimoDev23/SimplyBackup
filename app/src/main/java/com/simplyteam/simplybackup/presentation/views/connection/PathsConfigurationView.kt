package com.simplyteam.simplybackup.presentation.views.connection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.presentation.viewmodels.connection.ConnectionConfigurationViewModel

class PathsConfigurationView {

    @Composable
    fun Build(
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
                elevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
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
                        }
                    )

                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
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

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                elevation = 2.dp
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("Paths")
                ) {
                    itemsIndexed(viewModel.Paths) { index, path ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
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
    }

}