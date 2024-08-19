val myImplementation: String
    get() {
        TODO()
    }

plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.madiotech"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.madiotech"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}



dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    implementation(files("C:\\Program Files\\Android\\gradle-8.5-all\\gradle-8.5\\lib\\retrofit-2.9.0.jar"))
//    implementation(files("C:\\Program Files\\Android\\gradle-8.5-all\\gradle-8.5\\lib\\security-crypto-1.1.0-alpha06.aar"))
//    implementation(files("C:\\Program Files\\Android\\gradle-8.5-all\\gradle-8.5\\lib\\gson-2.10.1.jar"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
