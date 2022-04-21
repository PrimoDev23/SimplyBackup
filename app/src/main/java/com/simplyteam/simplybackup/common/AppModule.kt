package com.simplyteam.simplybackup.common

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.simplyteam.simplybackup.data.databases.SimplyBackupDatabase
import com.simplyteam.simplybackup.data.repositories.AccountRepository
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.repositories.HistoryRepository
import com.simplyteam.simplybackup.data.services.*
import com.simplyteam.simplybackup.data.services.cloudservices.GoogleDriveService
import com.simplyteam.simplybackup.data.services.cloudservices.NextCloudService
import com.simplyteam.simplybackup.data.services.cloudservices.SFTPService
import com.simplyteam.simplybackup.data.services.search.AccountSearchService
import com.simplyteam.simplybackup.data.services.search.ConnectionSearchService
import com.simplyteam.simplybackup.data.services.search.HistorySearchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    //region DB

    @Provides
    @Singleton
    fun GetDatabase(app: Application): SimplyBackupDatabase {
        return Room.databaseBuilder(
            app,
            SimplyBackupDatabase::class.java,
            SimplyBackupDatabase.DATABASE_NAME
        )
            .build()
    }

    @Provides
    @Singleton
    fun GetConnectionDao(simplyBackupDatabase: SimplyBackupDatabase) = simplyBackupDatabase.connectionDao

    @Provides
    @Singleton
    fun GetHistoryDao(simplyBackupDatabase: SimplyBackupDatabase) = simplyBackupDatabase.historyDao

    @Provides
    @Singleton
    fun GetAccountDao(simplyBackupDatabase: SimplyBackupDatabase) = simplyBackupDatabase.accountDao

    //endregion DB

}