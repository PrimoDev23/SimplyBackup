package com.simplyteam.simplybackup.presentation.viewmodels.connection

import androidx.lifecycle.ViewModel
import com.simplyteam.simplybackup.data.models.Connection

abstract class ConfigurationViewModelBase : ViewModel() {

    abstract fun GetBaseConnection() : Connection

    abstract fun LoadData(connection: Connection)

}