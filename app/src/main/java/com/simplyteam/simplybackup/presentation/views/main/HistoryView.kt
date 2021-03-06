package com.simplyteam.simplybackup.presentation.views.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.HistoryData
import com.simplyteam.simplybackup.data.models.events.main.HistoryEvent
import com.simplyteam.simplybackup.presentation.viewmodels.main.HistoryViewModel
import com.simplyteam.simplybackup.presentation.views.ConnectionIcon
import com.simplyteam.simplybackup.presentation.views.SearchBox
import com.simplyteam.simplybackup.presentation.views.collectFlowLifecycleAware

@Composable
fun HistoryView(
    paddingValues: PaddingValues,
    viewModel: HistoryViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        val data by viewModel.HistoryDataFlow.collectFlowLifecycleAware(emptyList())
        val searchText by viewModel.SearchTextFlow.collectAsState()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    0.dp,
                    0.dp,
                    0.dp,
                    8.dp
                )
                .testTag("History"),
            state = viewModel.ListState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SearchBox(
                    searchText = searchText,
                    search = {
                        viewModel.OnEvent(
                            HistoryEvent.Search(
                                it
                            )
                        )
                    },
                    resetSearch = {
                        viewModel.OnEvent(HistoryEvent.ResetSearch)
                    }
                )
            }
            items(data) { historyData ->
                HistoryCard(
                    historyData = historyData,
                    openHistory = {
                        viewModel.OnEvent(
                            HistoryEvent.OnOpenHistory(
                                historyData.Connection
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun HistoryCard(
    historyData: HistoryData,
    openHistory: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                8.dp,
                0.dp
            )
            .clickable(
                onClick = openHistory
            )
            .testTag(historyData.Connection.Name),
        elevation = 0.dp,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colors.onBackground.copy(0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .width(65.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ConnectionIcon(
                        connectionType = historyData.Connection.ConnectionType
                    )
                }

                Text(
                    modifier = Modifier
                        .padding(
                            12.dp,
                            8.dp,
                            8.dp,
                            0.dp
                        ),
                    text = historyData.Connection.Name,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HistoryInformation(
                    modifier = Modifier
                        .weight(1f),
                    titleId = R.string.LastBackup,
                    text = historyData.LastBackup
                )

                HistoryInformation(
                    modifier = Modifier
                        .weight(1f),
                    titleId = R.string.LastBackupSize,
                    text = historyData.LastBackupSize
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HistoryInformation(
                    modifier = Modifier
                        .weight(1f),
                    titleId = R.string.NextBackup,
                    text = historyData.NextBackup
                )

                HistoryInformation(
                    modifier = Modifier
                        .weight(1f),
                    titleId = R.string.TotalBackupSize,
                    text = historyData.TotalBackedUpSize
                )
            }
        }
    }
}

@Composable
private fun HistoryInformation(
    modifier: Modifier,
    titleId: Int,
    text: String
) {
    Column(
        modifier = modifier
            .padding(8.dp)
    ) {
        Text(
            text = stringResource(
                id = titleId
            ),
            style = MaterialTheme.typography.subtitle1
        )

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = text,
                style = MaterialTheme.typography.body2
            )
        }
    }
}