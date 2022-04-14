package com.simplyteam.simplybackup.presentation.views.main

import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.presentation.activities.ConnectionConfigurationActivity
import com.simplyteam.simplybackup.presentation.viewmodels.main.ConnectionOverviewViewModel
import com.simplyteam.simplybackup.presentation.views.ConnectionIcon
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ConnectionOverviewView(
    paddingValues: PaddingValues,
    viewModel: ConnectionOverviewViewModel,
    lazyListState: LazyListState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f),
            state = lazyListState
        ) {
            items(viewModel.GetConnections()) { item ->
                ConnectionCard(
                    item = item,
                    viewModel = viewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ConnectionCard(
    item: Connection,
    viewModel: ConnectionOverviewViewModel
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.StartConfiguration(context, item)
            }
            .testTag(item.Id.toString())
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
                    connectionType = item.ConnectionType
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(
                        8.dp,
                        0.dp
                    )
                    .weight(1f)
            ) {
                Text(
                    modifier = Modifier
                        .padding(
                            8.dp,
                            8.dp,
                            8.dp,
                            0.dp
                        ),
                    text = item.Name,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
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
                        text = item.Username,
                        style = MaterialTheme.typography.body2,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            IconButton(
                onClick = {
                    scope.launch {
                        viewModel.DeleteConnection(item)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(
                        id = R.string.Delete
                    )
                )
            }
        }
    }
}