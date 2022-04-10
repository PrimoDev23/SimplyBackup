package com.simplyteam.simplybackup.presentation.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.simplyteam.simplybackup.data.repositories.ConnectionRepository
import com.simplyteam.simplybackup.data.repositories.HistoryRepository
import com.simplyteam.simplybackup.data.services.NotificationService
import com.simplyteam.simplybackup.data.services.PackagingService
import com.simplyteam.simplybackup.presentation.views.main.MainTabView
import com.simplyteam.simplybackup.presentation.theme.SimplyBackupTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var MainTabView: MainTabView

    @Inject
    lateinit var ConnectionRepository: ConnectionRepository

    @Inject
    lateinit var HistoryRepository: HistoryRepository

    @Inject
    lateinit var PackagingService: PackagingService

    @Inject
    lateinit var NotificationService: NotificationService

    private lateinit var _storageAccessIntent: Intent
    private val _storageAccessLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
            RequestFullStorageAccess()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1000
            )
        }

        if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
            _storageAccessIntent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)

            RequestFullStorageAccess()
        }

        NotificationService.CreateNotificationChannel()

        InitRepositories()

        setContent {
            SimplyBackupTheme {
                MainTabView.Build()
            }
        }
    }

    private fun RequestFullStorageAccess() {
        _storageAccessLauncher.launch(_storageAccessIntent)
    }

    private fun InitRepositories() {
        lifecycleScope.launchWhenStarted {
            ConnectionRepository.Init()
        }

        lifecycleScope.launchWhenStarted {
            HistoryRepository.Init()
        }
    }
}