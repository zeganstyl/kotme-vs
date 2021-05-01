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

    //android()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("app.thelema:thelema-engine:0.6.0")
            }
        }

//        val androidMain by getting {
//            dependencies {
//                implementation("app.thelema:thelema-engine-android-debug:0.6.0")
//            }
//        }

        val jvmMain by getting {
            dependencies {
                implementation("app.thelema:thelema-engine-jvm:0.6.0")
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
//}