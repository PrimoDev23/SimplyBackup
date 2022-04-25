package com.simplyteam.simplybackup.data.services.search

import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionSearchService @Inject constructor(
    _connectionRepository: ConnectionRepository
) : SearchServiceBase<Connection>() {
    override var FilteredItems: Flow<List<Connection>> = _connectionRepository.GetFlow().combine(SearchText) { connections, search ->
        connections.filter {
            it.Name.contains(
                search,
                true
            )
        }
    }
}