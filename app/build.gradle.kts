import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kover)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.android)
}

// Validated at startup by AppConfig.parse; the build only checks that it's set.
val apiUrl: String =
    providers.gradleProperty("jsonplaceholder.apiUrl").orNull
        ?: error("jsonplaceholder.apiUrl is not set; see docs/README-getting-started.md")

android {
    namespace = "uk.co.fredjames.jsonplaceholder"
    compileSdk = 37

    defaultConfig {
        // Distinct from the React Native app's package, so both install side by side.
        applicationId = "uk.co.fredjames.jsonplaceholderandroid"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"

        buildConfigField("String", "API_URL", "\"$apiUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        // Robolectric needs the merged resources and manifest.
        unitTests.isIncludeAndroidResources = true
        unitTests.all {
            // Robolectric reaches into JDK internals to fake an API 36+ device, which newer JDKs
            // (Android Studio's bundled JBR is 25) block unless opened explicitly.
            it.jvmArgs(
                "--add-opens=java.base/jdk.internal.access=ALL-UNNAMED",
                "--enable-native-access=ALL-UNNAMED",
            )
        }
    }

    lint {
        warningsAsErrors = true
        abortOnError = true
        // New releases would otherwise turn a green build red overnight; version bumps are
        // a deliberate change to gradle/libs.versions.toml instead.
        disable += setOf("GradleDependency", "NewerVersionAvailable", "AndroidGradlePluginVersion", "Typos")
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
        allWarningsAsErrors = true
    }
}

ktlint {
    version = libs.versions.ktlint.cli
}

kover {
    reports {
        filters {
            excludes {
                classes("*.BuildConfig", "*.ComposableSingletons*")
                annotatedBy("androidx.compose.ui.tooling.preview.Preview")
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.hilt.android)
    "kapt"(libs.hilt.compiler)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.hilt.navigation.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.robolectric)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    // Compose's test library brings Espresso 3.5, which calls InputManager APIs removed in API 36+.
    testImplementation(libs.androidx.test.espresso.core)
    // Registers the empty activity createComposeRule() hosts content in.
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    ktlintRuleset(libs.compose.rules.ktlint)
}
