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
        versionCode = providers.gradleProperty("qaVersionCode").orNull?.toInt() ?: 58
        versionName = "0.9.14-cabinet-proportions"
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
    implementation("androidx.core:core-splashscreen:1.0.1")
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

/**
 * Debug-only source-backed book art. Each QA build reproduces the exact 18
 * SVG prototypes from WS-12C/D/F before merging local Android assets.
 * No release variant references this task or assets; no online service used.
 */
val generateWs12QaArt by tasks.registering(Exec::class) {
    workingDir = rootProject.projectDir
    commandLine(
        "python3", "tools/asset_pipeline/render_family_review.py",
        "--tokens", "design/asset-pipeline/paperback-v1-tokens.json",
        "--output-dir", "app/src/debug/assets/ws12"
    )
}
// Decode verified SVGs at build time so playable cards use lightweight
// Compose PNGs, not 18 costly per-card WebViews. This is DEBUG ONLY.
val rasterizeWs12QaArt by tasks.registering(Exec::class) {
    dependsOn(generateWs12QaArt)
    workingDir = rootProject.projectDir
    commandLine(
        "python3", "tools/asset_pipeline/rasterize_android_qa.py",
        "--input-dir", "app/src/debug/assets/ws12",
        "--output-dir", "app/src/debug/assets/ws12/raster"
    )
}
tasks.matching { it.name == "mergeDebugAssets" }.configureEach {
    dependsOn(rasterizeWs12QaArt)
}


/**
 * WS-12K — a second, independently hashed experimental illustration style.
 * The original quiet art and all saved games remain untouched.
 * No study artwork is included in release.
 */
val generateWs12FieldNotes by tasks.registering(Exec::class) {
    dependsOn(generateWs12QaArt)
    workingDir = rootProject.projectDir
    commandLine(
        "python3", "tools/asset_pipeline/render_field_notes_study.py",
        "--tokens", "design/asset-pipeline/paperback-v1-tokens.json",
        "--original-dir", "app/src/debug/assets/ws12",
        "--output-dir", "app/src/debug/assets/ws12/alt/field-notes"
    )
}
val rasterizeWs12FieldNotes by tasks.registering(Exec::class) {
    dependsOn(generateWs12FieldNotes)
    workingDir = rootProject.projectDir
    commandLine(
        "python3", "tools/asset_pipeline/rasterize_android_qa.py",
        "--input-dir", "app/src/debug/assets/ws12/alt/field-notes",
        "--output-dir", "app/src/debug/assets/ws12/raster_field_notes"
    )
}
tasks.matching { it.name == "mergeDebugAssets" }.configureEach {
    dependsOn(rasterizeWs12FieldNotes)
}
