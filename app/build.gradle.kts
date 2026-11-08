plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.compose")
}

android {
    namespace = "com.playereden.gridmates.wordsearch"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.playereden.gridmates.wordsearch"
        minSdk = 26
        targetSdk = 35
        versionCode = providers.gradleProperty("qaVersionCode").orNull?.toInt() ?: 46
        versionName = "0.9.2-continuous-region-nav-qa-alpha"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Test-only public QA signer: NEVER use for a release or Google Play.
    signingConfigs {
        create("wordSearchQa") {
            storeFile = rootProject.file("qa-signing/wordsearch-qa.p12")
            storeType = "pkcs12"
            storePassword = "android" // public QA-only credential
            keyAlias = "wordsearchQa"
            keyPassword = "android"
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("wordSearchQa")
            // A QA build installs alongside the eventual production Word Search.
            applicationIdSuffix = ".qa"
            versionNameSuffix = "-qa"
        }
        getByName("release") {
            isMinifyEnabled = false
            // Production signing will be configured in a separate approved release pass.
        }
    }

    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core"))
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(platform("androidx.compose:compose-bom:2025.04.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.04.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
}
