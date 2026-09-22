package io.github.kobych.sanitly.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.kobych.sanitly.data.database.dao.QuestionDao
import io.github.kobych.sanitly.data.database.UserDatabase
import io.github.kobych.sanitly.data.database.dao.GoalDao
import io.github.kobych.sanitly.data.database.dao.IntroductionDao
import io.github.kobych.sanitly.data.database.dao.ResultDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    @Singleton
    fun provideQuestionDao(
        @ApplicationContext context: Context,
    ): QuestionDao = UserDatabase.getDatabase(context).questionDao()

    @Provides
    @Singleton
    fun provideGoalDao(
        @ApplicationContext context: Context
    ): GoalDao = UserDatabase.getDatabase(context).goalDao()

    @Provides
    @Singleton
    fun provideIntroductionDao(
        @ApplicationContext context: Context,
    ): IntroductionDao = UserDatabase.getDatabase(context).introductionDao()

    @Provides
    @Singleton
    fun provideResultDao(
        @ApplicationContext context: Context,
    ): ResultDao = UserDatabase.getDatabase(context).resultDao()
}
