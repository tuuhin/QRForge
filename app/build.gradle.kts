import com.android.build.api.dsl.ApkSigningConfig
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.stability.analyzer)
//	google services and crashlytics are applied but only configured in play built
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.koin.compiler)
}

android {
    namespace = "com.sam.qrforge"

    compileSdk {
        val majorVersion = libs.versions.android.compilesdk.major.get().toInt()
        version = release(majorVersion) {
            minorApiLevel = libs.versions.android.compilesdk.revision.get().toInt()
        }
    }

    defaultConfig {
        applicationId = "com.sam.qrforge"
        minSdk = libs.versions.android.minsdk.get().toInt()
        targetSdk = libs.versions.android.targetsdk.get().toInt()
        versionCode = 8
        versionName = "1.0.2"

        testInstrumentationRunner = "com.sam.qrforge.runtime.InstrumentTestRunner"
    }

    signingConfigs {
        // find if there is a properties file
        createReleaseConfig()
    }

    flavorDimensions += "distribution"

    productFlavors {
        create("play") {
            dimension = "distribution"
            applicationIdSuffix = ".play"
        }
        create("oss") {
            dimension = "distribution"
            applicationIdSuffix = ".oss"
            isDefault = true
        }
    }


    buildTypes {

        debug {
            applicationIdSuffix = ".debug"
            // don't send the mapping file as the code is not minified
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
        }

        release {
            optimization {
                enable = true
            }
            // change the signing config if release is not found
            signingConfig = signingConfigs.findByName("release")
            // send the mapping file too to get the crash report
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    sourceSets {
        getByName("androidTest").assets.directories.add("$projectDir/schemas")
    }
}

composeCompiler {
    val compilerReportDirectory = project.layout.buildDirectory.dir("compose_compiler")
    reportsDestination.set(compilerReportDirectory)
    metricsDestination.set(compilerReportDirectory)

    stabilityConfigurationFiles.add(
        rootProject.layout.projectDirectory.file("stability_config.conf"),
    )
}

kotlin {
    compilerOptions {
        optIn.add("androidx.compose.material3.ExperimentalMaterial3ExpressiveApi")
    }
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

koinCompiler {
    userLogs = true
}

dependencies {

    implementation(libs.androidx.compose.adaptive.navigation3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    // compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)
    // lifecyle and navigation
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.bundles.navigation3)
    //koin
    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)
    //room
    implementation(libs.bundles.androidx.room)
    ksp(libs.androidx.room.compiler)
    // camerax
    implementation(libs.bundles.androidx.camerax)
    // kotlinx
    implementation(libs.bundles.kotlinx)
    // others
    implementation(libs.gms.play.services.location)
    implementation(libs.zxing.core)
    implementation(libs.androidx.concurrent.futures.ktx)
    implementation(libs.barcode.scanning)
    implementation(libs.accompanist.permissions)
    //tests
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)

    // firebase
    "playImplementation"(platform(libs.firebase.bom))
    "playImplementation"(libs.firebase.analytics)
    "playImplementation"(libs.firebase.crashlytics)

    // android tests
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.kotlin.test.junit)
    androidTestImplementation(libs.assertk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.intents)
    // koin testing
    androidTestImplementation(platform(libs.koin.bom))
    androidTestImplementation(libs.bundles.koin.testing)
    //room testing
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

fun NamedDomainObjectContainer<out ApkSigningConfig>.createReleaseConfig() {
    val keySecretFile = project.file("keystore.properties")

    var storeFileName: String? = null
    var keyAlias: String? = null
    var keyPassword: String? = null
    var storePassword: String? = null

    // first check if keystore exists in the root project
    if (keySecretFile.exists()) {
        val properties = Properties()
        keySecretFile.inputStream().use { properties.load(it) }
        storeFileName = properties.getProperty("STORE_FILE_NAME")
        keyAlias = properties.getProperty("KEY_ALIAS")
        keyPassword = properties.getProperty("KEY_PASSWORD")
        storePassword = properties.getProperty("STORE_PASSWORD")
    }

    // if not found fallback to env
    if (storeFileName.isNullOrBlank()) storeFileName = System.getenv("STORE_FILE_NAME")
    if (keyAlias.isNullOrBlank()) keyAlias = System.getenv("KEY_ALIAS")
    if (keyPassword.isNullOrBlank()) keyPassword = System.getenv("KEY_PASSWORD")
    if (storePassword.isNullOrBlank()) storePassword = System.getenv("STORE_PASSWORD")

    // finally search for the jks file
    val jksFile = storeFileName?.takeIf { it.isNotBlank() }?.let(project::file)

    val propertiesOk =
        !keyAlias.isNullOrBlank() && !keyPassword.isNullOrBlank() && !storePassword.isNullOrBlank()
    val isJskFileOk = jksFile?.exists() == true

    if (!propertiesOk || !isJskFileOk) return

    // Create the release config
    create("release") {
        storeFile = jksFile
        this.keyAlias = keyAlias
        this.keyPassword = keyPassword
        this.storePassword = storePassword
    }
}
