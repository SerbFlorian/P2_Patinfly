plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}


android {
    namespace = "cat.deim.asm_22.p2_patinfly"
    compileSdk = 35

    defaultConfig {
        applicationId = "cat.deim.asm_22.p2_patinfly"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation (libs.jbcrypt)
    implementation (libs.bcrypt.v0102) // Use the latest version
    implementation(libs.retrofit)
    // Gson converter for JSON parsing
    implementation(libs.converter.gson)
    // Kotlin Coroutines for asynchronous operations
    implementation(libs.kotlinx.coroutines.android)
    // OkHttp logging interceptor (optional, but very useful for debugging API calls)
    implementation(libs.logging.interceptor)

    implementation (libs.jjwt.api)
    implementation (libs.jjwt.impl)
    implementation (libs.jjwt.jackson)
    implementation(libs.kotlinx.serialization.json) // o la versión que uses
    implementation(libs.retrofit2.kotlinx.serialization.converter)

    // Retrofit y conversores
    //noinspection UseTomlInstead
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    //noinspection UseTomlInstead
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Para JSON
    //noinspection UseTomlInstead
    implementation("com.squareup.okhttp3:okhttp:4.11.0") // Cliente HTTP

    // Kotlin Serialization (opcional, si se usa)
    //noinspection UseTomlInstead
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    //noinspection UseTomlInstead
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // Room components
    implementation(libs.androidx.room.runtime)

    //noinspection KaptUsageInsteadOfKsp
    kapt(libs.androidx.room.compiler)
    // Kotlin Extensions and Coroutines support
    implementation(libs.androidx.room.ktx)

    implementation (libs.androidx.material.icons.extended)
    implementation (libs.androidx.foundation)
    implementation (libs.material3)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.gson)
    implementation(libs.androidx.lifecycle.runtime.ktx.v270)
    implementation(libs.androidx.activity.compose.v182)
    implementation(platform(libs.androidx.compose.bom.v20230800))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(platform(libs.androidx.compose.bom.v20250300))
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.androidx.material)
    implementation(libs.androidx.lifecycle.runtime.ktx.v262)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.bcrypt)
    implementation(libs.gson)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.benchmark.macro)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}