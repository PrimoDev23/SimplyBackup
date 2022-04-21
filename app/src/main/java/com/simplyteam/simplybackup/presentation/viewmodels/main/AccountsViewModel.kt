package com.simplyteam.simplybackup.presentation.viewmodels.main

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Account
import com.simplyteam.simplybackup.data.models.Event
import com.simplyteam.simplybackup.data.models.UIText
import com.simplyteam.simplybackup.data.repositories.AccountRepository
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.search.AccountSearchService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val _connectionRepository: ConnectionRepository,
    private val _accountRepository: AccountRepository,
    private val _accountSearchService: AccountSearchService
) : ViewModel() {

    var ListState = LazyListState()
    private val _connectionExistsFlow = MutableSharedFlow<Event.SimpleTextEvent>()
    val ConnectionExistsFlow = _connectionExistsFlow.asSharedFlow()

    val Accounts = _accountRepository.Accounts

    fun GetSearchText() = _accountSearchService.GetSearchText()

    fun Search(value: String) {
        _accountSearchService.Search(value)
    }

    fun ResetSearch() {
        _accountSearchService.Search("")
    }

    fun GetAccounts() = _accountRepository.Accounts

    fun DeleteAccount(account: Account) {
        viewModelScope.launch {
            try {
                if (_connectionRepository.Connections.any {
                        it.ConnectionType == account.Type &&
                                it.Username == account.Username
                    }
                ) {
                    _connectionExistsFlow.emit(
                        Event.SimpleTextEvent(
                            UIText.StringResource(
                                R.string.ConnectionStillExists
                            )
                        )
                    )
                } else {
                    if (_accountRepository.Delete(account) < 0) {
                        throw Exception("Deleting account failed")
                    }
                }
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }

}