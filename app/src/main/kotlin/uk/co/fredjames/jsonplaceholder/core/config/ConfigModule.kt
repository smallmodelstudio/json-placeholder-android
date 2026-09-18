package uk.co.fredjames.jsonplaceholder.core.config

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.co.fredjames.jsonplaceholder.BuildConfig
import javax.inject.Singleton

/** Hilt Module providing application-wide configuration. */
@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {
    /** Provides the parsed [AppConfig] instance as a singleton. */
    @Provides
    @Singleton
    fun provideAppConfig(): AppConfig = AppConfig.parse(BuildConfig.API_URL)
}
