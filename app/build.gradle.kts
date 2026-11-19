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

        // 面板地址与凭据（CI 里用 Secrets 注入；未配置时回退到默认值）
        // 注意：GitHub Actions 未设置的 secret 会传成空字符串（不是 null），
        // 所以这里必须用 isNullOrBlank 判断，否则会编译出空账号密码 → 打开一片空白
        fun envOr(key: String, fallback: String): String {
            val v = System.getenv(key)
            return if (v.isNullOrBlank()) fallback else v.trim()
        }
        val panelUrl = envOr("PANEL_URL", "https://blog.jermainli.world/panel/")
        val panelUser = envOr("PANEL_USER", "jermain")
        val panelPass = envOr("PANEL_PASS", "123456")
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
