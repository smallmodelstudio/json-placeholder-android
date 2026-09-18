package uk.co.fredjames.jsonplaceholder.core.config

import java.net.URI
import java.net.URISyntaxException

/** The app's configuration, parsed from `BuildConfig` once at startup. */
data class AppConfig(
    /** Base URL of the proxy API, without a trailing slash. */
    val apiUrl: String,
) {
    companion object {
        private const val API_URL_KEY = "jsonplaceholder.apiUrl"

        /**
         * Validates the raw values the build inlined. Throws [IllegalArgumentException] naming the
         * Gradle property for a missing or invalid value, so a bad config fails at launch rather
         * than on the first request.
         */
        fun parse(apiUrl: String?): AppConfig {
            require(!apiUrl.isNullOrBlank()) { "Invalid configuration: $API_URL_KEY is not set" }
            require(isHttpUrl(apiUrl)) {
                "Invalid configuration: $API_URL_KEY must be an http or https URL (got \"$apiUrl\")"
            }
            return AppConfig(apiUrl = apiUrl.trimEnd('/'))
        }

        private fun isHttpUrl(value: String): Boolean {
            val uri =
                try {
                    URI(value)
                } catch (_: URISyntaxException) {
                    return false
                }
            return uri.scheme in setOf("http", "https") && !uri.host.isNullOrEmpty()
        }
    }
}
