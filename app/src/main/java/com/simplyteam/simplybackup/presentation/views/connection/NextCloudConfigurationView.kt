package com.simplyteam.simplybackup.presentation.views.connection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
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
import com.simplyteam.simplybackup.presentation.viewmodels.connection.NextCloudConfigurationViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NextCloudInformationFields(viewModel: NextCloudConfigurationViewModel) {
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
                value = viewModel.Name,
                onValueChange = {
                    viewModel.Name = it
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
                singleLine = true,
                isError = viewModel.NameErrorShown
            )

            TextFieldErrorText(
                modifier = Modifier
                    .testTag("NameError"),
                shown = viewModel.NameErrorShown,
                text = stringResource(
                    id = R.string.EnterName
                )
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("Host"),
                label = {
                    Text(
                        text = stringResource(
                            id = R.string.Host
                        )
                    )
                },
                value = viewModel.Host,
                onValueChange = {
                    viewModel.Host = it
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_link_24),
                        contentDescription = stringResource(
                            id = R.string.Host
                        )
                    )
                },
                singleLine = true,
                isError = viewModel.HostErrorShown
            )

            TextFieldErrorText(
                modifier = Modifier
                    .testTag("HostError"),
                shown = viewModel.HostErrorShown,
                text = stringResource(
                    id = R.string.EnterHost
                )
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
                value = viewModel.Username,
                onValueChange = {
                    viewModel.Username = it
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
                isError = viewModel.UsernameErrorShown
            )

            TextFieldErrorText(
                modifier = Modifier
                    .testTag("UsernameError"),
                shown = viewModel.UsernameErrorShown,
                text = stringResource(
                    id = R.string.EnterUsername
                )
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
                value = viewModel.Password,
                onValueChange = {
                    viewModel.Password = it
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
                isError = viewModel.PasswordErrorShown
            )

            TextFieldErrorText(
                modifier = Modifier
                    .testTag("PasswordError"),
                shown = viewModel.PasswordErrorShown,
                text = stringResource(
                    id = R.string.EnterPassword
                )
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
                value = viewModel.RemotePath,
                onValueChange = {
                    viewModel.RemotePath = it
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
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
                singleLine = true
            )
        }
    }
}

@Composable
private fun TextFieldErrorText(
    modifier: Modifier,
    shown: Boolean,
    text: String
) {
    if (shown) {
        Text(
            modifier = modifier
                .padding(
                    12.dp,
                    4.dp,
                    0.dp,
                    0.dp
                ),
            text = text,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption
        )
    }
}