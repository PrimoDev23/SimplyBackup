package com.simplyteam.simplybackup.data.services.search

import com.simplyteam.simplybackup.data.models.Account
import com.simplyteam.simplybackup.data.repositories.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountSearchService @Inject constructor(
    _accountRepository: AccountRepository
) : SearchServiceBase<Account>() {
    override var FilteredItems: Flow<List<Account>> = _accountRepository.GetFlow().combine(SearchText) { accounts, search ->
        accounts.filter {
            it.Username.contains(
                search,
                true
            )
        }
    }
}