package com.simplyteam.simplybackup.presentation.views.connection

import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.ScheduleType
import com.simplyteam.simplybackup.data.models.Screen
import com.simplyteam.simplybackup.presentation.viewmodels.connection.ConnectionConfigurationViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.connection.NextCloudConfigurationViewModel
import kotlinx.coroutines.launch

class ConnectionConfigurationView(
    private val _nextCloudConfigurationView: NextCloudConfigurationView
) {

    @Composable
    fun Build(
        paddingValues: PaddingValues,
        navController: NavHostController,
        viewModel: ConnectionConfigurationViewModel
    ) {
        val scope = rememberCoroutineScope()
        val activity = LocalContext.current as ComponentActivity
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            BuildConnectionTypeRow(
                viewModel = viewModel
            )

            BuildInformationFields(
                viewModel = viewModel
            )

            BuildPathConfigurationView(
                navController = navController
            )

            BuildScheduleTypeCard(
                viewModel = viewModel
            )

            BuildScheduleTypeDialog(
                viewModel = viewModel
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        8.dp,
                        0.dp,
                        8.dp,
                        8.dp
                    )
                    .testTag("Save"),
                onClick = {
                    scope.launch {
                        viewModel.SaveConnection(activity)
                    }
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

    @OptIn(ExperimentalComposeUiApi::class)
    private @Composable
    fun BuildInformationFields(viewModel: ConnectionConfigurationViewModel) {
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
                when (viewModel.ConnectionType.value) {
                    ConnectionType.NextCloud -> {
                        _nextCloudConfigurationView.BuildInformationFields(
                            viewModel = viewModel.ViewModelMap[ConnectionType.NextCloud] as NextCloudConfigurationViewModel
                        )
                    }
                }

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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            0.dp,
                            8.dp,
                            0.dp,
                            0.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        modifier = Modifier
                            .testTag("WifiOnly"),
                        checked = viewModel.WifiOnly.value,
                        onCheckedChange = {
                            viewModel.WifiOnly.value = it
                        },
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
    private fun BuildScheduleTypeCard(viewModel: ConnectionConfigurationViewModel) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    8.dp,
                    0.dp,
                    8.dp,
                    8.dp
                )
                .clickable {
                    viewModel.ScheduleTypeDialogShown.value = true
                }
                .testTag("ScheduleTypeCard"),
            elevation = 2.dp
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
                            id = when (viewModel.ScheduleType.value) {
                                ScheduleType.DAILY -> R.string.Daily
                                ScheduleType.WEEKLY -> R.string.Weekly
                                ScheduleType.MONTHLY -> R.string.Monthly
                                ScheduleType.YEARLY -> R.string.Yearly
                            }
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun BuildScheduleTypeDialog(viewModel: ConnectionConfigurationViewModel) {
        if (viewModel.ScheduleTypeDialogShown.value) {
            Dialog(
                onDismissRequest = {
                    viewModel.ScheduleTypeDialogShown.value = false
                }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            text = stringResource(
                                id = R.string.SelectScheduleType
                            ),
                            fontWeight = FontWeight.Bold
                        )

                        Divider()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    8.dp,
                                    0.dp
                                )
                                .clickable {
                                    viewModel.UpdateScheduleType(ScheduleType.DAILY)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            RadioButton(
                                modifier = Modifier
                                    .testTag("DailyScheduleType"),
                                selected = viewModel.ScheduleType.value == ScheduleType.DAILY,
                                onClick = {
                                    viewModel.UpdateScheduleType(ScheduleType.DAILY)
                                }
                            )

                            Text(
                                text = stringResource(
                                    id = R.string.Daily
                                )
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    8.dp,
                                    0.dp
                                )
                                .clickable {
                                    viewModel.UpdateScheduleType(ScheduleType.WEEKLY)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            RadioButton(
                                modifier = Modifier
                                    .testTag("WeeklyScheduleType"),
                                selected = viewModel.ScheduleType.value == ScheduleType.WEEKLY,
                                onClick = {
                                    viewModel.UpdateScheduleType(ScheduleType.WEEKLY)
                                }
                            )

                            Text(
                                text = stringResource(
                                    id = R.string.Weekly
                                )
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    8.dp,
                                    0.dp
                                )
                                .clickable {
                                    viewModel.UpdateScheduleType(ScheduleType.MONTHLY)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            RadioButton(
                                modifier = Modifier
                                    .testTag("MonthlyScheduleType"),
                                selected = viewModel.ScheduleType.value == ScheduleType.MONTHLY,
                                onClick = {
                                    viewModel.UpdateScheduleType(ScheduleType.MONTHLY)
                                }
                            )

                            Text(
                                text = stringResource(
                                    id = R.string.Monthly
                                )
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    8.dp,
                                    0.dp
                                )
                                .clickable {
                                    viewModel.UpdateScheduleType(ScheduleType.YEARLY)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            RadioButton(
                                modifier = Modifier
                                    .testTag("YearlyScheduleType"),
                                selected = viewModel.ScheduleType.value == ScheduleType.YEARLY,
                                onClick = {
                                    viewModel.UpdateScheduleType(ScheduleType.YEARLY)
                                }
                            )

                            Text(
                                text = stringResource(
                                    id = R.string.Yearly
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun BuildConnectionTypeRow(viewModel: ConnectionConfigurationViewModel) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = 2.dp
        ) {
            LazyRow(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                items(ConnectionType.values()) { type ->
                    BuildConnectionButton(
                        viewModel = viewModel,
                        type = type
                    )
                }
            }
        }
    }

    @Composable
    private fun BuildConnectionButton(
        viewModel: ConnectionConfigurationViewModel,
        type: ConnectionType
    ) {
        if (viewModel.ConnectionType.value == type) {
            OutlinedButton(
                modifier = Modifier
                    .height(70.dp)
                    .width(98.dp)
                    .padding(4.dp)
                    .testTag(type.name),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colors.primary
                ),
                onClick = {
                }) {
                viewModel.GetIconProvider()
                    .BuildIconFromConnectionType(
                        connectionType = type
                    )
            }
        } else {
            OutlinedButton(
                modifier = Modifier
                    .height(70.dp)
                    .width(98.dp)
                    .padding(4.dp)
                    .testTag(type.name),
                onClick = {
                    viewModel.ConnectionType.value = type
                }) {
                viewModel.GetIconProvider()
                    .BuildIconFromConnectionType(
                        connectionType = type
                    )
            }
        }
    }

    @Composable
    private fun BuildPathConfigurationView(
        navController: NavHostController
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    8.dp,
                    0.dp,
                    8.dp,
                    8.dp
                )
                .clickable {
                    navController.navigate(Screen.PathsConfiguration.Route)
                }
                .testTag("ConfigurePaths"),
            elevation = 2.dp
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

}