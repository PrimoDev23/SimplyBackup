package com.simplyteam.simplybackup.presentation.viewmodels.connection

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.simplyteam.simplybackup.data.models.Account
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.Event
import com.simplyteam.simplybackup.data.models.exceptions.FieldNotFilledException
import com.simplyteam.simplybackup.data.repositories.AccountRepository
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.cloudservices.GoogleDriveService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GoogleDriveConfigurationViewModel @Inject constructor(
    private val _accountRepository: AccountRepository,
    private val _googleDriveService: GoogleDriveService
) : ConfigurationViewModelBase() {

    var SelectionDialogShown by mutableStateOf(false)
        private set

    private val _newAccountFlow = MutableSharedFlow<Event.GoogleSignInEvent>()
    val NewAccountFlow = _newAccountFlow.asSharedFlow()

    var SelectedAccount by mutableStateOf("")
    var SelectedAccountError by mutableStateOf(false)
    var Name by mutableStateOf("")
    var NameError by mutableStateOf(false)
    var RemotePath by mutableStateOf("")

    fun ShowSelectionDialog() {
        SelectionDialogShown = true
    }

    fun HideSelectionDialog() {
        SelectionDialogShown = false
    }

    fun GetAccounts(): List<String> = _accountRepository.Accounts.filter {
        it.Type == ConnectionType.GoogleDrive
    }
        .map {
            it.Username
        }

    fun AddNewAccount() {
        viewModelScope.launch {
            val intent = _googleDriveService.GetSignInIntent()

            _newAccountFlow.emit(
                Event.GoogleSignInEvent(
                    intent
                )
            )
        }
    }

    fun SetAccountFromIntent(data: Intent) {
        viewModelScope.launch {
            try {
                val account = _googleDriveService.GetAccountFromIntent(data)

                account.email?.let { mail ->
                    _accountRepository.Insert(
                        Account(
                            ConnectionType.GoogleDrive,
                            mail
                        )
                    )
                    SelectedAccount = mail
                }
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }

    override fun GetBaseConnection(): Connection {
        if (!ValuesValid()) {
            throw FieldNotFilledException()
        }

        return Connection(
            ConnectionType = ConnectionType.GoogleDrive,
            Name = Name,
            Username = SelectedAccount,
            RemotePath = RemotePath
        )
    }

    private fun ValuesValid(): Boolean {
        NameError = Name.isEmpty()
        SelectedAccountError = SelectedAccount.isEmpty()

        return !(NameError || SelectedAccountError)
    }

    override fun LoadData(connection: Connection) {
        Name = connection.Name
        SelectedAccount = connection.Username
        RemotePath = connection.RemotePath
    }
}