package com.simplyteam.simplybackup.presentation.viewmodels.connection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.events.connection.NextCloudConfigurationEvent
import com.simplyteam.simplybackup.data.models.exceptions.FieldNotFilledException
import com.simplyteam.simplybackup.presentation.uistates.connection.NextCloudConfigurationState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NextCloudConfigurationViewModel @Inject constructor(

) : ConfigurationViewModelBase() {

    var State by mutableStateOf(NextCloudConfigurationState())

    fun OnEvent(event: NextCloudConfigurationEvent){
        when(event){
            is NextCloudConfigurationEvent.OnHostChange -> {
                State = State.copy(
                    Host = event.Value
                )
            }
            is NextCloudConfigurationEvent.OnNameChange -> {
                State = State.copy(
                    Name = event.Value
                )
            }
            is NextCloudConfigurationEvent.OnPasswordChange -> {
                State = State.copy(
                    Password = event.Value
                )
            }
            is NextCloudConfigurationEvent.OnRemotePathChange -> {
                State = State.copy(
                    RemotePath = event.Value
                )
            }
            is NextCloudConfigurationEvent.OnUsernameChange -> {
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
            ConnectionType = ConnectionType.NextCloud,
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
            RemotePathError = GetRemotePathError()
        )

        return !(State.NameError || State.HostError || State.UsernameError || State.PasswordError || State.RemotePathError != R.string.PlaceholderValue)
    }

    private fun GetRemotePathError(): Int{
        if(State.RemotePath.isEmpty() || State.RemotePath == "/"){
            return R.string.PlaceholderValue
        }else if(!State.RemotePath.startsWith("/")){
            return R.string.StartRemotePathWithSlash
        }else if(State.RemotePath.endsWith("/")){
            return R.string.RemotePathEndsWithSlash
        }else{
            return R.string.PlaceholderValue
        }
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