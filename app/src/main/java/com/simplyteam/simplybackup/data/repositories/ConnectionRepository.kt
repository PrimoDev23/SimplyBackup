package com.simplyteam.simplybackup.data.repositories

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.simplyteam.simplybackup.data.daos.ConnectionDao
import com.simplyteam.simplybackup.data.databases.SimplyBackupDatabase
import com.simplyteam.simplybackup.data.models.Connection
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect

class ConnectionRepository {
    private lateinit var _connectionDao: ConnectionDao

    val Connections = mutableStateOf<List<Connection>>(listOf())

    @OptIn(InternalCoroutinesApi::class)
    suspend fun Init(context: Context) {
        _connectionDao = SimplyBackupDatabase.getDatabase(context)
            .connectionDao()

        _connectionDao.GetAllConnectionsFlow()
            .collect {
                Connections.value = it
            }
    }

    suspend fun GetAllConnections(context: Context): List<Connection> {
        return SimplyBackupDatabase.getDatabase(context)
            .connectionDao()
            .GetAllConnections()
    }

    suspend fun InsertConnection(connection: Connection): Long {
        return _connectionDao.InsertConnection(connection)
    }

    suspend fun RemoveConnection(connection: Connection): Int {
        return _connectionDao.DeleteConnection(connection)
    }

    suspend fun UpdateConnection(connection: Connection): Int {
        _connectionDao.UpdateConnection(connection)

        return 1
    }
}