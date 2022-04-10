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
class NextCloudConfigurationViewModel @Inject constructor(

) : ConfigurationViewModelBase() {

    var Name by mutableStateOf("")
    var URL by mutableStateOf("")
    var Username by mutableStateOf("")
    var Password by mutableStateOf("")
    var RemotePath by mutableStateOf("")

    override fun GetBaseConnection(): Connection {
        when {
            Name.isEmpty() -> {
                throw FieldNotFilledException("Name")
            }
            URL.isEmpty() -> {
                throw FieldNotFilledException("URL")
            }
            Username.isEmpty() -> {
                throw FieldNotFilledException("Username")
            }
            Password.isEmpty() -> {
                throw FieldNotFilledException("Password")
            }
        }

        return Connection(
            ConnectionType = ConnectionType.NextCloud,
            Name = Name,
            URL = URL,
            Username = Username,
            Password = Password,
            RemotePath = RemotePath
        )
    }

    override fun LoadData(connection: Connection) {
        Name = connection.Name
        URL = connection.URL
        Username = connection.Username
        Password = connection.Password
        RemotePath = connection.RemotePath
    }
}