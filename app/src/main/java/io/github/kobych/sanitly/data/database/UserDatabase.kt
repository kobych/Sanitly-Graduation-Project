package io.github.kobych.sanitly.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.kobych.sanitly.data.database.dao.GoalDao
import io.github.kobych.sanitly.data.database.dao.IntroductionDao
import io.github.kobych.sanitly.data.database.dao.QuestionDao
import io.github.kobych.sanitly.data.database.dao.ResultDao
import io.github.kobych.sanitly.data.database.entities.Goal
import io.github.kobych.sanitly.data.database.entities.Introduction
import io.github.kobych.sanitly.data.database.entities.Question
import io.github.kobych.sanitly.data.database.entities.Result

/**
 * Single Room database for the application.
 *
 * Entities: [Question], [Goal].
 * Access instances via [getDatabase] — construction is thread-safe via double-checked locking.
 */
@TypeConverters(ListConverter::class)
@Database(
    entities = [Question::class, Goal::class, Introduction::class, Result::class],
    version = 1,
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun goalDao(): GoalDao
    abstract fun introductionDao(): IntroductionDao
    abstract fun resultDao(): ResultDao

    companion object {
        @Volatile
        private var instance: UserDatabase? = null

        /** Returns the singleton database instance, creating it on first call. */
        fun getDatabase(context: Context): UserDatabase =
            instance ?: synchronized(this) {
                Room
                    .databaseBuilder(context, UserDatabase::class.java, "user_database")
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also {
                        instance = it
                    }
            }
    }
}
