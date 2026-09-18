package uk.co.fredjames.jsonplaceholder

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Root application class annotated for Hilt DI initialization. */
@HiltAndroidApp
class JsonPlaceholderApplication : Application()
