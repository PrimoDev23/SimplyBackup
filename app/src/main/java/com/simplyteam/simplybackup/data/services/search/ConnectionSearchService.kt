package com.simplyteam.simplybackup.data.services.search

import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionSearchService @Inject constructor(
    private val _connectionRepository: ConnectionRepository
) : SearchServiceBase<Connection>() {

    override fun Search(value: String) {
        SearchText = value

        FilteredItems = _connectionRepository.Connections.filter {
            it.Name.contains(
                value,
                true
            )
        }
    }
}