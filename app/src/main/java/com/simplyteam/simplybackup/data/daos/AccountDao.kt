package com.simplyteam.simplybackup.data.daos

import androidx.room.*
import com.simplyteam.simplybackup.data.models.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM Accounts")
    fun GetFlow() : Flow<List<Account>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun InsertAccount(account: Account): Long

    @Delete
    suspend fun DeleteAccount(account: Account): Int

}