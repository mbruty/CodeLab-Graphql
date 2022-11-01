import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.ByteArrayOutputStream
class ReleasePlugin: Plugin<Project> {
	override fun apply(target: Project) {
		target.task("release") {
			doFirst {
				// Grab the version, and bump it
				val version = target.version.toString().split('.').toMutableList()
				version.removeLast()
				var minor: Int = version.removeLast().toInt()

				// See what git branch we're on
				val output = ByteArrayOutputStream()
				target.exec {
					commandLine = "git rev-parse --abbrev-ref HEAD".split(" ")
					standardOutput = output
				}
				val branch = output.toString().trim()

				val isMain = branch == "main" || branch == "master"

				if(isMain) { minor += 1 }

				val runner = System.getenv("GITHUB_RUN_NUMBER")
				// Save the new version
				// <major>.<minor>.<patch>
				val suffix = if(isMain) "${minor}.${runner}" else "${minor}.${runner}-SNAPSHOT"
				target.version = "${version[0]}.${suffix}"

				// Write the changes to the file
				val file = File("${project.projectDir}/build.gradle.kts")
				val s = file.readText().split('\n').toMutableList()
				for (i in s.indices) {
					if(s[i].startsWith("version")) {
						s[i] = "version = \"${target.version}\""
					}
				}
				file.writeText(s.joinToString("\n"))

				val release = if(isMain) "latest" else "snapshot"
				File("${project.projectDir}/release.txt").writeText(release)
				File("${project.projectDir}/tag.txt").writeText(target.version.toString())

				// On release, update application.properties to set mongo connection to 0.0.0.0 instead of external
				val applicationFile = File("./src/main/resources/application.properties")
				val data = applicationFile.readText().split('\n').toMutableList()
				for (i in data.indices) {
					if(data[i].startsWith("spring.data.mongodb.host")) {
						data[i] = "spring.data.mongodb.host=mongodb_container:27017"
					}
				}
				applicationFile.writeText(data.joinToString("\n"))
			}
		}
	}
}

apply<ReleasePlugin>()

val exposedVersion = "0.38.2"

group = "net.bruty"
version = "0.15.19"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
	// region Bcrypt | hashing
	implementation("org.mindrot:jbcrypt:0.4")
	// endregion

	// region GraphQL | Netflix DGS framework
	implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:5.2.2"))
	implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter")
	// endregion

	// region JWeaver
	implementation("org.aspectj:aspectjweaver:1.9.9.1")
	// endregion

	// region JWT
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	// endregion

	// region Metrics
	// implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-micrometer")
	// implementation ("io.micrometer:micrometer-registry-prometheus:1.9.4")
	// implementation("org.springframework.boot:spring-boot-starter-actuator")
	// endregion

	// region ORM | Jetbrains Exposed
	// Update when this issue is fixed https://github.com/JetBrains/Exposed/issues/1556
	implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
	implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
	implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
	// Test database
	testImplementation("com.h2database:h2:1.4.200")
	// endregion

	// region Postgres driver
	implementation("org.postgresql:postgresql:42.5.0")
	// endregion

	// region Spring
	implementation("org.springframework.boot:spring-boot-starter-amqp") // Advance message query protocol | rabbitmq
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	// endregion

	// region Spring testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.amqp:spring-rabbit-test")
	// endregion

	// region JUnit
	testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
	// endregion
}

plugins {
	// Spring
	id("org.springframework.boot") version "2.7.4"
	id("io.spring.dependency-management") version "1.0.14.RELEASE"

	// Kotlin
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.serialization") version "1.4.0"

	// Graphql code gen
	id("com.netflix.dgs.codegen") version "5.1.14"
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.getByName<Jar>("jar") {
	enabled = false
}

// Graphql model generation
tasks.generateJava {
	typeMapping = mutableMapOf(
		"Long" to "java.lang.Long"
	)
	schemaPaths =
		listOf("${projectDir}/src/main/resources/schema").toMutableList() // List of directories containing schema files
	packageName = "net.bruty" // The package name to use to generate sources
	generateClient = true // Enable generating the type safe query API
}