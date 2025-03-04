plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "2.0.10"
}

group = "org.ttlzmc"
version = 1.0

val javafxVersion = "17.0.11"

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
    maven("https://releases.groupdocs.com/java/repo/")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainClass = "org.ttlzmc.app.UpdaterApplicationKt"
    applicationDefaultJvmArgs = listOf("-Xmx512m")
}

javafx {
    version = javafxVersion
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("org.openjfx:javafx-base:$javafxVersion")
    implementation("org.openjfx:javafx-controls:$javafxVersion")
    implementation("org.openjfx:javafx-graphics:$javafxVersion")
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("net.java.dev.jna:jna:5.13.0")
    implementation("net.java.dev.jna:jna-platform:5.13.0")
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("org.sejda.webp-imageio:webp-imageio-sejda:0.1.0")
    implementation("com.groupdocs:groupdocs-conversion:25.2")
}