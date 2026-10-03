import com.iamkaf.multiloader.support.MultiloaderProjectContext

plugins {
    id("com.iamkaf.multiloader.neoforge")
}

val multiloader = MultiloaderProjectContext.of(project)
// The guide book is optional; pass -Parcanearmory.withModonomicon=false to run without it.
val modonomicon = multiloader.optionalProperty("dependencies.modonomicon-neoforge")
        ?.takeIf { providers.gradleProperty("arcanearmory.withModonomicon").orNull != "false" }

dependencies {
    if (modonomicon != null) {
        add("runtimeOnly", "maven.modrinth:modonomicon:$modonomicon")
    }
}
