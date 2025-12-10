plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "clc65.hoangluu.duan"
    compileSdk = 36

    defaultConfig {
        applicationId = "clc65.hoangluu.duan"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true // Đảm bảo dòng này được thêm
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase BOM (Đảm bảo tất cả các thư viện dùng chung 1 version)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // 1. Firebase Authentication (Đăng nhập, Đăng ký)
    implementation("com.google.firebase:firebase-auth")

    // 2. Cloud Firestore (Cơ sở dữ liệu NoSQL)
    implementation("com.google.firebase:firebase-firestore")

    // 3. (Tùy chọn) Firebase Storage (Nếu bạn muốn lưu trữ ảnh đồ uống)
    implementation("com.google.firebase:firebase-storage")

    // ------------------------------------------------------------------
    // KHỐI CODE BẠN VỪA THÊM (NÊN ĐẶT TẠI ĐÂY)
    // ------------------------------------------------------------------

    val navVersion = "2.7.5" // Kiểm tra phiên bản mới nhất trên developer.android.com
    // Lưu ý: Do bạn đang code Java, nên dùng navigation-fragment và navigation-ui (không có ktx)
    // Tuy nhiên, nếu bạn vẫn muốn dùng ktx (dùng Java gọi thư viện Kotlin) vẫn được.

    // NAVIGATION COMPONENTS
    implementation("androidx.navigation:navigation-fragment:$navVersion")
    implementation("androidx.navigation:navigation-ui:$navVersion")

    // Material Design (Cho BottomNavigationView và các Widget khác)
    implementation("com.google.android.material:material:1.11.0")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
}