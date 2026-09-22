package io.github.kobych.sanitly.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.kobych.sanitly.data.database.entities.Answer
import io.github.kobych.sanitly.data.database.entities.Task

class ListConverter {
    /**
     * Answers
     */
    @TypeConverter
    fun fromAnswerList(answer: List<Answer>): String = Gson().toJson(answer)

    @TypeConverter
    fun toAnswerList(string: String): List<Answer> {
        val listType = object : TypeToken<List<Answer>>() {}.type
        return Gson().fromJson(string, listType)
    }

    /**
     * Tasks
     */
    @TypeConverter
    fun fromTaskList(task: List<Task>): String = Gson().toJson(task)

    @TypeConverter
    fun toTaskList(string: String): List<Task> {
        val listType = object : TypeToken<List<Task>>() {}.type
        return Gson().fromJson(string, listType)
    }

    /**
     * Strings
     */
    @TypeConverter
    fun fromStringList(list: List<String>): String = Gson().toJson(list)

    @TypeConverter
    fun toStringList(string: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(string, listType)
    }
}
