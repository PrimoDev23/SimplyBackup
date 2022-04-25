package com.simplyteam.simplybackup.data.services.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class SearchServiceBase<T> {

    private val _searchText = MutableStateFlow("")
    val SearchText get() = _searchText.asStateFlow()

    abstract var FilteredItems: Flow<List<T>>

    suspend fun Search(value: String){
        _searchText.emit(value)
    }
}