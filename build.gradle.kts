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
        classpath("com.android.tools.build:gradle:_")
        classpath("dev.icerock.moko:resources-generator:_")
    }
}

allprojects {
    repositories {
        google()
        maven {
            setUrl("https://repo.eclipse.org/content/repositories/paho-releases/")
        }
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}