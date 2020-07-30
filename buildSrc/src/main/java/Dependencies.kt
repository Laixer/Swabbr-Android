object ApplicationId {
    const val id = "com.laixer.swabbr"
}

object Modules {
    const val app = ":app"

    const val cache = ":common:cache"
    const val network = ":common:network"
    const val presentation = ":common:presentation"

    const val liveVideoBroadcasterSDK = ":liveVideoBroadcasterSDK"
}

object Releases {
    const val versionCode = 1
    const val versionName = "1.0"
}

object Versions {
    const val gradle = "4.0.0-beta04"

    const val compileSdk = 29
    const val minSdk = 26
    const val buildTools = "29.0.3"

    const val targetSdk = 29

    const val googleServices = "4.3.3"

    const val firebaseAnalytics = "17.2.2"
    const val firebaseMessaging = "20.1.0"
    const val firebaseIid = "20.0.2"

    const val fabric = "1.31.2"

    const val androidx = "1.1.0"
    const val cardview = "1.0.0"

    const val ktx = "1.2.0"
    const val fragment_ktx = "1.2.4"

    const val material = "1.1.0"

    const val nav = "2.3.0-alpha01"
    const val kotlin = "1.3.72"
    const val timber = "4.7.1"
    const val rxjava = "2.2.17"
    const val rxkotlin = "2.4.0"
    const val retrofit = "2.8.1"
    const val okhttp = "3.14.7"
    const val glide = "4.11.0"
    const val rxpaper = "1.4.0"
    const val paperdb = "2.6"
    const val moshi = "1.8.0"
    const val lifecycle = "2.2.0"
    const val lifecycletesting = "2.1.0"
    const val workmanager = "2.3.1"
    const val leakCanary = "2.2"
    const val crashlytics = "2.10.1"
    const val koin = "2.0.1"

    const val playCore = "1.6.4"

    const val junit = "4.13"
    const val assertjCore = "3.15.0"
    const val mockitoKotlin = "2.1.0"
    const val mockitoInline = "3.2.4"

    const val exoplayer = "2.10.5"
    const val camera = "1.0.0-beta03"
    const val swiperefreshlayout = "1.0.0"
    const val viewpager2 = "1.0.0"
    const val constraintlayout = "1.1.3"
    const val annotation = "1.1.0"
    const val circleImageView = "3.1.0"
    const val imagePicker = "1.7.2"
    const val rtmpClient = "3.1.0"
}

object Libraries {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"

    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val rxjava = "io.reactivex.rxjava2:rxjava:${Versions.rxjava}"
    const val rxkotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxkotlin}"
    const val rxjavaAdapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"

    const val rxpaper = "com.github.pakoito:RxPaper2:${Versions.rxpaper}"
    const val paperdb = "io.paperdb:paperdb:${Versions.paperdb}"
    const val moshi = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
    const val moshiAdapters = "com.squareup.moshi:moshi-adapters:${Versions.moshi}"
    const val moshiConverter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
    const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
    const val lifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}"
    const val workmanager = "androidx.work:work-runtime-ktx:${Versions.workmanager}"
    const val workmanagerRxjava = "androidx.work:work-rxjava2:${Versions.workmanager}"

    const val koinAndroid = "org.koin:koin-android:${Versions.koin}"
    const val koinViewModel = "org.koin:koin-androidx-viewmodel:${Versions.koin}"

    const val imagePicker = "com.github.dhaval2404:imagepicker:${Versions.imagePicker}"
    const val circleImageView = "de.hdodenhof:circleimageview:${Versions.circleImageView}"
    const val rtmpClient = "net.butterflytv.utils:rtmp-client:${Versions.rtmpClient}"
}

object JetpackLibraries {
    const val ktx = "androidx.core:core-ktx:${Versions.ktx}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.androidx}"
    const val cardView = "androidx.cardview:cardview:${Versions.cardview}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.androidx}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}"
    const val fragment = "androidx.fragment:fragment-ktx:${Versions.fragment_ktx}"
    const val viewPager2 = "androidx.viewpager2:viewpager2:${Versions.viewpager2}"
    const val cameraCore = "androidx.camera:camera-core:${Versions.camera}"
    const val camera2 = "androidx.camera:camera-camera2:${Versions.camera}"
    const val swiperefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swiperefreshlayout}"
    const val navFragmentKtx = "androidx.navigation:navigation-fragment-ktx:${Versions.nav}"
    const val navUiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.nav}"
}

object GoogleLibraries {
    const val playCore = "com.google.android.play:core:${Versions.playCore}"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val exoplayer = "com.google.android.exoplayer:exoplayer:${Versions.exoplayer}"
    const val annotation = "androidx.annotation:annotation:${Versions.annotation}"
}

object FirebaseLibraries {
    const val analytics = "com.google.firebase:firebase-analytics:${Versions.firebaseAnalytics}"
    const val messaging = "com.google.firebase:firebase-messaging:${Versions.firebaseMessaging}"
    const val iid = "com.google.firebase:firebase-iid:${Versions.firebaseIid}"
    const val crashlytics = "com.crashlytics.sdk.android:crashlytics:${Versions.crashlytics}"
    const val leakCanaryAndroid = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
}

object TestLibraries {
    const val junit = "junit:junit:${Versions.junit}"
    const val assertjCore = "org.assertj:assertj-core:${Versions.assertjCore}"
    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"
    const val mockitoInline = "org.mockito:mockito-inline:${Versions.mockitoInline}"
    const val lifecycleTesting = "androidx.arch.core:core-testing:${Versions.lifecycletesting}"
    const val workmanagerTesting = "androidx.work:work-testing:${Versions.workmanager}"
}
