package com.simplyteam.simplybackup.data.services.search

import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.models.HistoryData
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.repositories.HistoryRepository

class HistorySearchService constructor(
    private val _historyRepository: HistoryRepository
) : SearchServiceBase<HistoryData>() {
    override fun Search(value: String) {
        SearchText = value

        FilteredItems = _historyRepository.HistoryData.filter {
            it.Connection.Name.contains(
                value,
                true
            )
        }
    }
}