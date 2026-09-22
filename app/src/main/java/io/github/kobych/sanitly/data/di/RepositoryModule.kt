package io.github.kobych.sanitly.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.kobych.sanitly.data.GitHubClient
import io.github.kobych.sanitly.data.database.dao.GoalDao
import io.github.kobych.sanitly.data.database.dao.IntroductionDao
import io.github.kobych.sanitly.data.repositories.NetworkRemoteResourcesRepository
import io.github.kobych.sanitly.data.repositories.StatusSurveyRepository
import io.github.kobych.sanitly.data.repositories.UserPreferencesRepository
import io.github.kobych.sanitly.data.database.dao.QuestionDao
import io.github.kobych.sanitly.data.database.dao.ResultDao
import io.github.kobych.sanitly.data.repositories.AnalyticsRepository
import io.github.kobych.sanitly.data.repositories.DiaryRepository
import io.github.kobych.sanitly.data.repositories.GoalBuilderRepository
import io.github.kobych.sanitly.data.repositories.GoalListRepository
import io.github.kobych.sanitly.network.ConnectivityChecker
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideNetworkGitHubJsonRepository(): NetworkRemoteResourcesRepository =
        NetworkRemoteResourcesRepository(GitHubClient.api)

    @Provides
    @Singleton
    fun provideStatusSurveyRepository(
        networkGitHubJsonRepository: NetworkRemoteResourcesRepository,
        questionDao: QuestionDao,
        introductionDao: IntroductionDao,
        resultDao: ResultDao,
        connectivityChecker: ConnectivityChecker
    ): StatusSurveyRepository = StatusSurveyRepository(
        networkGitHubJsonRepository,
        questionDao,
        introductionDao,
        resultDao,
        connectivityChecker
    )

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(dataStore: DataStore<Preferences>): UserPreferencesRepository =
        UserPreferencesRepository(dataStore)

    @Provides
    @Singleton
    fun provideGoalBuilderRepository(
        goalDao: GoalDao
    ): GoalBuilderRepository = GoalBuilderRepository(goalDao)

    @Provides
    @Singleton
    fun provideGoalProcessRepository(
        goalDao: GoalDao
    ): GoalListRepository = GoalListRepository(goalDao)

    @Provides
    @Singleton
    fun provideDiaryRepository(
        goalProcessRepository: GoalListRepository
    ): DiaryRepository = DiaryRepository(goalProcessRepository)

    @Provides
    @Singleton
    fun provideAnalyticsRepository(
        goalProcessRepository: GoalListRepository
    ): AnalyticsRepository = AnalyticsRepository(goalProcessRepository)
}
