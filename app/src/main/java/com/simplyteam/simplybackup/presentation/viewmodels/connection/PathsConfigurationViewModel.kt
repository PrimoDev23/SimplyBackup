package com.simplyteam.simplybackup.presentation.viewmodels.connection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.simplyteam.simplybackup.data.models.Path
import com.simplyteam.simplybackup.data.models.PathType
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

@HiltViewModel
class PathsConfigurationViewModel @Inject constructor(

): ViewModel() {

    var CurrentPath by mutableStateOf("")
    var CurrentPathError by mutableStateOf(false)

    var Paths by mutableStateOf(listOf<Path>())

    private fun CreatePathObjectFromStringPath(path: String): Path {
        val file = File(path)

        if (!file.exists()) {
            throw FileNotFoundException(path)
        }

        return if (file.isDirectory) {
            Path(
                Path = file.absolutePath,
                Type = PathType.DIRECTORY
            )
        } else {
            Path(
                Path = file.absolutePath,
                Type = PathType.FILE
            )
        }
    }

    fun AddPath(stringPath: String) {
        try {
            val path = CreatePathObjectFromStringPath(stringPath)

            val list = Paths.toMutableList()

            list.add(path)

            Paths = list
            CurrentPath = ""
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    fun RemovePath(path: Path) {
        val list = Paths.toMutableList()

        list.remove(path)

        Paths = list
    }

}