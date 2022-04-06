package com.simplyteam.simplybackup.data.daos

import androidx.room.*
import com.simplyteam.simplybackup.data.models.Connection
import kotlinx.coroutines.flow.Flow

@Dao
interface ConnectionDao {

    @Query("SELECT * FROM Connections")
    fun GetAllConnectionsFlow() : Flow<List<Connection>>

    @Query("SELECT * FROM Connections")
    suspend fun GetAllConnections() : List<Connection>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun InsertConnection(connection: Connection) : Long

    @Update
    suspend fun UpdateConnection(connection: Connection) : Int

    @Delete
    suspend fun DeleteConnection(connection: Connection)
}