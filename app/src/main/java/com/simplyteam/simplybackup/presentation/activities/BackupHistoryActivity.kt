package com.simplyteam.simplybackup.presentation.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.presentation.theme.SimplyBackupTheme
import com.simplyteam.simplybackup.presentation.viewmodels.backuphistory.BackupHistoryViewModel
import com.simplyteam.simplybackup.presentation.views.backuphistory.BackupHistoryView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackupHistoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val connection = this.intent.extras?.get("Connection") as Connection

        setContent {
            SimplyBackupTheme {
                val viewModel: BackupHistoryViewModel = hiltViewModel()
                val lazyListState = rememberLazyListState()

                LaunchedEffect(key1 = true) {
                    viewModel.InitValues(connection)
                }

                Scaffold(
                    topBar = {
                        BuildTopBar(
                            listState = lazyListState
                        )
                    },
                    snackbarHost = {
                        SnackbarHost(
                            modifier = Modifier
                                .testTag("RestoreSnackbar"),
                            hostState = viewModel.RestoreSnackbarState,
                            snackbar = {
                                Snackbar(
                                    snackbarData = it,
                                    backgroundColor = MaterialTheme.colors.background,
                                    contentColor = MaterialTheme.colors.onBackground
                                )
                            }
                        )
                    }
                ) {
                    BackupHistoryView(
                        paddingValues = it,
                        viewModel = viewModel,
                        listState = lazyListState
                    )
                }
            }
        }
    }

    @Composable
    private fun BuildTopBar(listState: LazyListState) {
        val activity = LocalContext.current as ComponentActivity

        val elevation by animateDpAsState(
            if (listState.firstVisibleItemIndex == 0) {
                minOf(
                    listState.firstVisibleItemScrollOffset.toFloat().dp,
                    AppBarDefaults.TopAppBarElevation
                )
            } else {
                AppBarDefaults.TopAppBarElevation
            }
        )

        TopAppBar(
            title = {
                Text(
                    text =
                    stringResource(id = R.string.History)
                )
            },
            navigationIcon = {
                IconButton(
                    modifier = Modifier
                        .testTag("BackButton"),
                    onClick = {
                        activity.finish()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = ""
                    )
                }
            },
            elevation = elevation
        )
    }
}