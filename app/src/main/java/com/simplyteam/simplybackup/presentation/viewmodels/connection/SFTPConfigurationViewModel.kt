package com.simplyteam.simplybackup.presentation.viewmodels.connection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.exceptions.FieldNotFilledException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SFTPConfigurationViewModel @Inject constructor(

) : ConfigurationViewModelBase() {

    var Name by mutableStateOf("")
    var NameErrorShown by mutableStateOf(false)

    var Host by mutableStateOf("")
    var HostErrorShown by mutableStateOf(false)

    var Username by mutableStateOf("")
    var UsernameErrorShown by mutableStateOf(false)

    var Password by mutableStateOf("")
    var PasswordErrorShown by mutableStateOf(false)

    var RemotePath by mutableStateOf("")
    var RemotePathErrorShown by mutableStateOf(false)

    override fun GetBaseConnection(): Connection {
        if(!ValuesValid()){
            throw FieldNotFilledException()
        }

        return Connection(
            ConnectionType = ConnectionType.SFTP,
            Name = Name,
            Host = Host,
            Username = Username,
            Password = Password,
            RemotePath = RemotePath
        )
    }

    private fun ValuesValid(): Boolean {
        NameErrorShown = Name.isEmpty()
        HostErrorShown = Host.isEmpty()
        UsernameErrorShown = Username.isEmpty()
        PasswordErrorShown = Password.isEmpty()
        RemotePathErrorShown = RemotePath.isEmpty()

        return !(NameErrorShown || HostErrorShown || UsernameErrorShown || PasswordErrorShown || RemotePathErrorShown)
    }

    override fun LoadData(connection: Connection) {
        Name = connection.Name
        Host = connection.Host
        Username = connection.Username
        Password = connection.Password
        RemotePath = connection.RemotePath
    }
}