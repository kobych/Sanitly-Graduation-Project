package io.github.kobych.sanitly.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import io.github.kobych.sanitly.data.database.entities.Result

@Dao
interface ResultDao {
    @Query("SELECT * FROM results")
    suspend fun getAllResults(): List<Result>

    @Insert(onConflict = REPLACE)
    suspend fun saveResults(results: List<Result>)

    @Query("DELETE FROM results")
    suspend fun deleteResults()

    @Transaction
    suspend fun replaceAll(results: List<Result>) {
        deleteResults()
        saveResults(results)
    }
}
