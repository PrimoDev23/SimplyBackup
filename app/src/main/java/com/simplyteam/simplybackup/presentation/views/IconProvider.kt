package com.simplyteam.simplybackup.presentation.views

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.ConnectionType

class IconProvider {

    @Composable
    fun BuildIconFromConnectionType(connectionType: ConnectionType){
        when(connectionType){
            ConnectionType.NextCloud -> BuildNextcloudIcon()
        }
    }

    @Composable
    private fun BuildNextcloudIcon() {
        Icon(
            modifier = Modifier
                .size(64.dp),
            painter = painterResource(
                id = R.drawable.logo_nextcloud_blue
            ),
            contentDescription = stringResource(
                id = R.string.NextCloud
            ),
            tint = Color(0, 130, 201)
        )
    }

}