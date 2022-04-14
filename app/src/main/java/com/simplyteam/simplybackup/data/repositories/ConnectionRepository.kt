package com.simplyteam.simplybackup.data.repositories

import com.simplyteam.simplybackup.data.daos.ConnectionDao
import com.simplyteam.simplybackup.data.models.Connection

class ConnectionRepository(
    private val _connectionDao: ConnectionDao
) {
    val ConnectionsFlow = _connectionDao.GetAllConnectionsFlow()

    suspend fun GetAllConnections(): List<Connection> {
        return _connectionDao.GetAllConnections()
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