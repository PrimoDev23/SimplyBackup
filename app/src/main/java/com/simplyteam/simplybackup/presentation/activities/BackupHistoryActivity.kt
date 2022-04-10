package com.simplyteam.simplybackup.presentation.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.presentation.activities.ui.theme.SimplyBackupTheme
import com.simplyteam.simplybackup.presentation.viewmodels.backuphistory.BackupHistoryViewModel
import com.simplyteam.simplybackup.presentation.views.backuphistory.BackupHistoryView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BackupHistoryActivity : ComponentActivity() {

    @Inject
    lateinit var BackupHistoryView: BackupHistoryView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val connection = this.intent.extras?.get("Connection") as Connection

        setContent {
            SimplyBackupTheme {
                val viewModel: BackupHistoryViewModel = hiltViewModel()

                LaunchedEffect(key1 = true){
                    viewModel.InitValues(connection)
                }

                Scaffold(
                    topBar = {
                        BuildTopBar()
                    }
                ) {
                    BackupHistoryView.Build(
                        paddingValues = it,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    @Composable
    private fun BuildTopBar() {
        val activity = LocalContext.current as ComponentActivity

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
            elevation = 4.dp
        )
    }


}