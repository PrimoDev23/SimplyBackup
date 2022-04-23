package com.simplyteam.simplybackup.presentation.viewmodels.connection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.events.connection.SFTPConfigurationEvent
import com.simplyteam.simplybackup.data.models.exceptions.FieldNotFilledException
import com.simplyteam.simplybackup.presentation.uistates.connection.SFTPConfigurationState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SFTPConfigurationViewModel @Inject constructor(

) : ConfigurationViewModelBase() {

    var State by mutableStateOf(SFTPConfigurationState())

    fun OnEvent(event: SFTPConfigurationEvent) {
        when (event) {
            is SFTPConfigurationEvent.OnHostChange -> {
                State = State.copy(
                    Host = event.Value
                )
            }
            is SFTPConfigurationEvent.OnNameChange -> {
                State = State.copy(
                    Name = event.Value
                )
            }
            is SFTPConfigurationEvent.OnPasswordChange -> {
                State = State.copy(
                    Password = event.Value
                )
            }
            is SFTPConfigurationEvent.OnRemotePathChange -> {
                State = State.copy(
                    RemotePath = event.Value
                )
            }
            is SFTPConfigurationEvent.OnUsernameChange -> {
                State = State.copy(
                    Username = event.Value
                )
            }
        }
    }

    override fun GetBaseConnection(): Connection {
        if (!ValuesValid()) {
            throw FieldNotFilledException()
        }

        return Connection(
            ConnectionType = ConnectionType.SFTP,
            Name = State.Name,
            Host = State.Host,
            Username = State.Username,
            Password = State.Password,
            RemotePath = State.RemotePath
        )
    }

    private fun ValuesValid(): Boolean {
        State = State.copy(
            NameError = State.Name.isEmpty(),
            HostError = State.Host.isEmpty(),
            UsernameError = State.Username.isEmpty(),
            PasswordError = State.Password.isEmpty(),
            RemotePathError = State.RemotePath.isEmpty(),
        )
        return !(State.NameError || State.HostError || State.UsernameError || State.PasswordError || State.RemotePathError)
    }

    override fun LoadData(connection: Connection) {
        State = State.copy(
            Name = connection.Name,
            Host = connection.Host,
            Username = connection.Username,
            Password = connection.Password,
            RemotePath = connection.RemotePath
        )
    }
}