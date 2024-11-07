plugins {
    id("java")
}

allprojects {
    group = "de.inmediasp.codingkatas"
    version = "0.1"
}

subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
    }
}