package com.simplyteam.simplybackup.data.daos

import androidx.room.*
import com.simplyteam.simplybackup.data.models.Connection
import kotlinx.coroutines.flow.Flow

@Dao
interface ConnectionDao {

    @Query("SELECT * FROM Connections WHERE TemporarilyDeleted = 0")
    fun GetAllConnectionsFlow() : Flow<List<Connection>>

    @Query("SELECT * FROM Connections WHERE TemporarilyDeleted = 0")
    suspend fun GetAllConnections() : List<Connection>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun InsertConnection(connection: Connection) : Long

    @Update
    suspend fun UpdateConnection(connection: Connection)

    @Delete
    suspend fun DeleteConnection(connection: Connection) : Int
}