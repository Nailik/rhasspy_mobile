buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        //classpath dependencies cannot be loaded from buildSrc
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:_")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
        classpath("dev.icerock.moko:resources-generator:_")
        classpath("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:_")
        classpath("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:_")
        classpath("com.google.gms:google-services:_")
        classpath("com.google.firebase:firebase-crashlytics-gradle:_")
        classpath("org.gradle:test-retry-gradle-plugin:_")
        classpath("org.jetbrains.compose:compose-gradle-plugin:_")
        classpath("org.kodein.mock:mockmp-gradle-plugin:_")
        classpath("co.touchlab.crashkios.crashlyticslink:co.touchlab.crashkios.crashlyticslink.gradle.plugin:_")
        classpath("com.android.tools.build:gradle:_")
        classpath("de.undercouch.download:de.undercouch.download.gradle.plugin:_")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://androidx.dev/storage/compose-compiler/repository/")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}