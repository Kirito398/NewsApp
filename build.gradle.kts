import io.gitlab.arturbosch.detekt.extensions.DetektExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.daggerHiltAndroid) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.detekt) apply false
}

allprojects.onEach { project ->
    project.afterEvaluate {
        if (project.plugins.hasPlugin(libs.plugins.jetbrainsKotlinAndroid.get().pluginId)
            || project.plugins.hasPlugin(libs.plugins.jetbrainsKotlinJvm.get().pluginId)
        ) {
            project.plugins.apply(libs.plugins.detekt.get().pluginId)

            project.extensions.configure<DetektExtension> {
                config.setFrom(rootProject.files("default-detekt-config.yml"))
            }

            project.dependencies.add("detektPlugins", libs.detekt.formatting.get().toString())
        }
    }
}