plugins {
    id("io.gitlab.arturbosch.detekt") version "1.7.0-beta2"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("com.github.ben-manes.versions") version "0.28.0"
}

buildscript {
    repositories {
        maven("https://maven.fabric.io/public")
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0-beta05")
        classpath("com.google.gms:google-services:${Versions.googleServices}")
        classpath("io.fabric.tools:gradle:${Versions.fabric}")
        classpath(kotlin("gradle-plugin", version = Versions.kotlin))
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.nav}")
    }
}

subprojects {
    apply {
        plugin("io.gitlab.arturbosch.detekt")
        plugin("org.jlleitschuh.gradle.ktlint")
    }

    repositories {
        maven("https://plugins.gradle.org/m2/")
        maven("https://dl.bintray.com/microsoftazuremobile/SDK")
        maven("https://maven.fabric.io/public")
        maven("https://jitpack.io")
        google()
        jcenter()
    }

    detekt {
        config = files("$rootDir/default-detekt-config.yml")
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
        include("**/*.kt")
        include("**/*.kts")
        exclude("**build**")
        exclude("**/resources/**")
        exclude("**/tmp/**")
    }
}

task("clean") {
    delete(rootProject.buildDir)
}
