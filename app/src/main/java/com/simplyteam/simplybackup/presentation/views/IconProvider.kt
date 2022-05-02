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

@Composable
fun ConnectionIcon(connectionType: ConnectionType) {
    when (connectionType) {
        ConnectionType.NextCloud -> NextcloudIcon()
        ConnectionType.SFTP -> SftpIcon()
        ConnectionType.GoogleDrive -> GoogleDriveIcon()
        ConnectionType.SeaFile -> SeaFileIcon()
    }
}

@Composable
private fun NextcloudIcon() {
    Image(
        modifier = Modifier
            .size(64.dp),
        painter = painterResource(
            id = R.mipmap.logo_nextcloud_blue
        ),
        contentDescription = ConnectionType.NextCloud.toString()
    )
}

@Composable
private fun SftpIcon() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(
                id = R.drawable.ic_baseline_folder_24
            ),
            contentDescription = ConnectionType.SFTP.toString(),
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

@Composable
private fun GoogleDriveIcon() {
    Image(
        modifier = Modifier
            .size(40.dp),
        painter = painterResource(
            id = R.drawable.google_drive_logo
        ),
        contentDescription = ConnectionType.GoogleDrive.toString()
    )
}

@Composable
fun SeaFileIcon() {
    Image(
        modifier = Modifier
            .size(52.dp),
        painter = painterResource(
            id = R.drawable.seafile_logo
        ),
        contentDescription = ConnectionType.SeaFile.toString()
    )
}