package com.simplyteam.simplybackup.common

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.simplyteam.simplybackup.data.databases.SimplyBackupDatabase
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.repositories.HistoryRepository
import com.simplyteam.simplybackup.data.services.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
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

    //endregion DB

    //region Services

    @Provides
    @Singleton
    fun GetNextCloudService(@ApplicationContext appContext: Context) = NextCloudService(appContext)

    @Provides
    @Singleton
    fun GetFtpService(@ApplicationContext appContext: Context) = SFTPService(appContext)

    @Provides
    @Singleton
    fun GetSchedulerService(@ApplicationContext appContext: Context) = SchedulerService(appContext)

    @Provides
    @Singleton
    fun GetPackagingService() = PackagingService()

    @Provides
    @Singleton
    fun GetNotificationService(@ApplicationContext appContext: Context) = NotificationService(appContext)

    //endregion Services

    //region Repos

    @Provides
    @Singleton
    fun GetConnectionRepository(simplyBackupDatabase: SimplyBackupDatabase) = ConnectionRepository(
        simplyBackupDatabase.connectionDao
    )

    @Provides
    @Singleton
    fun GetHistoryRepository(simplyBackupDatabase: SimplyBackupDatabase) = HistoryRepository(
        simplyBackupDatabase.historyDao
    )

    //endregion Repos

}