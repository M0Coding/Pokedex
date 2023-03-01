import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import com.mocoding.pokedex.Configuration
import com.mocoding.pokedex.Deps

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("kotlin-parcelize")
    id("app.cash.sqldelight")
}

@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
kotlin {
    android()
    ios()
    iosSimulatorArm64()

    cocoapods {
        summary = "Pokedex the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../ios/Podfile")
        framework {
            baseName = "shared"

            // Optional properties
            // Specify the framework linking type. It's dynamic by default.
            isStatic = true
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Compose
                with(compose) {
                    api(runtime)
                    api(foundation)
                    api(material)
//                    api(material3)
                }

                // Ktor
                with(Deps.Io.Ktor) {
                    api(ktorClientCore)
                    api(ktorSerializationKotlinxJson)
                    api(ktorClientContentNegotiation)
                }

                // SqlDelight
                implementation(Deps.CashApp.SQLDelight.coroutinesExtensions)

                // Koin
                with(Deps.Koin) {
                    api(core)
                    api(test)
                }

                // KotlinX Serialization
                implementation(Deps.Org.JetBrains.Kotlinx.kotlinxSerializationJson)

                // Coroutines
                implementation(Deps.Org.JetBrains.Kotlinx.coroutinesCore)

                // MVIKotlin
                with(Deps.ArkIvanov.MVIKotlin) {
                    api(mvikotlin)
                    api(mvikotlinMain)
                    api(mvikotlinExtensionsCoroutines)
                }

                // Decompose
                with(Deps.ArkIvanov.Decompose) {
                    api(decompose)
                    api(extensionsCompose)
                }
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                // Ktor
                implementation(Deps.Io.Ktor.ktorClientAndroid)
                implementation(Deps.Io.Ktor.ktorClientOkhttp)

                // SqlDelight
                implementation(Deps.CashApp.SQLDelight.androidDriver)
            }
        }
        val androidUnitTest by getting

        val iosMain by getting {
            dependsOn(commonMain)

            dependencies {
                // Ktor
                with(Deps.Io.Ktor) {
                    api(ktorClientDarwin)
                }

                // SqlDelight
                implementation(Deps.CashApp.SQLDelight.nativeDriver)
            }
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosTest by getting {
            dependsOn(commonTest)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
    }
}



android {
    namespace = "com.mocoding.pokedex"
    compileSdk = Configuration.compileSdk
    defaultConfig {
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.mocoding.pokedex.core.database")
        }
    }
}