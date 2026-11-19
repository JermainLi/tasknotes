plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "world.jermainli.taskpanel"
    compileSdk = 34

    defaultConfig {
        applicationId = "world.jermainli.taskpanel"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // 面板地址（可在 CI 里用环境变量覆盖）
        val panelUrl = System.getenv("PANEL_URL") ?: "https://blog.jermainli.world/panel/"
        val panelUser = System.getenv("PANEL_USER") ?: "jermain"
        val panelPass = System.getenv("PANEL_PASS") ?: "123456"
        buildConfigField("String", "PANEL_URL", "\"$panelUrl\"")
        buildConfigField("String", "PANEL_USER", "\"$panelUser\"")
        buildConfigField("String", "PANEL_PASS", "\"$panelPass\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.webkit:webkit:1.11.0")
}
