plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.bioprep"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.bioprep"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    // Lab 08 Topic 6: implementation "androidx.constraintlayout:constraintlayout:2.2.0"
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    testImplementation("org.json:json:20240303")
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}