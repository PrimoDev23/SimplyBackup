package com.simplyteam.simplybackup.data.services.search

import com.simplyteam.simplybackup.data.models.Account
import com.simplyteam.simplybackup.data.repositories.AccountRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountSearchService @Inject constructor(
    private val _accountRepository: AccountRepository
) : SearchServiceBase<Account>() {
    override fun Search(value: String) {
        SearchText = value

        FilteredItems = _accountRepository.Accounts.filter {
            it.Username.contains(
                value,
                true
            )
        }
    }
}