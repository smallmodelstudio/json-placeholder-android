package uk.co.fredjames.jsonplaceholder

import android.app.Application
import uk.co.fredjames.jsonplaceholder.core.config.AppConfig

class JsonPlaceholderApplication : Application() {
    /** Parsed in [onCreate], so an invalid config crashes at launch with a clear message. */
    lateinit var config: AppConfig
        private set

    override fun onCreate() {
        super.onCreate()
        config = AppConfig.parse(BuildConfig.API_URL)
    }
}
