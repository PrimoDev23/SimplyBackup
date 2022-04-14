package com.simplyteam.simplybackup.data.services.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

abstract class SearchServiceBase<T> {

    protected var SearchText by mutableStateOf("")

    protected var AllItems by mutableStateOf(listOf<T>())
    var FilteredItems by mutableStateOf(listOf<T>())

    fun GetSearchText() = SearchText

    abstract fun Search(value: String)

}