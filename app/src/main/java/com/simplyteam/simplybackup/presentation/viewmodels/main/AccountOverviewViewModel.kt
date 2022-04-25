package com.simplyteam.simplybackup.presentation.viewmodels.main

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplyteam.simplybackup.R
import com.simplyteam.simplybackup.data.models.Account
import com.simplyteam.simplybackup.data.models.UIText
import com.simplyteam.simplybackup.data.models.events.UIEvent
import com.simplyteam.simplybackup.data.models.events.main.AccountOverviewEvent
import com.simplyteam.simplybackup.data.repositories.AccountRepository
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.services.search.AccountSearchService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AccountOverviewViewModel @Inject constructor(
    private val _connectionRepository: ConnectionRepository,
    private val _accountRepository: AccountRepository,
    private val _accountSearchService: AccountSearchService
) : ViewModel() {

    var ListState = LazyListState()
    private val _connectionExistsFlow = MutableSharedFlow<UIEvent.ShowSnackbar>()
    val ConnectionExistsFlow = _connectionExistsFlow.asSharedFlow()

    fun OnEvent(event: AccountOverviewEvent){
        when(event){
            is AccountOverviewEvent.OnDeleteAccount -> {
                DeleteAccount(event.Account)
            }
        }
    }

    val AccountFlow = _accountSearchService.FilteredItems
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val SearchTextFlow = _accountSearchService.SearchText
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    fun Search(value: String) {
        viewModelScope.launch {
            _accountSearchService.Search(value)
        }
    }

    fun ResetSearch() {
        viewModelScope.launch {
            _accountSearchService.Search("")
        }
    }

    private fun DeleteAccount(account: Account) {
        viewModelScope.launch {
            try {
                if (_connectionRepository.GetAllConnections().any {
                        it.ConnectionType == account.Type &&
                                it.Username == account.Username
                    }
                ) {
                    _connectionExistsFlow.emit(
                        UIEvent.ShowSnackbar(
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