package com.simplyteam.simplybackup.common

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.simplyteam.simplybackup.data.databases.SimplyBackupDatabase
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.repositories.HistoryRepository
import com.simplyteam.simplybackup.data.services.*
import com.simplyteam.simplybackup.presentation.navigation.ConnectionConfigurationNavigation
import com.simplyteam.simplybackup.presentation.navigation.MainNavigation
import com.simplyteam.simplybackup.presentation.views.IconProvider
import com.simplyteam.simplybackup.presentation.views.backuphistory.BackupHistoryView
import com.simplyteam.simplybackup.presentation.views.connection.ConnectionConfigurationView
import com.simplyteam.simplybackup.presentation.views.connection.SFTPConfigurationView
import com.simplyteam.simplybackup.presentation.views.connection.NextCloudConfigurationView
import com.simplyteam.simplybackup.presentation.views.connection.PathsConfigurationView
import com.simplyteam.simplybackup.presentation.views.main.ConnectionOverviewView
import com.simplyteam.simplybackup.presentation.views.main.HomeView
import com.simplyteam.simplybackup.presentation.views.main.MainTabView
import com.simplyteam.simplybackup.presentation.views.main.SettingsView
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TestAppModule {

    //region DB

    @Provides
    @Singleton
    fun GetDatabase(app: Application): SimplyBackupDatabase {
        return Room.inMemoryDatabaseBuilder(
            app,
            SimplyBackupDatabase::class.java
        )
            .setQueryExecutor(Executors.newSingleThreadExecutor())
            .setTransactionExecutor(Executors.newSingleThreadExecutor())
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

    //region MainViews

    @Provides
    @Singleton
    fun GetHomeView() = HomeView()

    @Provides
    @Singleton
    fun GetCloudOverviewView() = ConnectionOverviewView()

    @Provides
    @Singleton
    fun GetSettingsView() = SettingsView()

    @Provides
    @Singleton
    fun GetMainNavigation(
        homeView: HomeView,
        connectionOverviewView: ConnectionOverviewView,
        settingsView: SettingsView
    ) = MainNavigation(
        homeView,
        connectionOverviewView,
        settingsView
    )

    @Provides
    @Singleton
    fun GetMainTabView(mainNavigation: MainNavigation) = MainTabView(mainNavigation)

    //endregion MainViews

    //region ConnectionViews

    @Provides
    @Singleton
    fun GetNextCloudConfigurationView() = NextCloudConfigurationView()

    @Provides
    @Singleton
    fun GetFtpConfigurationView() = SFTPConfigurationView()

    @Provides
    @Singleton
    fun GetConnectionConfigurationView(
        nextCloudConfigurationView: NextCloudConfigurationView,
        SFTPConfigurationView: SFTPConfigurationView
    ) = ConnectionConfigurationView(
        nextCloudConfigurationView,
        SFTPConfigurationView
    )

    @Provides
    @Singleton
    fun GetPathsConfigurationView() = PathsConfigurationView()

    @Provides
    @Singleton
    fun GetConnectionConfigurationNavigation(
        connectionConfigurationView: ConnectionConfigurationView,
        pathsConfigurationView: PathsConfigurationView
    ) = ConnectionConfigurationNavigation(
        connectionConfigurationView,
        pathsConfigurationView
    )

    //endregion ConnectionViews

    //region BackupHistoryViews

    @Provides
    @Singleton
    fun GetBackupHistoryView() = BackupHistoryView()

    //endregion BackupHistoryViews

    @Provides
    @Inject
    fun GetIconProvider() = IconProvider()

}