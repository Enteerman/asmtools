
plugins {
    java
    application
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.5"
}

repositories {
    jcenter()
}
val main: String = "org.openjdk.asmtools.Main"

application {
    mainClassName = main
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

tasks {
    jar {
        manifest {
            attributes(mapOf("Main-Class" to main))
        }
    }
}

val projectVersion = System.getenv("TRAVIS_TAG") ?: "unspecified"

publishing {
    publications {
        register<MavenPublication>("bintray") {
            from(components["java"])
            groupId = "me.enterman"
            artifactId = "asmtools"
            version = projectVersion
        }
    }
}

tasks.register("bintray") {
    dependsOn("bintrayUpload", "bintrayPublish")
}

fun findProperty(s: String) = project.findProperty(s) as String?

bintray {
    user = System.getenv("bintrayUser")
    key = System.getenv("bintrayApiKey")
    publish = true
    setPublications("bintray")
    pkg(delegateClosureOf<com.jfrog.bintray.gradle.BintrayExtension.PackageConfig> {
        repo = "asmtools"
        name = "asmtools"
        userOrg = user
        setLabels("asmtools")
        version(closureOf<com.jfrog.bintray.gradle.BintrayExtension.VersionConfig> {
            name = projectVersion
        })
    })
}
