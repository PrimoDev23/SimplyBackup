package com.simplyteam.simplybackup.data.converter

import androidx.room.TypeConverter
import com.simplyteam.simplybackup.data.models.Path
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class Converters {

    private val _pathListAdapter: JsonAdapter<List<Path>> = Moshi.Builder().build().adapter(Types.newParameterizedType(List::class.java, Path::class.java))

    @TypeConverter
    fun RestoreMutableList(json: String) : List<Path> {
        return _pathListAdapter.fromJson(json)!!
    }

    @TypeConverter
    fun SaveMutableList(list: List<Path>) : String {
        return _pathListAdapter.toJson(list)
    }

}