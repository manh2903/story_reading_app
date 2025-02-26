plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.ndm.stotyreading"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ndm.stotyreading"
        minSdk = 24
        targetSdk = 34
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    val nav_version = "2.5.3"
    // navigation component
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    implementation ("androidx.multidex:multidex:2.0.1")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    //bragde
    implementation ("com.nex3z:notification-badge:1.0.4")
    //glider
    implementation ("com.github.bumptech.glide:glide:4.15.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.0")

    implementation("com.squareup.picasso:picasso:2.71828")

    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}