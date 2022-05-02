package com.simplyteam.simplybackup.presentation.viewmodels.connection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.events.connection.NextCloudConfigurationEvent
import com.simplyteam.simplybackup.data.models.events.connection.SeaFileConfigurationEvent
import com.simplyteam.simplybackup.data.models.exceptions.FieldNotFilledException
import com.simplyteam.simplybackup.presentation.uistates.connection.NextCloudConfigurationState
import com.simplyteam.simplybackup.presentation.uistates.connection.SeaFileConfigurationState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SeaFileConfigurationViewModel @Inject constructor(

) : ConfigurationViewModelBase() {

    var State by mutableStateOf(SeaFileConfigurationState())

    fun OnEvent(event: SeaFileConfigurationEvent){
        when(event){
            is SeaFileConfigurationEvent.OnHostChange -> {
                State = State.copy(
                    Host = event.Value
                )
            }
            is SeaFileConfigurationEvent.OnNameChange -> {
                State = State.copy(
                    Name = event.Value
                )
            }
            is SeaFileConfigurationEvent.OnPasswordChange -> {
                State = State.copy(
                    Password = event.Value
                )
            }
            is SeaFileConfigurationEvent.OnRemotePathChange -> {
                State = State.copy(
                    RemotePath = event.Value
                )
            }
            is SeaFileConfigurationEvent.OnUsernameChange -> {
                State = State.copy(
                    Username = event.Value
                )
            }
            is SeaFileConfigurationEvent.OnRepoIdChange -> {
                State = State.copy(
                    RepoId = event.Value
                )
            }
        }
    }

    override fun GetBaseConnection(): Connection {
        if (!ValuesValid()) {
            throw FieldNotFilledException()
        }

        return Connection(
            ConnectionType = ConnectionType.SeaFile,
            Name = State.Name,
            Host = State.Host,
            Username = State.Username,
            Password = State.Password,
            RepoId = State.RepoId,
            RemotePath = State.RemotePath
        )
    }

    private fun ValuesValid(): Boolean {
        State = State.copy(
            NameError = State.Name.isEmpty(),
            HostError = State.Host.isEmpty(),
            UsernameError = State.Username.isEmpty(),
            PasswordError = State.Password.isEmpty(),
            RepoIdError = State.RepoId.isEmpty()
        )

        return !(State.NameError || State.HostError || State.UsernameError || State.PasswordError || State.RepoIdError)
    }

    override fun LoadData(connection: Connection) {
        State = State.copy(
            Name = connection.Name,
            Host = connection.Host,
            Username = connection.Username,
            Password = connection.Password,
            RepoId = connection.RepoId,
            RemotePath = connection.RemotePath
        )
    }
}