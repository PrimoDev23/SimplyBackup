package com.simplyteam.simplybackup.presentation.views.connection

import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.ScheduleType
import com.simplyteam.simplybackup.data.models.Screen
import com.simplyteam.simplybackup.presentation.viewmodels.connection.ConnectionConfigurationViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.connection.NextCloudConfigurationViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.connection.SFTPConfigurationViewModel
import com.simplyteam.simplybackup.presentation.views.ConnectionIcon

@Composable
fun ConnectionConfigurationView(
    paddingValues: PaddingValues,
    navController: NavHostController,
    viewModel: ConnectionConfigurationViewModel
) {
    val activity = LocalContext.current as ComponentActivity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(viewModel.ScrollState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    8.dp,
                    0.dp
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ConnectionTypeRow(
                viewModel = viewModel
            )

            TypeSpecificOptions(
                selectedType = viewModel.SelectedConnectionType,
                viewModelMap = viewModel.ViewModelMap
            )

            ExtraInformationCard(
                backupPassword = viewModel.BackupPassword,
                onBackupPasswordChange = {
                    viewModel.BackupPassword = it
                },
                wifiOnly = viewModel.WifiOnly,
                onWifiOnlyChange = {
                    viewModel.WifiOnly = it
                }
            )

            PathConfigurationCard(
                onClick = {
                    navController.navigate(Screen.PathsConfiguration.Route)
                }
            )

            ScheduleTypeCard(
                selectedScheduleType = viewModel.SelectedScheduleType,
                onSelectionChange = {
                    viewModel.SelectedScheduleType = it
                }
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("Save"),
                onClick = {
                    viewModel.SaveConnection()
                },
                elevation = ButtonDefaults.elevation(2.dp)
            ) {
                Text(
                    text = stringResource(
                        id = R.string.Save
                    )
                )
            }
        }
    }
}

@Composable
private fun ConnectionTypeRow(viewModel: ConnectionConfigurationViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = 0.dp,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colors.onBackground.copy(0.12f)
        )
    ) {
        LazyRow(
            modifier = Modifier
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ConnectionType.values()) { type ->
                ConnectionButton(
                    type = type,
                    isSelected = viewModel.SelectedConnectionType == type,
                    typeSelected = {
                        viewModel.SelectedConnectionType = type
                    }
                )
            }
        }
    }
}

@Composable
private fun ConnectionButton(
    type: ConnectionType,
    isSelected: Boolean,
    typeSelected: () -> Unit
) {
    if (isSelected) {
        OutlinedButton(
            modifier = Modifier
                .height(64.dp)
                .width(92.dp)
                .testTag("${type.name}Selected"),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colors.primary
            ),
            onClick = {
            }) {
            ConnectionIcon(
                connectionType = type
            )
        }
    } else {
        OutlinedButton(
            modifier = Modifier
                .height(64.dp)
                .width(92.dp)
                .testTag(type.name),
            onClick = {
                typeSelected()
            }) {
            ConnectionIcon(
                connectionType = type
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TypeSpecificOptions(
    selectedType: ConnectionType,
    viewModelMap: Map<ConnectionType, ViewModel>
) {
    when (selectedType) {
        ConnectionType.NextCloud -> {
            NextCloudInformationFields(
                viewModel = viewModelMap[ConnectionType.NextCloud] as NextCloudConfigurationViewModel
            )
        }
        ConnectionType.SFTP -> {
            SFTPInformationFields(
                viewModel = viewModelMap[ConnectionType.SFTP] as SFTPConfigurationViewModel
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ExtraInformationCard(
    backupPassword: String,
    onBackupPasswordChange: (String) -> Unit,
    wifiOnly: Boolean,
    onWifiOnlyChange: (Boolean) -> Unit
) {
    val keyBoardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

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
                    0.dp
                )
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("BackupPassword"),
                label = {
                    Text(
                        text = stringResource(
                            id = R.string.BackupPassword
                        )
                    )
                },
                value = backupPassword,
                onValueChange = onBackupPasswordChange,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_baseline_vpn_key_24
                        ),
                        contentDescription = stringResource(
                            id = R.string.BackupPassword
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

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    modifier = Modifier
                        .testTag("WifiOnly"),
                    checked = wifiOnly,
                    onCheckedChange = onWifiOnlyChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colors.primary
                    )
                )

                Text(
                    text = stringResource(
                        id = R.string.WifiOnly
                    )
                )
            }
        }
    }
}

@Composable
private fun PathConfigurationCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("ConfigurePaths"),
        elevation = 0.dp,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colors.onBackground.copy(0.12f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .padding(8.dp),
                text = stringResource(
                    id = R.string.ConfigurePaths
                )
            )

            Icon(
                modifier = Modifier
                    .padding(8.dp),
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = ""
            )
        }
    }
}

@Composable
private fun ScheduleTypeCard(
    selectedScheduleType: ScheduleType,
    onSelectionChange: (ScheduleType) -> Unit
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                menuExpanded = true
            }
            .testTag("ScheduleTypeCard"),
        elevation = 0.dp,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colors.onBackground.copy(0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
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
                    id = R.string.SelectScheduleType
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
                    text = stringResource(
                        id = when (selectedScheduleType) {
                            ScheduleType.DAILY -> R.string.Daily
                            ScheduleType.WEEKLY -> R.string.Weekly
                            ScheduleType.MONTHLY -> R.string.Monthly
                            ScheduleType.YEARLY -> R.string.Yearly
                        }
                    )
                )
            }

            DropdownMenu(
                modifier = Modifier
                    .fillMaxWidth(),
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                }
            ) {
                DropdownMenuItem(
                    modifier = Modifier
                        .testTag("DailyMenuItem"),
                    onClick = {
                        onSelectionChange(ScheduleType.DAILY)
                        menuExpanded = false
                    },
                    contentPadding = PaddingValues(
                        4.dp,
                        8.dp
                    )
                ) {
                    Text(
                        modifier = Modifier
                            .padding(
                                20.dp,
                                0.dp,
                                0.dp,
                                0.dp
                            ),
                        text = stringResource(
                            id = R.string.Daily
                        )
                    )
                }

                DropdownMenuItem(
                    modifier = Modifier
                        .testTag("WeeklyMenuItem"),
                    onClick = {
                        onSelectionChange(ScheduleType.WEEKLY)
                        menuExpanded = false
                    },
                    contentPadding = PaddingValues(
                        4.dp,
                        8.dp
                    )
                ) {
                    Text(
                        modifier = Modifier
                            .padding(
                                20.dp,
                                0.dp,
                                0.dp,
                                0.dp
                            ),
                        text = stringResource(
                            id = R.string.Weekly
                        )
                    )
                }

                DropdownMenuItem(
                    modifier = Modifier
                        .testTag("MonthlyMenuItem"),
                    onClick = {
                        onSelectionChange(ScheduleType.MONTHLY)
                        menuExpanded = false
                    },
                    contentPadding = PaddingValues(
                        4.dp,
                        8.dp
                    )
                ) {
                    Text(
                        modifier = Modifier
                            .padding(
                                20.dp,
                                0.dp,
                                0.dp,
                                0.dp
                            ),
                        text = stringResource(
                            id = R.string.Monthly
                        )
                    )
                }

                DropdownMenuItem(
                    modifier = Modifier
                        .testTag("YearlyMenuItem"),
                    onClick = {
                        onSelectionChange(ScheduleType.YEARLY)
                        menuExpanded = false
                    },
                    contentPadding = PaddingValues(
                        4.dp,
                        8.dp
                    )
                ) {
                    Text(
                        modifier = Modifier
                            .padding(
                                20.dp,
                                0.dp,
                                0.dp,
                                0.dp
                            ),
                        text = stringResource(
                            id = R.string.Yearly
                        )
                    )
                }
            }
        }
    }
}