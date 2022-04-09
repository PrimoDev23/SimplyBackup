package com.simplyteam.simplybackup.presentation.viewmodels.connection

import androidx.compose.runtime.mutableStateOf
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.exceptions.FieldNotFilledException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SFTPConfigurationViewModel @Inject constructor(

) : ConfigurationViewModelBase() {

    val Name = mutableStateOf("")
    val URL = mutableStateOf("")
    val Username = mutableStateOf("")
    val Password = mutableStateOf("")

    override fun GetBaseConnection(): Connection {
        when {
            Name.value.isEmpty() -> {
                throw FieldNotFilledException("Name")
            }
            URL.value.isEmpty() -> {
                throw FieldNotFilledException("URL")
            }
            Username.value.isEmpty() -> {
                throw FieldNotFilledException("Username")
            }
            Password.value.isEmpty() -> {
                throw FieldNotFilledException("Password")
            }
        }

        return Connection(
            ConnectionType = ConnectionType.SFTP,
            Name = Name.value,
            URL = URL.value,
            Username = Username.value,
            Password = Password.value
        )
    }

    override fun LoadData(connection: Connection) {
        Name.value = connection.Name
        URL.value = connection.URL
        Username.value = connection.Username
        Password.value = connection.Password
    }
}