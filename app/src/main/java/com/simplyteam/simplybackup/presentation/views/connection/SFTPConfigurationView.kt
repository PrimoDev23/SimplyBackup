package com.simplyteam.simplybackup.presentation.views.connection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.presentation.viewmodels.connection.SFTPConfigurationViewModel

class SFTPConfigurationView {

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun BuildInformationFields(viewModel: SFTPConfigurationViewModel) {
        val keyBoardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    8.dp,
                    0.dp,
                    8.dp,
                    8.dp
                ),
            elevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        8.dp,
                        8.dp,
                        8.dp,
                        8.dp
                    )
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("Name"),
                    label = {
                        Text(
                            text = stringResource(
                                id = R.string.Name
                            )
                        )
                    },
                    value = viewModel.Name.value,
                    onValueChange = {
                        viewModel.Name.value = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_title_24),
                            contentDescription = stringResource(
                                id = R.string.Name
                            )
                        )
                    },
                    singleLine = true
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("URL"),
                    label = {
                        Text(
                            text = stringResource(
                                id = R.string.URL
                            )
                        )
                    },
                    value = viewModel.URL.value,
                    onValueChange = {
                        viewModel.URL.value = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_link_24),
                            contentDescription = stringResource(
                                id = R.string.URL
                            )
                        )
                    },
                    singleLine = true
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("Username"),
                    label = {
                        Text(
                            text = stringResource(
                                id = R.string.Username
                            )
                        )
                    },
                    value = viewModel.Username.value,
                    onValueChange = {
                        viewModel.Username.value = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.ic_baseline_person_24
                            ),
                            contentDescription = stringResource(
                                id = R.string.Username
                            )
                        )
                    },
                    singleLine = true,
                    keyboardActions = KeyboardActions {
                        this.defaultKeyboardAction(ImeAction.Next)
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("Password"),
                    label = {
                        Text(
                            text = stringResource(
                                id = R.string.Password
                            )
                        )
                    },
                    value = viewModel.Password.value,
                    onValueChange = {
                        viewModel.Password.value = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.ic_baseline_vpn_key_24
                            ),
                            contentDescription = stringResource(
                                id = R.string.Password
                            )
                        )
                    },
                    singleLine = true,
                    keyboardActions = KeyboardActions {
                        this.defaultKeyboardAction(ImeAction.Next)
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("RemotePath"),
                    label = {
                        Text(
                            text = stringResource(
                                id = R.string.RemotePath
                            )
                        )
                    },
                    value = viewModel.RemotePath.value,
                    onValueChange = {
                        viewModel.RemotePath.value = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
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
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyBoardController?.hide()
                            focusManager.clearFocus(true)
                        }
                    )
                )
            }
        }
    }

}