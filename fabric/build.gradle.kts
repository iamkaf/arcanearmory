plugins {
    id("com.iamkaf.multiloader.fabric")
}

extensions.configure<com.iamkaf.multiloader.fabric.MultiloaderFabricExtension>("multiloaderFabric") {
    commonDatagen.set(true)
}
