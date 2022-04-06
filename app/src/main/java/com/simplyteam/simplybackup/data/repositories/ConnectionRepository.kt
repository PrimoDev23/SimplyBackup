package com.simplyteam.simplybackup.data.repositories

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.simplyteam.simplybackup.data.daos.ConnectionDao
import com.simplyteam.simplybackup.data.models.Connection
import com.simplyteam.simplybackup.data.databases.SimplyBackupDatabase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlin.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ConnectionRepository {
    private lateinit var _connectionDao: ConnectionDao

    val Connections = mutableStateOf<List<Connection>>(listOf())

    @OptIn(InternalCoroutinesApi::class)
    suspend fun Init(context: Context) {
        _connectionDao = SimplyBackupDatabase.getDatabase(context).connectionDao()

        _connectionDao.GetAllConnectionsFlow().collect {
            Connections.value = it
        }
    }

    suspend fun GetAllConnections(context: Context): List<Connection> {
        return SimplyBackupDatabase.getDatabase(context).connectionDao().GetAllConnections()
    }

    suspend fun InsertConnection(connection: Connection): Result<Long> {
        return try {
            Result.success(_connectionDao.InsertConnection(connection))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun RemoveConnection(connection: Connection): Result<Boolean> {
        return try {
            _connectionDao.DeleteConnection(connection)

            Result.success(true)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun UpdateConnection(connection: Connection): Result<Long> {
        return try {
            _connectionDao.UpdateConnection(connection)

            Result.success(connection.Id)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}