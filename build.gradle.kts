buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        //classpath dependencies cannot be loaded from buildSrc
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
        @Suppress("GradlePluginVersion")
        classpath(Android.tools.build.gradlePlugin)
        classpath("dev.icerock.moko:resources-generator:_")
        classpath("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:_")
        classpath("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:_")
        classpath("com.google.gms:google-services:_")
        classpath("com.google.firebase:firebase-crashlytics-gradle:_")
        classpath("co.touchlab.crashkios:utils:_")
        classpath("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:_")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}