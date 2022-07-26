buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        classpath "org.jetbrains.kotlin:kotlin-serialization:$kotlin_version"
    }
}

plugins {
    id 'org.jetbrains.kotlin.jvm' version '1.7.0'
    id 'pl.allegro.tech.build.axion-release' version '1.13.14'
    id 'maven-publish'
}

scmVersion {
    useHighestVersion = true
    tag {
        prefix = 'krush-'
    }
}

group = 'pl.touk.krush'
project.version = scmVersion.version

allprojects {
    repositories {
        mavenCentral()
    }
}

configure([project(':annotation-processor'), project(':runtime'), project(':runtime-postgresql')]) {
    apply plugin: 'java'
    apply plugin: 'maven-publish'
    apply plugin: 'signing'

    def snapshot = scmVersion.version.endsWith('SNAPSHOT')

    tasks.withType(Sign) {
        onlyIf { !snapshot }
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    publishing {
        publications {
            mavenJava(MavenPublication) {
                artifactId "krush-${project.name}"
                from components.java
                pom {
                    groupId = 'pl.touk.krush'
                    name = "krush-${project.name}"
                    version = scmVersion.version

                    description = 'Krush, idiomatic persistence layer for Kotlin'
                    url = 'https://github.com/TouK/sputnik/'
                    scm {
                        url = 'scm:git@github.com:TouK/krush.git'
                        connection = 'scm:git@github.com:TouK/krush.git'
                        developerConnection = 'scm:git@github.com:Touk/krush.git'
                    }
                    licenses {
                        license {
                            name = 'The Apache Software License, Version 2.0'
                            url = 'https://www.apache.org/licenses/LICENSE-2.0.txt'
                            distribution = 'repo'
                        }
                    }
                    developers {
                        developer {
                            id = 'mateusz_sledz'
                            name = 'Mateusz Śledź'
                        }
                        developer {
                            id = 'piotr_jagielski'
                            name = 'Piotr Jagielski'
                        }
                    }
                }
            }
        }

        repositories {
            maven {
                credentials {
                    username = System.getenv("SONATYPE_USERNAME")
                    password = System.getenv("SONATYPE_PASSWORD")
                }
                def releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
                def snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
                url = snapshot ? snapshotsRepoUrl : releasesRepoUrl
            }
        }
    }

    signing {
        useInMemoryPgpKeys(System.getenv('SIGNING_PRIVATE_KEY'), System.getenv('SIGNING_PASSWORD'))
        sign publishing.publications.mavenJava
    }

}
