plugins {
    id("java")
}

group = "de.inmediasp.codingkatas"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":game-algorithms:core"))

    implementation("org.springframework:spring-context:6.2.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}