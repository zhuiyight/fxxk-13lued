plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 34

    namespace = "icu.fuck.blued"

    defaultConfig {
        applicationId = "icu.fuck.blued"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = "1.0.0-alpha.2"
    }

    buildTypes {
        named("release") {
            // 以下两项务必保持false，否则编译代码优化会移除掉模块定义
            isShrinkResources = false
            isMinifyEnabled = false
            proguardFiles("proguard-rules.pro")
        }
    }

    androidResources {
        additionalParameters += arrayOf("--allow-reserved-package-id", "--package-id", "0x45")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    implementation("com.github.kyuubiran:EzXHelper:2.0.8")
    compileOnly("de.robv.android.xposed:api:82")
    implementation("com.google.code.gson:gson:2.10.1")
}

dependencies {
    val collectionVersion = "1.4.3"
    implementation("androidx.collection:collection:$collectionVersion")
}

dependencies {
    val fragmentVersion = "1.8.3"
    // Java language implementation
    implementation("androidx.fragment:fragment:$fragmentVersion")
    // Kotlin
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")
    // Compose
    implementation("androidx.fragment:fragment-compose:$fragmentVersion")
    // Testing Fragments in Isolation
    debugImplementation("androidx.fragment:fragment-testing:$fragmentVersion")
}
