import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val exposedVersion = "0.37.3"
group = "net.bruty"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter-amqp") // Advance message query protocol | rabbitmq
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// GraphQL | Netflix DGS framework
	implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:latest.release"))
	implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter")

	// Spring testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.amqp:spring-rabbit-test")

	// Metrics
	implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-micrometer")
	implementation ("io.micrometer:micrometer-registry-prometheus:1.8.1")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.11.2")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

	// Bcrypt | hashing
	implementation("org.mindrot:jbcrypt:0.4")


}

plugins {
	id("org.springframework.boot") version "2.7.4"
	id("io.spring.dependency-management") version "1.0.14.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
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
	packageName = "com.tastebuds" // The package name to use to generate sources
	generateClient = true // Enable generating the type safe query API
}