package com.simplyteam.simplybackup.presentation.views.connection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.ScheduleType
import com.simplyteam.simplybackup.presentation.viewmodels.connection.GoogleDriveConfigurationViewModel
import com.simplyteam.simplybackup.presentation.views.ErrorOutlinedTextField
import com.simplyteam.simplybackup.presentation.views.RadioButton

@Composable
fun GoogleDriveConfigurationView(viewModel: GoogleDriveConfigurationViewModel) {

    InformationCard(
        name = viewModel.Name,
        onNameChange = {
            viewModel.Name = it
        },
        nameError = viewModel.NameError,
        remotePath = viewModel.RemotePath,
        onRemotePathChange = {
            viewModel.RemotePath = it
        }
    )

    LoginCard(
        onClick = {
            viewModel.ShowSelectionDialog()
        },
        account = viewModel.SelectedAccount,
        isError = viewModel.SelectedAccountError
    )

    AccountSelectionDialog(
        shown = viewModel.SelectionDialogShown,
        onDismiss = {
            viewModel.HideSelectionDialog()
        },
        accounts = viewModel.GetAccounts(),
        onConfirm = {
            viewModel.HideSelectionDialog()
            if (it == "NewAccount") {
                viewModel.AddNewAccount()
            } else {
                viewModel.SelectedAccount = it
            }
        }
    )
}

@Composable
fun InformationCard(
    name: String,
    onNameChange: (String) -> Unit,
    nameError: Boolean,
    remotePath: String,
    onRemotePathChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = 0.dp,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colors.onBackground.copy(0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    8.dp,
                    0.dp,
                    8.dp,
                    8.dp
                )
        ) {
            ErrorOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("Name"),
                value = name,
                onValueChange = onNameChange,
                label = {
                    Text(
                        text = stringResource(
                            id = R.string.Name
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_baseline_title_24
                        ),
                        contentDescription = stringResource(
                            id = R.string.Name
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                errorModifier = Modifier
                    .testTag("NameError"),
                isError = nameError,
                errorText = stringResource(
                    id = R.string.EnterName
                )
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("RemotePath"),
                value = remotePath,
                onValueChange = onRemotePathChange,
                label = {
                    Text(
                        text = stringResource(
                            id = R.string.FolderLink
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_baseline_link_24
                        ),
                        contentDescription = stringResource(
                            id = R.string.FolderLink
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
        }
    }
}

@Composable
private fun LoginCard(
    onClick: () -> Unit,
    account: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("LoginCard"),
        elevation = 0.dp,
        border = if (isError) {
            BorderStroke(
                1.dp,
                MaterialTheme.colors.error.copy(1f)
            )
        } else {
            BorderStroke(
                1.dp,
                MaterialTheme.colors.onBackground.copy(0.12f)
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    modifier = Modifier
                        .padding(
                            8.dp,
                            8.dp,
                            8.dp,
                            2.dp
                        ),
                    style = MaterialTheme.typography.subtitle1,
                    text = stringResource(
                        id = R.string.SelectedAccount
                    ),
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
                        style = MaterialTheme.typography.body2,
                        text = account
                    )
                }
            }

            if (isError) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_baseline_error_24
                    ),
                    contentDescription = stringResource(
                        id = R.string.Error
                    ),
                    tint = MaterialTheme.colors.error
                )
            }
        }
    }
}

@Composable
fun AccountSelectionDialog(
    shown: Boolean,
    onDismiss: () -> Unit,
    accounts: List<String>,
    onConfirm: (String) -> Unit
) {
    var selectedAccount by remember {
        mutableStateOf("")
    }

    if (shown) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .testTag("AccountSelectionCard")
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    LazyColumn {
                        items(accounts) { account ->
                            RadioButton(
                                modifier = Modifier
                                    .testTag("${account}Radio"),
                                selected = selectedAccount == account,
                                onClick = {
                                    selectedAccount = account
                                },
                                text = account
                            )
                        }
                        item {
                            RadioButton(
                                modifier = Modifier
                                    .testTag("NewAccountRadio"),
                                selected = selectedAccount == "NewAccount",
                                onClick = {
                                    selectedAccount = "NewAccount"
                                },
                                text = stringResource(id = R.string.AddNewAccount)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onDismiss) {
                            Text(
                                text = stringResource(
                                    id = R.string.Cancel
                                )
                            )
                        }

                        TextButton(
                            modifier = Modifier
                                .testTag("OK"),
                            onClick = {
                                onConfirm(selectedAccount)
                            }
                        ) {
                            Text(
                                text = stringResource(
                                    id = R.string.OK
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}