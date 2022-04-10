package com.simplyteam.simplybackup.presentation.views.connection

import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.ScheduleType
import com.simplyteam.simplybackup.data.models.Screen
import com.simplyteam.simplybackup.presentation.viewmodels.connection.ConnectionConfigurationViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.connection.SFTPConfigurationViewModel
import com.simplyteam.simplybackup.presentation.viewmodels.connection.NextCloudConfigurationViewModel
import kotlinx.coroutines.launch

class ConnectionConfigurationView(
    private val _nextCloudConfigurationView: NextCloudConfigurationView,
    private val _sFTPConfigurationView: SFTPConfigurationView
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

            BuildWifiOnlyCard(
                viewModel = viewModel
            )

            BuildPathConfigurationCard(
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
        if (viewModel.ConnectionType == type) {
            OutlinedButton(
                modifier = Modifier
                    .height(70.dp)
                    .width(98.dp)
                    .padding(4.dp)
                    .testTag("${type.name}Selected"),
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
                    viewModel.ConnectionType = type
                }) {
                viewModel.GetIconProvider()
                    .BuildIconFromConnectionType(
                        connectionType = type
                    )
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    private fun BuildInformationFields(viewModel: ConnectionConfigurationViewModel) {
        when (viewModel.ConnectionType) {
            ConnectionType.NextCloud -> {
                _nextCloudConfigurationView.BuildInformationFields(
                    viewModel = viewModel.ViewModelMap[ConnectionType.NextCloud] as NextCloudConfigurationViewModel
                )
            }
            ConnectionType.SFTP -> {
                _sFTPConfigurationView.BuildInformationFields(
                    viewModel = viewModel.ViewModelMap[ConnectionType.SFTP] as SFTPConfigurationViewModel
                )
            }
        }
    }

    @Composable
    private fun BuildWifiOnlyCard(viewModel: ConnectionConfigurationViewModel) {
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        modifier = Modifier
                            .testTag("WifiOnly"),
                        checked = viewModel.WifiOnly,
                        onCheckedChange = {
                            viewModel.WifiOnly = it
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
    private fun BuildPathConfigurationCard(
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
                    viewModel.ScheduleTypeDialogShown = true
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
                            id = when (viewModel.ScheduleType) {
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
        if (viewModel.ScheduleTypeDialogShown) {
            Dialog(
                onDismissRequest = {
                    viewModel.ScheduleTypeDialogShown = false
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
                                selected = viewModel.ScheduleType == ScheduleType.DAILY,
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
                                selected = viewModel.ScheduleType == ScheduleType.WEEKLY,
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
                                selected = viewModel.ScheduleType == ScheduleType.MONTHLY,
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
                                selected = viewModel.ScheduleType == ScheduleType.YEARLY,
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

}