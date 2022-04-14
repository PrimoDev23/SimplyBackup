package com.simplyteam.simplybackup.data.services.search

import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import kotlinx.coroutines.flow.map

class ConnectionSearchService constructor(
    private val _connectionRepository: ConnectionRepository
) : SearchServiceBase<Connection>() {

    suspend fun Collect() {
        _connectionRepository.ConnectionsFlow.collect {
            AllItems = it

            Search(SearchText)
        }
    }

    override fun Search(value: String) {
        SearchText = value

        FilteredItems = AllItems.filter {
            it.Name.contains(
                value,
                true
            )
        }
    }
}