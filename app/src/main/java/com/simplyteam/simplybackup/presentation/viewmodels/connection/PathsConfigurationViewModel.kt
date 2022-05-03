package com.simplyteam.simplybackup.presentation.viewmodels.connection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.simplyteam.simplybackup.data.models.Path
import com.simplyteam.simplybackup.data.models.PathType
import com.simplyteam.simplybackup.data.models.events.connection.PathsConfigurationEvent
import com.simplyteam.simplybackup.data.models.exceptions.FieldNotFilledException
import com.simplyteam.simplybackup.presentation.uistates.connection.PathsConfigurationState
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

@HiltViewModel
class PathsConfigurationViewModel @Inject constructor(

) : ViewModel() {

    var State by mutableStateOf(PathsConfigurationState())

    fun OnEvent(event: PathsConfigurationEvent) {
        when (event) {
            is PathsConfigurationEvent.OnCurrentPathChange -> {
                State = State.copy(
                    CurrentPath = event.Value
                )
            }
            PathsConfigurationEvent.OnAddPathClicked -> {
                AddPath()
            }
            is PathsConfigurationEvent.OnDeletePathClicked -> {
                RemovePath(event.Path)
            }
            is PathsConfigurationEvent.OnLoadData -> {
                State = State.copy(
                    Paths = event.Paths
                )
            }
        }
    }

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

    private fun AddPath() {
        try {
            if (State.CurrentPath.isEmpty()) {
                State = State.copy(
                    CurrentPathError = true
                )
                throw FieldNotFilledException()
            }else{
                State = State.copy(
                    CurrentPathError = false
                )
            }

            val path = CreatePathObjectFromStringPath(State.CurrentPath)

            val list = State.Paths.toMutableList()

            list.add(path)

            State = State.copy(
                Paths = list,
                CurrentPath = ""
            )
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    private fun RemovePath(path: Path) {
        val list = State.Paths.toMutableList()

        list.remove(path)

        State = State.copy(
            Paths = list
        )
    }

}