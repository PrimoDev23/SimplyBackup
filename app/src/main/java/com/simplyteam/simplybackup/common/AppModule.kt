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

    //endregion DB

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

    @Provides
    @Singleton
    fun GetAccountRepository(simplyBackupDatabase: SimplyBackupDatabase) = AccountRepository(
        simplyBackupDatabase.accountDao
    )

    //endregion Repos

    //region Services

    @Provides
    @Singleton
    fun GetConnectionSearchService(connectionRepository: ConnectionRepository) = ConnectionSearchService(connectionRepository)

    @Provides
    @Singleton
    fun GetHistorySearchService(historyRepository: HistoryRepository) = HistorySearchService(historyRepository)

    @Provides
    @Singleton
    fun GetNextCloudService(@ApplicationContext appContext: Context) = NextCloudService(appContext)

    @Provides
    @Singleton
    fun GetFtpService(@ApplicationContext appContext: Context) = SFTPService(appContext)

    @Provides
    @Singleton
    fun GetGoogleDriveService(@ApplicationContext appContext: Context) = GoogleDriveService(appContext)

    @Provides
    @Singleton
    fun GetSchedulerService(@ApplicationContext appContext: Context) = SchedulerService(appContext)

    @Provides
    @Singleton
    fun GetPackagingService() = PackagingService()

    @Provides
    @Singleton
    fun GetNotificationService(@ApplicationContext appContext: Context) =
        NotificationService(appContext)

    //endregion Services

}