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
    implementation(libs.helidon.http.media.json)
    implementation(libs.helidon.json.binding)
    implementation(libs.helidon.http.media.json.binding)
    implementation(libs.sqlite.jdbc)

    annotationProcessor(libs.helidon.codegen.apt)
    annotationProcessor(libs.helidon.service.codegen)
    annotationProcessor(libs.helidon.json.codegen)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(27)
    }
}

application {
    mainClass = "com.dglynch.twelvedollar.App"
    applicationDefaultJvmArgs = listOf(
        "-Xmx1024m",
        "-Xms1024m",
        "-XX:+AlwaysPreTouch",
        "-XX:+CrashOnOutOfMemoryError",
        "-XX:+HeapDumpOnOutOfMemoryError",
    )
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
