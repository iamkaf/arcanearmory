plugins {
    id("com.iamkaf.multiloader.common")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.1.0")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
