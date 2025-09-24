plugins {
    id("java")
}

group = "org.xtracat"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(files("./libs/fastcgi-lib.jar"))
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("org.slf4j:slf4j-api:2.0.7")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    // give the jar a custom name (without ".jar")
    archiveBaseName.set("app")

    manifest {
        attributes["Main-Class"] = "org.xtracat.Main"
    }

    // include runtime dependencies if you want a "fat jar"
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

}

tasks.withType<JavaCompile> {
    options.release.set(17)
}
