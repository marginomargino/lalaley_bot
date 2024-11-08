plugins {
    application
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
}


allprojects {
    repositories {
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}


dependencies {
    implementation("com.vk.api:sdk:1.0.14")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.1.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.21.0") // Overriding com.vk.api:sdk:1.0.14
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.1")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.10.2")
    implementation("org.mongodb:bson-kotlinx:4.10.2")
    implementation("org.quartz-scheduler:quartz:2.3.2")
}

application {
    mainClass.set("core.MainKt")
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "core.MainKt")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
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
