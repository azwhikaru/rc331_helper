plugins {
    alias(libs.plugins.android.application) apply false
}

val nativeAbi = providers.gradleProperty("nativeAbi").get()

tasks.register<Sync>("buildOutputs") {
    dependsOn(":app:assembleDebug")
    from("app/build/outputs/apk/debug/app-debug.apk") {
        rename { "RC331.apk" }
    }
    from("native/build/bin/$nativeAbi/helper") {
        filePermissions { unix("rwxr-xr-x") }
    }
    into(layout.buildDirectory.dir("outputs"))
}
