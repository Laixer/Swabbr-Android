import kotlin.String
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

/**
 * Generated by https://github.com/jmfayard/buildSrcVersions
 *
 * Find which updates are available by running
 *     `$ ./gradlew buildSrcVersions`
 * This will only update the comments.
 *
 * YOU are responsible for updating manually the dependency version.
 */
object Versions {
  const val io_gitlab_arturbosch_detekt: String = "1.12.0"

  const val com_github_bumptech_glide: String = "4.11.0"

  const val com_squareup_retrofit2: String = "2.9.0"

  const val com_squareup_okhttp3: String = "4.9.0"

  const val org_jetbrains_kotlin: String = "1.3.72"

  const val androidx_navigation: String = "2.3.0"

  const val androidx_lifecycle: String = "2.2.0"

  const val com_squareup_moshi: String = "1.11.0"

  const val androidx_work: String = "2.4.0"

  const val org_koin: String = "2.1.6"

  const val com_android_tools_build_gradle: String = "4.1.0"

  const val de_fayard_buildsrcversions_gradle_plugin: String = "0.7.0"

  const val runtime_permission_kotlin: String = "1.1.2"

  const val leakcanary_android: String = "2.5"

  const val swiperefreshlayout: String = "1.1.0"

  const val constraintlayout: String = "2.0.2"

  const val circleimageview: String = "3.1.0"

  const val google_services: String = "4.3.4"

  const val camera_camera2: String = "1.0.0-beta03"

  const val mockito_inline: String = "3.5.13"

  const val mockito_kotlin: String = "2.2.0"

  const val espresso_core: String = "3.3.0"

  const val assertj_core: String = "3.17.2"

  const val core_testing: String = "2.1.0"

  const val fragment_ktx: String = "1.2.5"

  const val crashlytics: String = "2.3.0"

  const val imagepicker: String = "1.7.5"

  const val lint_gradle: String = "27.0.2"

  const val rtmp_client: String = "3.1.0"

  const val annotation: String = "1.1.0"

  const val leonidslib: String = "1.3.2"

  const val viewpager2: String = "1.0.0"

  const val appcompat: String = "1.2.0"

  const val exoplayer: String = "2.12.0"

  const val jwtdecode: String = "2.0.0"

  const val core_ktx: String = "1.5.0-alpha04"

  const val material: String = "1.2.1"

  const val rxkotlin: String = "2.4.0"

  const val rxpaper2: String = "1.5.0"

  const val rxjava: String = "2.2.20"

  const val aapt2: String = "4.1.0"

  const val junit: String = "4.13"

  /**
   * Current version: "6.6.1"
   * See issue 19: How to update Gradle itself?
   * https://github.com/jmfayard/buildSrcVersions/issues/19
   */
  const val gradleLatestVersion: String = "6.6.1"
}

/**
 * See issue #47: how to update buildSrcVersions itself
 * https://github.com/jmfayard/buildSrcVersions/issues/47
 */
val PluginDependenciesSpec.buildSrcVersions: PluginDependencySpec
  inline get() =
      id("de.fayard.buildSrcVersions").version(Versions.de_fayard_buildsrcversions_gradle_plugin)