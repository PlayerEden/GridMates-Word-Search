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
    // Temporary WS-09A2 baseline acquisition; remove after pinning literal goldens.
    testLogging.showStandardStreams = true
}
