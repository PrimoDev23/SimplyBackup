package com.simplyteam.simplybackup.presentation.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.utils.ActivityUtil.FinishActivityWithAnimation
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
                val viewModel: BackupHistoryViewModel = viewModel()
                val scaffoldState = rememberScaffoldState()

                val context = LocalContext.current

                LaunchedEffect(key1 = true) {
                    viewModel.InitValues(connection)
                }

                LaunchedEffect(key1 = true) {
                    viewModel.RestoreFinishedFlow.collect {
                        scaffoldState.snackbarHostState.showSnackbar(
                            it.text.asString(context)
                        )
                    }
                }

                Scaffold(
                    scaffoldState = scaffoldState,
                    topBar = {
                        BuildTopBar(
                            viewModel = viewModel
                        )
                    },
                    snackbarHost = {
                        SnackbarHost(
                            modifier = Modifier
                                .testTag("RestoreSnackbar"),
                            hostState = scaffoldState.snackbarHostState,
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
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    @Composable
    private fun BuildTopBar(viewModel: BackupHistoryViewModel) {
        val activity = LocalContext.current as ComponentActivity

        val elevation by animateDpAsState(
            if (viewModel.ListState.firstVisibleItemIndex == 0) {
                minOf(
                    viewModel.ListState.firstVisibleItemScrollOffset.toFloat().dp,
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
                        activity.FinishActivityWithAnimation()
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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_right
        )
    }
}