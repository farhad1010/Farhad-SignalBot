plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.farhad.signalbot"

    compileSdk = 35

    defaultConfig {
        applicationId = "com.farhad.signalbot"

        minSdk = 26
        targetSdk = 35

        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        /*
         * API key is supplied through gradle.properties.
         *
         * Do NOT put the real key directly inside source code.
         */
        buildConfigField(
            "String",
            "MARKET_API_KEY",
            "\"${project.findProperty("MARKET_API_KEY") ?: ""}\""
        )
    }

    buildTypes {

        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes +=
                "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    compileOptions {
        sourceCompatibility =
            JavaVersion.VERSION_17

        targetCompatibility =
            JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    // --------------------------------------------------
    // Android core
    // --------------------------------------------------

    implementation(
        "androidx.core:core-ktx:1.16.0"
    )

    implementation(
        "androidx.activity:activity-compose:1.10.1"
    )

    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.9.1"
    )


    // --------------------------------------------------
    // Jetpack Compose
    // --------------------------------------------------

    implementation(
        platform(
            "androidx.compose:compose-bom:2025.06.01"
        )
    )

    implementation(
        "androidx.compose.ui:ui"
    )

    implementation(
        "androidx.compose.ui:ui-tooling-preview"
    )

    implementation(
        "androidx.compose.material3:material3"
    )

    implementation(
        "androidx.compose.material:material-icons-extended"
    )


    // --------------------------------------------------
    // Navigation
    // --------------------------------------------------

    implementation(
        "androidx.navigation:navigation-compose:2.9.0"
    )


    // --------------------------------------------------
    // ViewModel
    // --------------------------------------------------

    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1"
    )


    // --------------------------------------------------
    // Kotlin Coroutines
    // --------------------------------------------------

    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2"
    )


    // --------------------------------------------------
    // Retrofit
    // --------------------------------------------------

    implementation(
        "com.squareup.retrofit2:retrofit:3.0.0"
    )

    implementation(
        "com.squareup.retrofit2:converter-gson:3.0.0"
    )


    // --------------------------------------------------
    // OkHttp
    // --------------------------------------------------

    implementation(
        "com.squareup.okhttp3:okhttp:4.12.0"
    )

    /*
     * Logging interceptor is kept available for development,
     * but production code must not log API credentials.
     */
    implementation(
        "com.squareup.okhttp3:logging-interceptor:4.12.0"
    )


    // --------------------------------------------------
    // Room database
    // --------------------------------------------------

    implementation(
        "androidx.room:room-runtime:2.7.1"
    )

    implementation(
        "androidx.room:room-ktx:2.7.1"
    )

    ksp(
        "androidx.room:room-compiler:2.7.1"
    )


    // --------------------------------------------------
    // Unit tests
    // --------------------------------------------------

    testImplementation(
        "junit:junit:4.13.2"
    )


    // --------------------------------------------------
    // Android tests
    // --------------------------------------------------

    androidTestImplementation(
        "androidx.test.ext:junit:1.2.1"
    )

    androidTestImplementation(
        "androidx.test.espresso:espresso-core:3.7.0"
    )


    // --------------------------------------------------
    // Compose tooling
    // --------------------------------------------------

    debugImplementation(
        "androidx.compose.ui:ui-tooling"
    )
}
