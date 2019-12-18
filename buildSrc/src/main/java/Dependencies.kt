object ApplicationId {
    val id = "com.laixer.core"
}

object Modules {
    val app = ":app"
    val navigation = ":navigation"

    val cache = ":common:cache"
    val network = ":common:network"

    val presentation = ":common:presentation"

    val swabbr = ":swabbr"

    val gocodersdk = "com.wowza.gocoder.sdk.android:com.wowza.gocoder.sdk:2.0.0@aar"
}

object Releases {
    val versionCode = 1
    val versionName = "1.0"
}

object Versions {
    val gradle = "3.4.2"

    val compileSdk = 28
    val minSdk = 24
    val targetSdk = 28

    val googleAuth = "16.0.1"

    val googleServices = "4.3.2"

    val firebaseAuth = "19.0.0"
    val firebaseAnalytics = "17.2.0"
    val firebaseMessaging = "20.0.0"

    val notificationsHub = "0.6@aar"

    val fabric = "1.30.0"

    val androidx = "1.1.0"
    val maps = "15.0.1"

    val ktx = "1.2.0-beta01"
    val material = "1.2.0-alpha01"

    val kotlin = "1.3.61"
    val timber = "4.7.1"
    val rxjava = "2.2.10"
    val rxkotlin = "2.3.0"
    val retrofit = "2.6.0"
    val loggingInterceptor = "4.0.0"
    val glide = "4.9.0"
    val rxpaper = "1.4.0"
    val paperdb = "2.6"
    val moshi = "1.8.0"
    val lifecycle = "2.0.0"
    val workmanager = "2.2.0"
    val leakCanary = "2.0-alpha-2"
    val crashlytics = "2.10.1"
    val koin = "2.0.0-beta-1"

    val playCore = "1.6.1"

    val junit = "4.12"
    val assertjCore = "3.12.2"
    val mockitoKotlin = "2.1.0"
    val mockitoInline = "3.0.0"

    val exoplayer = "2.10.5"
    val camera = "1.0.0-alpha04"
    val swiperefreshlayout = "1.0.0"
    val viewpager2 = "1.0.0-rc01"
}

object Libraries {
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"

    val ktx = "androidx.core:core-ktx:${Versions.ktx}"

    val maps = "com.google.android.gms:play-services-maps:${Versions.maps}"

    val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    val rxjava = "io.reactivex.rxjava2:rxjava:${Versions.rxjava}"
    val rxkotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxkotlin}"

    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val rxjavaAdapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    val moshiConverter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.loggingInterceptor}"

    val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

    val paperdb = "io.paperdb:paperdb:${Versions.paperdb}"
    val rxpaper = "com.github.pakoito:RxPaper2:${Versions.rxpaper}"
    val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"

    val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
    val lifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}"
    val workmanager = "androidx.work:work-runtime-ktx:${Versions.workmanager}"
    val workmanagerRxjava = "androidx.work:work-rxjava2:${Versions.workmanager}"

    val leakCanaryAndroid = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"

    val crashlytics = "com.crashlytics.sdk.android:crashlytics:${Versions.crashlytics}"

    val koinAndroid = "org.koin:koin-android:${Versions.koin}"
    val koinViewModel = "org.koin:koin-androidx-viewmodel:${Versions.koin}"

    val cameraCore = "androidx.camera:camera-core:${Versions.camera}"
    val camera2 = "androidx.camera:camera-camera2:${Versions.camera}"

}

object JetpackLibraries {
    val appcompat = "androidx.appcompat:appcompat:${Versions.androidx}"
    val design = "com.google.android.material:material:${Versions.androidx}"
    val cardview = "androidx.cardview:cardview:${Versions.androidx}"
    val recyclerview = "androidx.recyclerview:recyclerview:${Versions.androidx}"
    val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.androidx}"
    val fragment = "androidx.fragment:fragment-ktx:1.2.0-alpha02"
    val viewpager2 = "androidx.viewpager2:viewpager2:${Versions.viewpager2}"
    val swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swiperefreshlayout}"


}

object GoogleLibraries {
    val auth = "com.google.android.gms:play-services-auth:${Versions.googleAuth}"
    val playCore = "com.google.android.play:core:${Versions.playCore}"
    val material = "com.google.android.material:material:${Versions.material}"
    val exoplayer = "com.google.android.exoplayer:exoplayer:${Versions.exoplayer}"


}

object MicrosoftLibraries {
    val notificationshub = "com.microsoft.azure:notification-hubs-android-sdk:${Versions.notificationsHub}"
}

object FirebaseLibraries {
    val auth = "com.google.firebase:firebase-auth:${Versions.firebaseAuth}"
    val messaging = "com.google.firebase:firebase-messaging:${Versions.firebaseMessaging}"
    val analytics = "com.google.firebase:firebase-analytics:${Versions.firebaseAnalytics}"
}

object TestLibraries {
    val junit = "junit:junit:${Versions.junit}"
    val assertjCore = "org.assertj:assertj-core:${Versions.assertjCore}"
    val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}"
    val mockitoInline = "org.mockito:mockito-inline:${Versions.mockitoInline}"
    val lifecycleTesting = "androidx.arch.core:core-testing:${Versions.lifecycle}"
    val workmanagerTesting = "androidx.work:work-testing:${Versions.workmanager}"
}
