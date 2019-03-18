repositories {
    mavenCentral()
    jcenter()
}


plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "0.10.1"
}

gradlePlugin {
    plugins {
        register("com.epages.restdocs-api-spec") {
            id = "com.epages.restdocs-api-spec"
            implementationClass = "com.epages.restdocs.apispec.gradle.RestdocsApiSpecPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/ePages-de/restdocs-api-spec"
    vcsUrl = "https://github.com/ePages-de/restdocs-api-spec"
    tags = listOf("spring", "restdocs", "openapi", "openapi3", "postman", "api", "specification")
    description = "Extends Spring REST Docs with API specifications in OpenAPI2, OpenAPI3 and Postman Collections formats"

    (plugins) {
        "com.epages.restdocs-api-spec" {
            displayName = "restdocs-api-spec gradle plugin"
        }
    }

    mavenCoordinates {
        groupId = "com.epages"
        artifactId = "restdocs-api-spec-gradle-plugin"
    }
}


val jacksonVersion: String by extra
val junitVersion: String by extra

val jacocoRuntime by configurations.creating

dependencies {
    compileOnly(gradleKotlinDsl())

    compile(kotlin("gradle-plugin"))
    compile(kotlin("stdlib-jdk8"))

    implementation(project(":restdocs-api-spec-openapi-generator"))
    implementation(project(":restdocs-api-spec-openapi3-generator"))
    implementation(project(":restdocs-api-spec-postman-generator"))
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit-pioneer:junit-pioneer:0.3.0")
    testImplementation("org.assertj:assertj-core:3.10.0")

    testImplementation("com.jayway.jsonpath:json-path:2.4.0")

    testCompile(gradleTestKit())

    jacocoRuntime("org.jacoco:org.jacoco.agent:0.8.2:runtime")
}

// generate gradle properties file with jacoco agent configured
// see https://discuss.gradle.org/t/testkit-jacoco-coverage/18792
val createTestKitFiles by tasks.creating {
    val outputDir = project.file("$buildDir/testkit")

    inputs.files(jacocoRuntime)
    outputs.dir(outputDir)

    doLast {
        outputDir.mkdirs()
        file("$outputDir/testkit-gradle.properties").writeText("org.gradle.jvmargs=-javaagent:${jacocoRuntime.asPath}=destfile=$buildDir/jacoco/test.exec")
    }
}

tasks["test"].dependsOn(createTestKitFiles)
