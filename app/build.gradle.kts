import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.apollo)
    alias(libs.plugins.googleServices)
}

// Load local properties safely
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

val shopifyApiKey = localProperties.getProperty("SHOPIFY_API_KEY") ?: ""
val exchangeRateApiKey = localProperties.getProperty("EXCHANGE_RATE_API_KEY") ?: ""
val adminApiKey = localProperties.getProperty("ADMIN_API_KEY") ?: ""
val storefrontEndpoint = localProperties.getProperty("STOREFRONT_ENDPOINT") ?: ""
val adminEndpoint = localProperties.getProperty("ADMIN_ENDPOINT") ?: ""
val paymobPublicKey = localProperties.getProperty("PAYMOB_PUBLIC_KEY") ?: ""
val paymobSecretKey = localProperties.getProperty("PAYMOB_SECRET_KEY")?:""

android {
    namespace = "com.example.qafilah"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "com.example.qafilah"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inject variables securely into BuildConfig
        buildConfigField("String", "SHOPIFY_API_KEY", "\"$shopifyApiKey\"")
        buildConfigField("String", "EXCHANGE_RATE_API_KEY", "\"$exchangeRateApiKey\"")
        buildConfigField("String", "ADMIN_API_KEY", "\"$adminApiKey\"")
        buildConfigField("String", "STOREFRONT_ENDPOINT", "\"$storefrontEndpoint\"")
        buildConfigField("String", "ADMIN_ENDPOINT", "\"$adminEndpoint\"")
        buildConfigField("String", "PAYMOB_PUBLIC_KEY", "\"$paymobPublicKey\"")
        buildConfigField("String",  "PAYMOB_SECRET_KEY", "\"$paymobSecretKey\"" )
    }

    buildFeatures {
        buildConfig = true
        compose = true
        dataBinding = true

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

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Jetpack Compose & Core Android
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    // Dependency Injection (Koin)
    implementation(libs.koin.android.ext)
    implementation(libs.koin.core.ext)
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.core:core:1.15.0")

    implementation(libs.logging.interceptor)

    // Koin
    implementation(libs.koin.androidx.compose)
    implementation("com.paymob.sdk:Paymob-SDK:1.9.2")
    // Local Storage (Room & DataStore)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.datastore.preferences)

    // Networking & APIs (Retrofit & Apollo GraphQL)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.apollo.runtime)

    // Image Loading
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Security & Authentication
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.tink.android)
    implementation("androidx.credentials:credentials:1.2.2")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Asynchronous Programming
    implementation(libs.coroutines.android)

    // Multi-module project references
    implementation(project(":ui_kit"))

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.lottie.compose)
}

apollo {
    service("storefront") {
        packageName.set("com.example.qafilah.graphql.storefront")
        srcDir("src/main/graphql/storefront")

        introspection {
            endpointUrl.set(storefrontEndpoint)
            schemaFile.set(file("src/main/graphql/storefront/schema.graphqls"))
            headers.put("X-Shopify-Storefront-Access-Token", shopifyApiKey)
        }
    }
    service("admin") {
        packageName.set("com.example.qafilah.graphql.admin")
        srcDir("src/main/graphql/admin")

        introspection {
            endpointUrl.set(adminEndpoint)
            schemaFile.set(file("src/main/graphql/admin/schema.graphqls"))
            headers.put("X-Shopify-Access-Token", adminApiKey)
        }
    }
}