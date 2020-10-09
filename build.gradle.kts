buildscript {
    repositories {
        maven("https://maven.fabric.io/public")
        jcenter()
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.com_android_tools_build_gradle}")
        classpath("com.google.gms:google-services:${Versions.google_services}")
        classpath("io.fabric.tools:gradle:${Versions.io_fabric_tools_gradle}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.org_jetbrains_kotlin}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.androidx_navigation}")
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt") version Versions.io_gitlab_arturbosch_detekt
    id("de.fayard.buildSrcVersions") version Versions.de_fayard_buildsrcversions_gradle_plugin
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.io_gitlab_arturbosch_detekt}")
}

allprojects {
    repositories {
        maven("https://plugins.gradle.org/m2/")
        maven("https://dl.bintray.com/microsoftazuremobile/SDK")
        maven("https://maven.fabric.io/public")
        maven("https://jitpack.io")
        google()
        jcenter()
    }
}

task("clean") {
    delete(rootProject.buildDir)
}

val detektAll by tasks.registering(io.gitlab.arturbosch.detekt.Detekt::class) {
    description = "Runs over whole code base without the starting overhead for each module."
    buildUponDefaultConfig = true
    autoCorrect = true
    parallel = true
    setSource(files(projectDir))
    config.setFrom(files("$rootDir/detekt.yml"))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/build/**")
    exclude("**/buildSrc/**")
    exclude("**/test/**/*.kt")
    reports {
        xml.enabled = false
        html.enabled = false
        txt.enabled = false
    }
}
