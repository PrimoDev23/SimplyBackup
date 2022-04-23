package com.simplyteam.simplybackup.presentation.views.connection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.events.connection.NextCloudConfigurationEvent
import com.simplyteam.simplybackup.presentation.viewmodels.connection.NextCloudConfigurationViewModel
import com.simplyteam.simplybackup.presentation.views.ErrorOutlinedTextField

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NextCloudInformationFields(viewModel: NextCloudConfigurationViewModel) {
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
                value = viewModel.State.Name,
                onValueChange = {
                    viewModel.OnEvent(
                        NextCloudConfigurationEvent.OnNameChange(
                            it
                        )
                    )
                },
                label = {
                    Text(
                        text = stringResource(
                            id = R.string.Name
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_title_24),
                        contentDescription = stringResource(
                            id = R.string.Name
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                errorModifier = Modifier
                    .testTag("NameError"),
                isError = viewModel.State.NameError,
                errorText = stringResource(
                    id = R.string.EnterName
                )
            )

            ErrorOutlinedTextField(
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
                value = viewModel.State.Host,
                onValueChange = {
                    viewModel.OnEvent(
                        NextCloudConfigurationEvent.OnHostChange(
                            it
                        )
                    )
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
                errorModifier = Modifier
                    .testTag("HostError"),
                isError = viewModel.State.HostError,
                errorText = stringResource(
                    id = R.string.EnterHost
                )
            )

            ErrorOutlinedTextField(
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
                value = viewModel.State.Username,
                onValueChange = {
                    viewModel.OnEvent(
                        NextCloudConfigurationEvent.OnUsernameChange(
                            it
                        )
                    )
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
                errorModifier = Modifier
                    .testTag("UsernameError"),
                isError = viewModel.State.UsernameError,
                errorText = stringResource(
                    id = R.string.EnterUsername
                )
            )

            ErrorOutlinedTextField(
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
                value = viewModel.State.Password,
                onValueChange = {
                    viewModel.OnEvent(
                        NextCloudConfigurationEvent.OnPasswordChange(
                            it
                        )
                    )
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
                errorModifier = Modifier
                    .testTag("PasswordError"),
                isError = viewModel.State.PasswordError,
                errorText = stringResource(
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
                value = viewModel.State.RemotePath,
                onValueChange = {
                    viewModel.OnEvent(
                        NextCloudConfigurationEvent.OnRemotePathChange(
                            it
                        )
                    )
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