plugins {
    kotlin("multiplatform") version "1.5.0"
    //id("com.android.application")
}

group = "com.kotme"

repositories {
    mavenCentral()
    mavenLocal()
    google()
}

kotlin {
    jvm()

    js {
        browser {
            binaries.executable()
        }
    }

//    android {
//        compilations.all {
//            kotlinOptions {
//                jvmTarget = "1.8"
//            }
//        }
//    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("app.thelema:thelema:0.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0-RC")
            }
        }

//        val androidMain by getting {
//            dependencies {
//                implementation("app.thelema:thelema-engine-android-debug:0.6.0")
//            }
//        }

        val jvmMain by getting {
            dependencies {
                implementation("app.thelema:thelema-jvm:0.6.0")
                implementation(files("libs/ogg-jvm-lib-0.1.0.jar"))
            }

            val jvmJar by tasks.getting(Jar::class)

            val jarsDir = "$buildDir/jars"

            val copyJars by tasks.registering(Copy::class) {
                dependsOn(jvmJar)
                delete(jarsDir)
                val files = files(jvmJar) + configurations.getByName("jvmRuntimeClasspath")
                from(files)
                into(jarsDir)
            }
            
            // wix toolset is required: https://wixtoolset.org/
            // .net framework 3.5 is required for wix toolset
            val jpackage by tasks.registering(Exec::class) {
                dependsOn(copyJars)
                doFirst {
                    commandLine("C:\\Program Files\\Java\\jdk-16.0.1\\bin\\jpackage",
                        "--type", "\"msi\"",
                        "--input", jarsDir,
                        "--name", "KOTme",
                        "--main-jar", "kotme-jvm.jar",
                        "--main-class", "com.kotme.MainKt",
                        "--java-options", "--enable-preview",
                        "--dest", "$buildDir/jpackage",
                        "--win-dir-chooser",
                        "--win-shortcut"
                    )
                }

/*
jpackage --input target/ \
--name JPackageDemoApp \
--main-jar JPackageDemoApp.jar \
--main-class com.baeldung.java14.jpackagedemoapp.JPackageDemoApp \
--type dmg \
--java-options '--enable-preview'
*/
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("app.thelema:thelema-js:0.6.0")
            }
        }
    }
}

//android {
//    compileSdkVersion(30)
//    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
//    defaultConfig {
//        applicationId = "com.kotme.AndroidMain"
//        minSdkVersion(24)
//        targetSdkVersion(30)
//        versionCode = 1
//        versionName = "1.0"
//    }
//    buildTypes {
//        getByName("release") {
//            isMinifyEnabled = false
//        }
//    }
//    compileOptions {
//        sourceCompatibility = org.gradle.api.JavaVersion.VERSION_1_8
//        targetCompatibility = org.gradle.api.JavaVersion.VERSION_1_8
//    }
//}