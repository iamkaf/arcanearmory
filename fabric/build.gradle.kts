import com.iamkaf.multiloader.support.MultiloaderProjectContext

plugins {
    id("com.iamkaf.multiloader.fabric")
}

extensions.configure<com.iamkaf.multiloader.fabric.MultiloaderFabricExtension>("multiloaderFabric") {
    commonDatagen.set(true)
}

val multiloader = MultiloaderProjectContext.of(project)
// The guide book is optional; pass -Parcanearmory.withModonomicon=false to run without it.
val modonomicon = multiloader.optionalProperty("dependencies.modonomicon-fabric")
        ?.takeIf { providers.gradleProperty("arcanearmory.withModonomicon").orNull != "false" }

// Modonomicon ships a datagen entrypoint of its own; run only this mod's.
tasks.matching { it.name == "runDatagen" }.configureEach {
    (this as JavaExec).systemProperty("fabric-api.datagen.modid", "arcanearmory")
}

repositories {
    maven("https://maven.modmuss50.me/") {
        name = "FiberConfig"
        content {
            includeGroup("me.zeroeightsix")
        }
    }
}

dependencies {
    if (modonomicon != null) {
        if (multiloader.useUnobfuscatedMinecraft()) {
            add("runtimeOnly", "maven.modrinth:modonomicon:$modonomicon")
        } else {
            add("modLocalRuntime", "maven.modrinth:modonomicon:$modonomicon")
            // Loom drops the libraries nested in a remapped Modonomicon jar, so supply them directly.
            add("localRuntime", "me.zeroeightsix:fiber:0.23.0-2")
            add("localRuntime", "org.commonmark:commonmark:0.22.0")
            add("localRuntime", "org.commonmark:commonmark-ext-gfm-strikethrough:0.22.0")
            add("localRuntime", "org.commonmark:commonmark-ext-ins:0.22.0")
        }
    }
}
