package com.simplyteam.simplybackup.presentation.viewmodels.main

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.events.main.HistoryEvent
import com.simplyteam.simplybackup.data.services.search.HistorySearchService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val _historySearchService: HistorySearchService
) : ViewModel() {

    private val _openHistoryFlow = MutableSharedFlow<Connection>()
    val OpenHistoryFlow = _openHistoryFlow.asSharedFlow()

    val ListState = LazyListState()

    fun OnEvent(event: HistoryEvent){
        when(event){
            is HistoryEvent.OnOpenHistory -> {
                viewModelScope.launch {
                    _openHistoryFlow.emit(
                        event.Connection
                    )
                }
            }
            is HistoryEvent.Search -> {
                Search(event.Value)
            }
            HistoryEvent.ResetSearch -> {
                ResetSearch()
            }
        }
    }

    val HistoryDataFlow = _historySearchService.FilteredItems
    val SearchTextFlow = _historySearchService.GetSearchText()

    private fun Search(searchText: String) {
        viewModelScope.launch {
            _historySearchService.Search(searchText)
        }
    }

    private fun ResetSearch() {
        viewModelScope.launch {
            _historySearchService.Search("")
        }
    }
}