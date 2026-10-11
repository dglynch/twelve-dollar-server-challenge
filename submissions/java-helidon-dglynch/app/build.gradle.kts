plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(platform(libs.helidon.bom))
    implementation(libs.helidon.webserver)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(27)
    }
}

application {
    mainClass = "com.dglynch.twelvedollar.App"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
