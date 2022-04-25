package com.simplyteam.simplybackup.presentation.views.connection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
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
import com.simplyteam.simplybackup.data.models.events.connection.GoogleDriveConfigurationEvent
import com.simplyteam.simplybackup.presentation.viewmodels.connection.GoogleDriveConfigurationViewModel
import com.simplyteam.simplybackup.presentation.views.ErrorOutlinedTextField
import com.simplyteam.simplybackup.presentation.views.RadioButton
import kotlinx.coroutines.flow.Flow

@Composable
fun GoogleDriveConfigurationView(viewModel: GoogleDriveConfigurationViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InformationCard(
            name = viewModel.State.Name,
            onNameChange = {
                viewModel.OnEvent(
                    GoogleDriveConfigurationEvent.OnNameChange(
                        it
                    )
                )
            },
            nameError = viewModel.State.NameError,
            remotePath = viewModel.State.FolderLink,
            onRemotePathChange = {
                viewModel.OnEvent(
                    GoogleDriveConfigurationEvent.OnFolderLinkChange(
                        it
                    )
                )
            }
        )

        LoginCard(
            onClick = {
                viewModel.OnEvent(GoogleDriveConfigurationEvent.OnLoginCardClicked)
            },
            account = viewModel.State.SelectedAccount,
            isError = viewModel.State.SelectedAccountError
        )

        AccountSelectionDialog(
            shown = viewModel.State.SelectionDialogShown,
            onDismiss = {
                viewModel.OnEvent(GoogleDriveConfigurationEvent.OnDialogDismissed)
            },
            accountFlow = viewModel.AccountFlow,
            onConfirm = {
                viewModel.OnEvent(GoogleDriveConfigurationEvent.OnDialogDismissed)
                if (it == "NewAccount") {
                    viewModel.OnEvent(GoogleDriveConfigurationEvent.OnRequestSignIn)
                } else {
                    viewModel.OnEvent(
                        GoogleDriveConfigurationEvent.OnSelectedAccountChange(
                            it
                        )
                    )
                }
            }
        )
    }
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
            .testTag(
                if (isError) {
                    "LoginCardError"
                } else {
                    "LoginCard"
                }
            ),
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
                            )
                            .testTag("Account"),
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
    accountFlow: Flow<List<String>>,
    onConfirm: (String) -> Unit
) {
    var selectedAccount by remember {
        mutableStateOf("")
    }
    val accounts by accountFlow.collectAsState(initial = emptyList())

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