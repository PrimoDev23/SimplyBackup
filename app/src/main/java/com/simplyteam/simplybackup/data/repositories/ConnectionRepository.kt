package com.simplyteam.simplybackup.data.repositories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.simplyteam.simplybackup.data.daos.ConnectionDao
import com.simplyteam.simplybackup.data.models.Connection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionRepository @Inject constructor(
    private val _connectionDao: ConnectionDao
) {
    val Flow get() = _connectionDao.GetAllConnectionsFlow()

    suspend fun GetAllConnections(): List<Connection> {
        return _connectionDao.GetAllConnections()
    }

    suspend fun GetConnection(id: Long): Connection {
        return _connectionDao.GetConnection(id)
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