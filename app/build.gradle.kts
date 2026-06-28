import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.apollo)
    alias(libs.plugins.googleServices)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

val shopifyApiKey = localProperties.getProperty("SHOPIFY_API_KEY") ?: ""


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

        buildConfigField("String", "SHOPIFY_API_KEY", "\"$shopifyApiKey\"")
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
        compose = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(project(":ui_kit"))

    // --- New Dependencies ---

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.core:core:1.15.0")

    // Koin
    implementation(libs.koin.androidx.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Apollo Client (GraphQL)
    implementation(libs.apollo.runtime)

    // DataStore Preferences
    implementation(libs.datastore.preferences)

    // Firebase Auth (via BOM)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)

    // Coroutines
    implementation(libs.coroutines.android)

    // Material Icons Extended
    implementation(libs.compose.material.icons.extended)
}

apollo {
    service("storefront") {
        packageName.set("com.example.qafilah.graphql.storefront")
        srcDir("src/main/graphql/storefront")

        introspection {
            endpointUrl.set("https://mad46-and8.myshopify.com/api/2024-01/graphql.json")
            schemaFile.set(file("src/main/graphql/storefront/schema.graphqls"))
            headers.put("X-Shopify-Storefront-Access-Token", shopifyApiKey)
        }
    }
}
