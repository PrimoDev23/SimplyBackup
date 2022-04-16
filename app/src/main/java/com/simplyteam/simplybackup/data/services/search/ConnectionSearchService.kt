package com.simplyteam.simplybackup.data.services.search

import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository

class ConnectionSearchService constructor(
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