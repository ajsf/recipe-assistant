package com.example.aaron.recipeassistant.common.db.room

import android.arch.persistence.room.TypeConverter

class StringListTypeConverter {

    @TypeConverter
    fun stringToStringList(data: String): List<String> = data.split("^")

    @TypeConverter
    fun stringListToString(list: List<String>): String = list.joinToString("^")
}