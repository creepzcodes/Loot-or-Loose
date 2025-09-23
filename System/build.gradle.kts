import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta13"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
}

val projectName = providers.gradleProperty("project_name").orNull ?: "app"
val projectGroup = providers.gradleProperty("project.group")
group = projectGroup

val gitRef = System.getenv("CI_COMMIT_REF_NAME") ?: "local"
val buildNumber = System.getenv("CI_PIPELINE_IID") ?: "SNAPSHOT"
val env = gitRef.replace("/", "-")
version = "git-$env"

repositories {
    mavenCentral()

    maven { url = uri("https://jitpack.io") }
    maven {
        name = "ossrh"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven("https://repo.nexomc.com/releases")
    maven("https://nexus.leonardbausenwein.de/repository/maven-releases/")
    maven("https://repo.simplecloud.app/snapshots")

}

dependencies {
    implementation("com.github.kangarko:Foundation:6.9.20")

    implementation("org.ow2.asm:asm:9.8")
    compileOnly("redis.clients:jedis:6.0.0")
    compileOnly("com.zaxxer:HikariCP:6.3.0")
    compileOnly("com.mysql:mysql-connector-j:9.3.0")
    compileOnly("com.nexomc:nexo:1.8.0")

    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")

    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
}

configurations.all {
    exclude(group = "org.mineacademy.plugin")

    resolutionStrategy {
        eachDependency {
            if (requested.group == "org.mineacademy.plugin") {
                useTarget("org.mineacademy:Foundation:${requested.version}")
                because("Replacing problematic plugin dependencies")
            }
        }

        componentSelection {
            all {
                if (candidate.group == "org.mineacademy.plugin") {
                    reject("Excluding problematic plugin dependencies")
                }
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
//    withJavadocJar()
//    withSourcesJar()
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    named<ShadowJar>("shadowJar") {
        archiveFileName.set("${projectName}-git-${env}-${buildNumber}.jar")

        relocate("org.mineacademy.fo", "de.duckmc.core.lib")

        dependencies {
            include(dependency("com.github.kangarko:Foundation"))
            include(dependency("com.github.Fedox-die-Ente:ntr-java"))
        }
    }

    named<Jar>("jar") {
        enabled = false
    }

    assemble {
        dependsOn(reobfJar)
    }

    build {
        dependsOn(shadowJar)
    }
}

val uploadEnabled = providers.gradleProperty("uploadEnabled").orNull == "true"
val uploadHost = providers.gradleProperty("uploadHost").orNull ?: ""
val uploadUser = providers.gradleProperty("uploadUser").orNull ?: ""
val uploadPath = providers.gradleProperty("uploadPath").orNull ?: ""

val shadowJarTask = tasks.named<ShadowJar>("shadowJar")

abstract class UploadJarTask : DefaultTask() {
    @get:InputFile
    abstract val jarFile: RegularFileProperty

    @get:Input
    abstract val uploadHost: Property<String>

    @get:Input
    abstract val uploadUser: Property<String>

    @get:Input
    abstract val uploadPath: Property<String>

    @get:Input
    abstract val uploadEnabled: Property<Boolean>

    @TaskAction
    fun upload() {
        if (!uploadEnabled.get()) {
            logger.info("Upload disabled, skipping")
            return
        }

        val jar = jarFile.get().asFile
        val remote = "${uploadUser.get()}@${uploadHost.get()}:${uploadPath.get()}"

        logger.info("Uploading ${jar.name} to $remote")

        val result = project.exec {
            commandLine("scp", jar.absolutePath, remote)
        }

        if (result.exitValue != 0) {
            throw GradleException("Upload failed with exit code ${result.exitValue}")
        }
    }
}

val uploadJar by tasks.registering(UploadJarTask::class) {
    jarFile.set(shadowJarTask.flatMap { it.archiveFile })
    uploadHost.set(providers.gradleProperty("uploadHost").orElse(""))
    uploadUser.set(providers.gradleProperty("uploadUser").orElse(""))
    uploadPath.set(providers.gradleProperty("uploadPath").orElse(""))
    uploadEnabled.set(providers.gradleProperty("uploadEnabled").map { it == "true" }.orElse(false))

    dependsOn(shadowJarTask)
}

tasks.named("shadowJar") { finalizedBy(uploadJar) }