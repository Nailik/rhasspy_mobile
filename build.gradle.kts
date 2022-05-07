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