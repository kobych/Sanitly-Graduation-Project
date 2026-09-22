import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

subprojects {
    val localProperties by extra(
        Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) {
                load(file.inputStream())
            }
        },
    )

    val githubToken by extra(
        localProperties.getProperty("github_token") ?: "",
    )

    /**
     * После обновления AGP, BaseAppModuleExtension был помечен как deprecated
     * BaseAppModuleExtension не предназначен в таких файлах начиная с новых версий AGP.
     * ApplicationExtension совместим с AGP и его рекомендует гугл
     */
    plugins.withId("com.android.application") {
        configure<com.android.build.api.dsl.ApplicationExtension> {
            defaultConfig {
                buildConfigField("String", "GITHUB_TOKEN", "\"$githubToken\"")
            }
        }
    }
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        config.setFrom(file("config/detekt/detekt.yml"))
        buildUponDefaultConfig = false
        allRules = false
        enableCompilerPlugin.set(true)
    }

    ktlint {
        android.set(true)
        outputColorName.set("RED")
    }
}
