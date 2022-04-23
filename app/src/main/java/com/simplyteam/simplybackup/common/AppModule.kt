package com.simplyteam.simplybackup.common

import android.app.Application
import androidx.room.Room
import com.simplyteam.simplybackup.data.databases.SimplyBackupDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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