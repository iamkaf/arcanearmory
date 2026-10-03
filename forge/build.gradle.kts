import com.iamkaf.multiloader.support.MultiloaderProjectContext
import com.iamkaf.multiloader.support.VersionPolicy

plugins {
    id("com.iamkaf.multiloader.forge")
}

val multiloader = MultiloaderProjectContext.of(project)
// The guide book is optional; pass -Parcanearmory.withModonomicon=false to run without it.
val modonomicon = multiloader.optionalProperty("dependencies.modonomicon-forge")
        ?.takeIf { providers.gradleProperty("arcanearmory.withModonomicon").orNull != "false" }

dependencies {
    if (modonomicon != null) {
        val configuration = if (VersionPolicy.usesLegacyForgePlugin(multiloader.minecraftVersion())) "modRuntimeOnly" else "runtimeOnly"
        add(configuration, "maven.modrinth:modonomicon:$modonomicon")
    }
}
