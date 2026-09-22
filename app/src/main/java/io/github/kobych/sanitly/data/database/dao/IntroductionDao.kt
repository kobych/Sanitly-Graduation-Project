package io.github.kobych.sanitly.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import io.github.kobych.sanitly.data.database.entities.Introduction

@Dao
interface IntroductionDao {
    @Query("SELECT * FROM introductions")
    suspend fun getAllIntroductions(): List<Introduction>

    @Insert(onConflict = REPLACE)
    suspend fun saveIntroductions(introductions: List<Introduction>)

    @Query("DELETE FROM introductions")
    suspend fun deleteIntroductions()

    @Transaction
    suspend fun replaceAll(introductions: List<Introduction>) {
        deleteIntroductions()
        saveIntroductions(introductions)
    }
}
