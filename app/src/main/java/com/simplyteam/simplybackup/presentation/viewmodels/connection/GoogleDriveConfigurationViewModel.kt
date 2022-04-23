package com.simplyteam.simplybackup.presentation.viewmodels.connection

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.simplyteam.simplybackup.data.models.Account
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.ConnectionType
import com.simplyteam.simplybackup.data.models.events.connection.GoogleDriveConfigurationEvent
import com.simplyteam.simplybackup.data.models.exceptions.FieldNotFilledException
import com.simplyteam.simplybackup.data.repositories.AccountRepository
import com.simplyteam.simplybackup.data.services.cloudservices.GoogleDriveService
import com.simplyteam.simplybackup.presentation.uistates.connection.GoogleDriveConfigurationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GoogleDriveConfigurationViewModel @Inject constructor(
    private val _accountRepository: AccountRepository,
    private val _googleDriveService: GoogleDriveService
) : ConfigurationViewModelBase() {
    private val _newAccountFlow = MutableSharedFlow<Intent>()
    val NewAccountFlow = _newAccountFlow.asSharedFlow()

    var State by mutableStateOf(GoogleDriveConfigurationState())

    fun OnEvent(event: GoogleDriveConfigurationEvent) {
        when (event) {
            is GoogleDriveConfigurationEvent.OnFolderLinkChange -> {
                State = State.copy(
                    FolderLink = event.Value
                )
            }
            is GoogleDriveConfigurationEvent.OnNameChange -> {
                State = State.copy(
                    Name = event.Value
                )
            }
            is GoogleDriveConfigurationEvent.OnSelectedAccountChange -> {
                State = State.copy(
                    SelectedAccount = event.Value
                )
            }
            GoogleDriveConfigurationEvent.OnDialogDismissed -> {
                State = State.copy(
                    SelectionDialogShown = false
                )
            }
            GoogleDriveConfigurationEvent.OnLoginCardClicked -> {
                State = State.copy(
                    SelectionDialogShown = true
                )
            }
            is GoogleDriveConfigurationEvent.OnRequestSignIn -> {
                viewModelScope.launch {
                    val intent = _googleDriveService.GetSignInIntent()

                    _newAccountFlow.emit(intent)
                }
            }
        }
    }

    fun GetAccounts(): List<String> = _accountRepository.Accounts.filter {
        it.Type == ConnectionType.GoogleDrive
    }
        .map {
            it.Username
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
                    OnEvent(
                        GoogleDriveConfigurationEvent.OnSelectedAccountChange(
                            mail
                        )
                    )
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
            Name = State.Name,
            Username = State.SelectedAccount,
            RemotePath = State.FolderLink
        )
    }

    private fun ValuesValid(): Boolean {
        State = State.copy(
            NameError = State.Name.isEmpty(),
            SelectedAccountError = State.SelectedAccount.isEmpty()
        )

        return !(State.NameError || State.SelectedAccountError)
    }

    override fun LoadData(connection: Connection) {
        State = State.copy(
            Name = connection.Name,
            SelectedAccount = connection.Username,
            FolderLink = connection.RemotePath
        )
    }
}