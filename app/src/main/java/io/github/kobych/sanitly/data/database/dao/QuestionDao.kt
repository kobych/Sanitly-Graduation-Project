package io.github.kobych.sanitly.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import io.github.kobych.sanitly.data.database.entities.Question

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions")
    suspend fun getAllQuestions(): List<Question>

    @Insert(onConflict = REPLACE)
    suspend fun saveQuestions(questions: List<Question>)

    @Query("DELETE FROM questions")
    suspend fun deleteQuestions()

    @Transaction
    suspend fun replaceAll(questions: List<Question>) {
        deleteQuestions()
        saveQuestions(questions)
    }
}
