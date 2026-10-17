import com.android.build.api.artifact.SingleArtifact

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.azwhikaru.rc331"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.azwhikaru.rc331helper"
        minSdk = 30
        targetSdk = 30
        versionCode = 3
        versionName = "1.2"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lint {
        disable += "ExpiredTargetSdkVersion"
    }
}

tasks.named("preBuild") {
    dependsOn(":native:assemble")
}

val nativeAbi = providers.gradleProperty("nativeAbi").get()
val redirectedApkDir = providers.gradleProperty("android.injected.apk.location").orNull?.let { file(it) }

androidComponents.onVariants { variant ->
    val variantName = variant.name.replaceFirstChar { it.uppercaseChar() }
    val nativeHelper = rootProject.file("native/build/bin/$nativeAbi/helper")
    val targetDir = redirectedApkDir?.resolve(variant.name)
    val helperOutput = if (targetDir != null) {
        providers.provider { targetDir.resolve("helper") }
    } else {
        variant.artifacts.get(SingleArtifact.APK).map { it.file("helper").asFile }
    }
    val copyNative = tasks.register("copy${variantName}Native") {
        dependsOn(":native:assemble")
        inputs.file(nativeHelper)
        outputs.file(helperOutput)
        doLast {
            val output = helperOutput.get()
            output.parentFile.mkdirs()
            nativeHelper.copyTo(output, overwrite = true)
            output.setExecutable(true, false)
        }
    }
    tasks.matching { it.name == "assemble$variantName" }.configureEach {
        finalizedBy(copyNative)
    }
}
