import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.ksp)
	alias(libs.plugins.kotlinx.serialization)
	alias(libs.plugins.androidx.room)
}

android {
	namespace = "com.sam.qrforge"
	compileSdk = 36

	defaultConfig {
		applicationId = "com.sam.qrforge"
		minSdk = 26
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
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	buildFeatures {
		compose = true
		buildConfig = true
	}
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
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)
	// lifecyle and navigation
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.lifecycle.runtime.compose)
	implementation(libs.androidx.navigation.compose)
	//koin
	implementation(platform(libs.koin.bom))
	implementation(libs.koin.core)
	implementation(libs.koin.android)
	implementation(libs.koin.compose)
	implementation(libs.koin.compose.navigation)
	implementation(libs.koin.android.startup)
	//room
	implementation(libs.androidx.room.ktx)
	implementation(libs.androidx.room.runtime)
	implementation(libs.gms.play.services.location)
	ksp(libs.androidx.room.compiler)
	// others
	implementation(libs.androidx.material.icons.extended)
	implementation(libs.zxing.core)
	implementation(libs.androidx.ui.text.google.fonts)
	implementation(libs.androidx.core.splashscreen)
	implementation(libs.kotlinx.datetime)
	implementation(libs.kotlinx.collections.immutable)
	// camerax
	implementation(libs.androidx.camera.camera2)
	implementation(libs.androidx.camera.lifecycle)
	implementation(libs.androidx.camera.mlkit.vision)
	//tests
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.room.testing)
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}