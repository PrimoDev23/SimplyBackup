package com.simplyteam.simplybackup.common

import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.repositories.HistoryRepository
import com.simplyteam.simplybackup.data.repositories.QueueRepository
import com.simplyteam.simplybackup.data.services.NextCloudService
import com.simplyteam.simplybackup.data.services.NotificationService
import com.simplyteam.simplybackup.data.services.PackagingService
import com.simplyteam.simplybackup.data.services.SchedulerService
import com.simplyteam.simplybackup.presentation.navigation.ConnectionConfigurationNavigation
import com.simplyteam.simplybackup.presentation.navigation.MainNavigation
import com.simplyteam.simplybackup.presentation.views.IconProvider
import com.simplyteam.simplybackup.presentation.views.backuphistory.BackupHistoryView
import com.simplyteam.simplybackup.presentation.views.connection.ConnectionConfigurationView
import com.simplyteam.simplybackup.presentation.views.connection.PathsConfigurationView
import com.simplyteam.simplybackup.presentation.views.main.ConnectionOverviewView
import com.simplyteam.simplybackup.presentation.views.main.HomeView
import com.simplyteam.simplybackup.presentation.views.main.MainTabView
import com.simplyteam.simplybackup.presentation.views.main.SettingsView
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    //region Services

    @Provides
    @Singleton
    fun GetNextCloudService() = NextCloudService()

    @Provides
    @Singleton
    fun GetSchedulerService() = SchedulerService()

    @Provides
    @Singleton
    fun GetPackagingService() = PackagingService()

    @Provides
    @Singleton
    fun GetNotificationService() = NotificationService()

    //endregion Services

    //region Repos

    @Provides
    @Singleton
    fun GetConnectionRepository() = ConnectionRepository()

    @Provides
    @Singleton
    fun GetHistoryRepository() = HistoryRepository()

    @Provides
    @Singleton
    fun GetQueueRepository() = QueueRepository()

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
    fun GetMainNavigation(homeView: HomeView, connectionOverviewView: ConnectionOverviewView, settingsView: SettingsView)
            = MainNavigation(homeView, connectionOverviewView, settingsView)

    @Provides
    @Singleton
    fun GetMainTabView(mainNavigation: MainNavigation) = MainTabView(mainNavigation)

    //endregion MainViews

    //region ConnectionViews

    @Provides
    @Singleton
    fun GetConnectionConfigurationView() = ConnectionConfigurationView()

    @Provides
    @Singleton
    fun GetPathsConfigurationView() = PathsConfigurationView()

    @Provides
    @Singleton
    fun GetConnectionConfigurationNavigation(connectionConfigurationView: ConnectionConfigurationView, pathsConfigurationView: PathsConfigurationView)
            = ConnectionConfigurationNavigation(connectionConfigurationView, pathsConfigurationView)

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