import java.util.Properties

plugins {
    base
}

val sdkDir = providers.fileContents(rootProject.layout.projectDirectory.file("local.properties"))
    .asText.map { text -> Properties().apply { load(text.reader()) }.getProperty("sdk.dir") }.get()
val ndkVersion = providers.gradleProperty("ndkVersion").get()
val nativeAbi = providers.gradleProperty("nativeAbi").get()
val ndkBuild = File(sdkDir, "ndk/$ndkVersion/ndk-build" +
        if (System.getProperty("os.name").startsWith("Windows")) ".cmd" else "")

val buildNative = tasks.register<Exec>("buildNative") {
    inputs.files("helper.c", "Android.mk", "Application.mk")
    inputs.property("nativeAbi", nativeAbi)
    inputs.property("ndkVersion", ndkVersion)
    outputs.dirs(layout.buildDirectory.dir("bin"), layout.buildDirectory.dir("obj"))
    workingDir(projectDir)
    commandLine(ndkBuild, "NDK_PROJECT_PATH=.", "APP_BUILD_SCRIPT=Android.mk",
        "NDK_APPLICATION_MK=Application.mk", "APP_ABI=$nativeAbi",
        "NDK_OUT=build/obj", "NDK_LIBS_OUT=build/bin", "-j${Runtime.getRuntime().availableProcessors()}")
}

tasks.named("assemble") {
    dependsOn(buildNative)
}
