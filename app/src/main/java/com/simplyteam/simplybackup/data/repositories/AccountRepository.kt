package com.simplyteam.simplybackup.data.repositories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.Delete
import com.simplyteam.simplybackup.data.daos.AccountDao
import com.simplyteam.simplybackup.data.models.Account
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor (
    private val _accountDao: AccountDao
) {

    var Accounts by mutableStateOf(listOf<Account>())

    fun GetFlow() = _accountDao.GetFlow()

    suspend fun Insert(account: Account) = _accountDao.InsertAccount(account)

    suspend fun Delete(account: Account) = _accountDao.DeleteAccount(account)
}