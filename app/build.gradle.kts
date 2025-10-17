import com.android.build.api.variant.ResValue
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.ksp)
	alias(libs.plugins.kotlinx.serialization)
	alias(libs.plugins.androidx.room)
//	google services and crashlytics are applied but only configured in play built
	alias(libs.plugins.firebase.crashlytics)
	alias(libs.plugins.google.services)
}

android {
	namespace = "com.sam.qrforge"
	compileSdk = 36

	defaultConfig {
		applicationId = "com.sam.qrforge"
		minSdk = 26
		targetSdk = 36
		versionCode = 7
		versionName = "1.0.1"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	signingConfigs {
		// find if there is a properties file
		val keySecretFile = rootProject.file("keystore.properties")
		if (!keySecretFile.exists()) return@signingConfigs

		// load the properties
		val properties = Properties()
		keySecretFile.inputStream().use { properties.load(it) }

		val userHome = System.getProperty("user.home")
		val storeFileName = properties.getProperty("STORE_FILE_NAME")

		val keyStoreFolder = File(userHome, "keystore")
		if (!keyStoreFolder.exists()) return@signingConfigs

		val keyStoreFile = File(keyStoreFolder, storeFileName)
		if (!keyStoreFile.exists()) return@signingConfigs

		create("release") {
			storeFile = keyStoreFile
			keyAlias = properties.getProperty("KEY_ALIAS")
			keyPassword = properties.getProperty("KEY_PASSWORD")
			storePassword = properties.getProperty("STORE_PASSWORD")
		}
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
			isMinifyEnabled = true
			isShrinkResources = true
			multiDexEnabled = true
			// change the signing config if release is not found
			signingConfig = signingConfigs.findByName("release")
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
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
}

androidComponents.onVariants { variant ->
	val buildType = variant.buildType
	val buildFlavour = variant.flavorName
	// no changes for the play release
	if (buildType == "release" && buildFlavour == "play") return@onVariants
	// only changes for oss release
	val flavourNameReadable = if (buildFlavour == "play") "Play" else "OSS"
	val appName = "QR Forge $flavourNameReadable" + if (buildType != "release") " (Debug) " else ""

	val key = object : ResValue.Key {
		override val name: String = "app_name"
		override val type: String = "string"
	}
	variant.resValues.put(key, ResValue(appName.trim()))
}

composeCompiler {
//	featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
	val compilerReportDirectory = project.layout.buildDirectory.dir("compose_compiler")
	reportsDestination.set(compilerReportDirectory)
	metricsDestination.set(compilerReportDirectory)

	stabilityConfigurationFiles.add(
		rootProject.layout.projectDirectory.file("stability_config.conf")
	)
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_17
	}
}

room {
	schemaDirectory("$projectDir/schemas")
}

dependencies {

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.core.splashscreen)
	implementation(libs.androidx.activity.compose)
	// compose
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.bundles.compose.ui)
	// lifecyle and navigation
	implementation(libs.bundles.androidx.lifecycle)
	implementation(libs.androidx.navigation.compose)
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
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.room.testing)
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}