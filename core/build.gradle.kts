plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}

tasks.test {
    useJUnit()
    // Temporary v3 fingerprint acquisition, remove after hashes are pinned.
    testLogging.showStandardStreams = true
}
