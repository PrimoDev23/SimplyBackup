package com.simplyteam.simplybackup.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.ConnectionType

class IconProvider {

    @Composable
    fun BuildIconFromConnectionType(connectionType: ConnectionType) {
        when (connectionType) {
            ConnectionType.NextCloud -> BuildNextcloudIcon()
            ConnectionType.SFTP -> BuildFtpIcon()
        }
    }

    @Composable
    private fun BuildNextcloudIcon() {
        Image(
            modifier = Modifier
                .size(64.dp),
            painter = painterResource(
                id = R.drawable.logo_nextcloud_blue
            ),
            contentDescription = stringResource(
                id = R.string.NextCloud
            )
        )
    }

    @Composable
    private fun BuildFtpIcon() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_baseline_folder_24),
                contentDescription = stringResource(
                    id = R.string.SFTP
                ),
                tint = MaterialTheme.colors.onBackground
            )

            Text(
                text = stringResource(
                    id = R.string.SFTP
                ),
                color = MaterialTheme.colors.onBackground
            )
        }
    }

}