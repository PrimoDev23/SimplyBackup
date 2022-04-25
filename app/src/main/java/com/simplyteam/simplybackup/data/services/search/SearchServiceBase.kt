package com.simplyteam.simplybackup.data.services.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

abstract class SearchServiceBase<T> {

    protected val SearchText = MutableStateFlow("")

    abstract var FilteredItems: Flow<List<T>>

    fun GetSearchText() = SearchText

    suspend fun Search(value: String){
        SearchText.emit(value)
    }
}