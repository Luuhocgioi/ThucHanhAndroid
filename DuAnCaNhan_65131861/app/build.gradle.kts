plugins {
    alias(libs.plugins.android.application)
    // Đã sửa lỗi cú pháp: Dùng id() thay vì id '...' cho Kotlin DSL
    id("com.google.gms.google-services")
}

android {
    namespace = "clc65.hoangluu.duancanhan_65131861"
    // Giữ nguyên các cấu hình của bạn
    compileSdk = 36

    defaultConfig {
        applicationId = "clc65.hoangluu.duancanhan_65131861"
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
        // Đảm bảo tương thích với Java 11 (phiên bản bạn đã khai báo)
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // ĐÃ SỬA LỖI: Chuyển từ Groovy syntax ('...') sang Kotlin DSL function call syntax ("...")

    // 1. Thư viện cơ bản của AndroidX (Giữ nguyên phiên bản)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // 2. Thư viện Firebase
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-database:20.3.1")

    // 3. Thư viện hỗ trợ UI (RecyclerView và CardView)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // 4. Thư viện tải ảnh (Picasso giúp tải ảnh sản phẩm từ Firebase Storage/URL)
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation(libs.activity)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.fragment)

    // 5. Thư viện Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // LƯU Ý: Tôi đã xóa các dòng khai báo lặp lại như implementation(libs.appcompat)
    // và giữ lại các khai báo string literal để đảm bảo tính nhất quán và rõ ràng về phiên bản.
}