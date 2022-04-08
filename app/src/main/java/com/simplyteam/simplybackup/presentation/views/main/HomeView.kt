package com.simplyteam.simplybackup.presentation.views.main

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.presentation.activities.BackupHistoryActivity
import com.simplyteam.simplybackup.presentation.viewmodels.main.HomeViewModel

class HomeView {

    @Composable
    fun Build(paddingValues: PaddingValues) {
        val viewModel: HomeViewModel = hiltViewModel()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .testTag("History")
            ) {
                items(viewModel.GetConnections()) { connection ->
                    BuildHistoryCard(
                        viewModel = viewModel,
                        connection = connection
                    )
                }
            }
        }
    }

    @Composable
    private fun BuildHistoryCard(viewModel: HomeViewModel, connection: Connection) {
        val context = LocalContext.current

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    val intent = Intent(context, BackupHistoryActivity::class.java)
                    intent.putExtra("Connection", connection)

                    context.startActivity(intent)
                },
            elevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val data = viewModel.BuildHistoryDataForConnection(connection)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    viewModel.GetIconProvider().BuildIconFromConnectionType(
                        connectionType = data.Type
                    )

                    Text(
                        modifier = Modifier
                            .padding(12.dp, 8.dp, 8.dp, 0.dp),
                        text = data.Name,
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
                    BuildInformationField(
                        modifier = Modifier
                            .weight(1f),
                        titleId = R.string.LastBackup,
                        text = data.LastBackup
                    )

                    BuildInformationField(
                        modifier = Modifier
                            .weight(1f),
                        titleId = R.string.LastBackupSize,
                        text = data.LastBackupSize
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BuildInformationField(
                        modifier = Modifier
                            .weight(1f),
                        titleId = R.string.NextBackup,
                        text = data.NextBackup
                    )

                    BuildInformationField(
                        modifier = Modifier
                            .weight(1f),
                        titleId = R.string.TotalBackupSize,
                        text = data.TotalBackedUpSize
                    )
                }
            }
        }
    }

    @Composable
    private fun BuildInformationField(
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
}