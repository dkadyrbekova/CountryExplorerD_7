plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.countryexplorerd"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.countryexplorerd"
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
}

dependencies {
    // Базовые библиотеки
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    // ФИКС: Явно указываем стабильную версию activity вместо libs.activity
    implementation("androidx.activity:activity:1.9.3")

    // Retrofit & GSON для работы с сетью
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Room для базы данных
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // Glide для загрузки флагов
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Lifecycle (ViewModel & LiveData)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")

    // Тестирование
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}