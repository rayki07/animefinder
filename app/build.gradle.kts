plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //parcelize
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.animefinder"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.animefinder"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //tambahan gemini
    implementation(libs.core.ktx) // Pastikan versi terbaru
    implementation(libs.appcompat) // Pastikan versi terbaru
    implementation(libs.google.material) // Untuk desain modern
    implementation(libs.androidx.constraintlayout) // Untuk tata letak fleksibel
    implementation(libs.androidx.recyclerview) // Untuk daftar yang efisien

    //Glide for Image Loading
    implementation(libs.glide)

    // Retrofit (HTTP Client)
    implementation(libs.retrofit)

    // Retrofit Converter untuk Gson (JSON Parser)
    implementation(libs.converter.gson)

    // Kotlin Coroutines (Untuk operasi asynchronous yang lebih mudah)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    //ViewModel dan LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)


}