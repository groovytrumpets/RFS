plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.groovy.rfs"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.groovy.rfs"
        minSdk = 21
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
    buildFeatures{
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.material:material:1.13.0")

    implementation("com.google.android.gms:play-services-auth:21.2.0")

    //bao mat token luu tren andorid
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    //
    implementation("com.github.bumptech.glide:glide:5.0.5")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.coordinatorlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}