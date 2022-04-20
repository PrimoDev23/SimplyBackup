package com.simplyteam.simplybackup.data.repositories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.simplyteam.simplybackup.data.daos.AccountDao
import com.simplyteam.simplybackup.data.models.Account
import kotlinx.coroutines.flow.collect

class AccountRepository constructor (
    private val _accountDao: AccountDao
) {

    var Accounts by mutableStateOf(listOf<Account>())

    suspend fun Collect() {
        _accountDao.GetFlow().collect {
            Accounts = it
        }
    }

    suspend fun Insert(account: Account) = _accountDao.InsertAccount(account)

}