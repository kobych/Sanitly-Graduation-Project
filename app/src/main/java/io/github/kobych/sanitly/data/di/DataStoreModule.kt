package io.github.kobych.sanitly.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.kobych.sanitly.SanitlyApplication
import io.github.kobych.sanitly.dataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun providesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.dataStore
}
