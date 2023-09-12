plugins {
    application
    kotlin("jvm") version "1.9.10"
}


allprojects {
    repositories {
        mavenCentral()
    }
}

dependencies {
    implementation("com.vk.api:sdk:1.0.14")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.10.2")
}

application {
    mainClass.set("core.Main")
}

kotlin.target.compilations.all {
    kotlinOptions {
        jvmTarget = "17"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

sourceSets {
    getByName("main") {
        java.setSrcDirs(emptyList<File>())
        kotlin.setSrcDirs(listOf("sources"))
        resources.setSrcDirs(listOf("resources"))
    }

    getByName("test") {
        java.setSrcDirs(emptyList<File>())
        kotlin.setSrcDirs(listOf("tests"))
        resources.setSrcDirs(listOf("test resources"))
    }
}
