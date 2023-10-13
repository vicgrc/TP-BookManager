import com.x3t.gradle.plugins.openapi.OpenapiDiffPluginTask
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    id("jacoco")
    id("io.gitlab.arturbosch.detekt") version("1.23.1")
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.spring") version "1.8.22"
    id("org.springdoc.openapi-gradle-plugin") version "1.7.0"
    id("com.x3t.gradle.plugins.openapi.openapi_diff") version "1.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

jacoco {
    toolVersion = "0.8.7"
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

sourceSets {
    create("testIntegration") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }

    create("testArchitecture") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

repositories {
    mavenCentral()
}

val testIntegrationImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val testArchitectureImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.liquibase:liquibase-core")
    implementation("org.postgresql:postgresql")
    implementation("org.springdoc:springdoc-openapi-starter-common:2.1.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("com.willowtreeapps.assertk:assertk:0.27.0")

    testIntegrationImplementation("io.mockk:mockk:1.13.8")
    testIntegrationImplementation("com.willowtreeapps.assertk:assertk:0.27.0")
    testIntegrationImplementation("com.ninja-squad:springmockk:4.0.2")
    testIntegrationImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    testIntegrationImplementation("org.testcontainers:postgresql:1.19.1")
    testIntegrationImplementation("org.testcontainers:junit-jupiter:1.19.1")
    testIntegrationImplementation("org.testcontainers:jdbc-test:1.12.0")
    testIntegrationImplementation("org.testcontainers:testcontainers:1.19.1")

    testArchitectureImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testArchitectureImplementation("com.tngtech.archunit:archunit-junit5:1.0.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

task<Test>("testIntegration") {
    useJUnitPlatform()
    testClassesDirs = sourceSets["testIntegration"].output.classesDirs
    classpath = sourceSets["testIntegration"].runtimeClasspath
}

tasks.register<JacocoReport>("jacocoFullReport") {
    executionData(tasks.named("test").get(), tasks.named("testIntegration").get())
    sourceSets(sourceSets["main"])

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

task<Test>("testArchitecture") {
    useJUnitPlatform()
    testClassesDirs = sourceSets["testArchitecture"].output.classesDirs
    classpath = sourceSets["testArchitecture"].runtimeClasspath
}

detekt {
    toolVersion = "1.23.1"
    config.setFrom("$projectDir/config/detekt.yml")
    buildUponDefaultConfig = true
    allRules = false
    ignoreFailures = true
    basePath = rootProject.projectDir.absolutePath
}

tasks.withType<Detekt>().configureEach {
    basePath = rootProject.projectDir.absolutePath
    reports {
        xml.required.set(true)
        html.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
        md.required.set(true)
    }
}

openApi {
    apiDocsUrl.set("http://localhost:8080/v3/api-docs.yaml")
    outputFileName.set("openapi.yaml")
}

tasks.register<OpenapiDiffPluginTask>("openApiDiff") {
    originalFile = "docs/openapi.yaml"
    newFile = "build/openapi.yaml"
    failOnIncompatible = true
    htmlReport = true
    markdownReport = true
    reportName = "build/OpenapiDiffReport"

    onlyIf {
        File(originalFile.get()).exists()
                && File(newFile.get()).exists()
    }
}

tasks.register<GradleBuild>("testContract") {
    tasks = listOf("generateOpenApiDocs", "openApiDiff", "copyOpenApi")
}

tasks.register<Copy>("copyOpenApi") {
    dependsOn("generateOpenApiDocs")
    from(layout.buildDirectory.file("openapi.yaml"))
    into(layout.projectDirectory.dir("docs"))
}