package io.antmedia.android.broadcaster.utils

import android.content.Context
import android.content.SharedPreferences
import java.text.DecimalFormat

object Utils {

    private const val APP_SHARED_PREFERENCES = "applicationDetails"
    private val DOES_ENCODER_WORKS = "${Utils::class.java.name}.DOES_ENCODER_WORKS"

    const val ENCODER_NOT_TESTED = -1
    const val ENCODER_WORKS = 1
    const val ENCODER_NOT_WORKS = 0

    //public static final String SHARED_PREFERENCE_FIRST_INSTALLATION="FIRST_INSTALLATION";
    private var sharedPreference: SharedPreferences? = null

    fun getDurationString(seconds: Int): String {
        var seconds = seconds
        if (seconds < 0 || seconds > 2000000) //there is an codec problem and duration is not set correctly,so display meaningfull string
            seconds = 0
        val hours = seconds / 3600
        val minutes = seconds % 3600 / 60
        seconds %= 60
        return if (hours == 0) "${twoDigitString(minutes)}:${twoDigitString(seconds)}"
        else "${twoDigitString(hours)}:${twoDigitString(minutes)}:${twoDigitString(seconds)}"
    }

    private fun twoDigitString(number: Int): String = DecimalFormat("00").format(number)

    private fun getDefaultSharedPreferences(context: Context): SharedPreferences =
        sharedPreference ?: context.getSharedPreferences(
            APP_SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )

    fun doesEncoderWork(context: Context): Int = getDefaultSharedPreferences(context).getInt(
        DOES_ENCODER_WORKS,
        ENCODER_NOT_TESTED
    )

    fun setEncoderWorks(context: Context, works: Boolean) = getDefaultSharedPreferences(context).edit().putInt(
        DOES_ENCODER_WORKS,
        if (works) ENCODER_WORKS else ENCODER_NOT_WORKS
    ).apply()
}
