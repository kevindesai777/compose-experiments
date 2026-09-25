import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

// The Android entry point. All UI lives in :composeApp; this module only holds what makes
// it an installable app: MainActivity, the manifest, launcher icons, signing and R8.
// Kotlin is compiled by AGP's built-in Kotlin support, so no kotlin-android plugin.
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

android {
    namespace = "com.pixel.composeexperiments"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.pixel.composeexperiments"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        // Play rejects an upload whose versionCode isn't higher than the last one, so bump
        // both on every release (or pass -PversionCode=N from CI).
        versionCode = 2
        versionName = "1.0.1"
    }
    signingConfigs {
        // Upload key for Play App Signing. Read from keystore.properties (local, gitignored)
        // or ANDROID_* env vars (CI); without either, release builds come out unsigned.
        val keystoreProperties = Properties().apply {
            val file = rootProject.file("keystore.properties")
            if (file.exists()) file.inputStream().use(::load)
        }
        fun secret(key: String, env: String): String? =
            keystoreProperties.getProperty(key) ?: System.getenv(env)

        val storePath = secret("storeFile", "ANDROID_KEYSTORE_PATH")
        if (storePath != null) {
            create("release") {
                storeFile = rootProject.file(storePath)
                storePassword = secret("storePassword", "ANDROID_KEYSTORE_PASSWORD")
                keyAlias = secret("keyAlias", "ANDROID_KEY_ALIAS")
                keyPassword = secret("keyPassword", "ANDROID_KEY_PASSWORD")
            }
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.findByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(projects.composeApp)
    implementation(libs.androidx.activity.compose)
}
