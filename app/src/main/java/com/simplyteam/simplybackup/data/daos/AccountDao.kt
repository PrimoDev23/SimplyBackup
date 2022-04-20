package com.simplyteam.simplybackup.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.simplyteam.simplybackup.data.models.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM Accounts")
    fun GetFlow() : Flow<List<Account>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun InsertAccount(account: Account): Long

}