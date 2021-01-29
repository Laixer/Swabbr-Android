package com.laixer.swabbr.presentation.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import com.laixer.swabbr.R
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

// TODO This is responsible for too many things.
/**
 *  UI component which displays how long someone is recording
 *  a VOD. This also manages timer events for progress bars,
 *  see [events].
 */
class TimerView(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {

    var mTimerStart = 0L
    private var mTimerThread: ScheduledExecutorService? = null

    /**
     *  List of objects containing a <minute, second> pair
     *  versus a function which can execute a [Unit].
     */
    private var events = ArrayList<Pair<Pair<Int, Int>, () -> Unit>>()

    /**
     *  List of progress bars which will be affected by this
     *  timer and its registered events.
     */
    private var progressBarList: ArrayList<Pair<ProgressBar, () -> Unit>> = ArrayList()

    interface TimerProvider {

        fun getTimecode(): Long
        fun getDuration(): Long
    }

    private lateinit var mTimerProvider: TimerProvider
    private var mDefaultTimerProvider: TimerProvider

    fun setTimerProvider(timerProvider: TimerProvider) {
        mTimerProvider = timerProvider
    }

    constructor(context: Context) : this(context, null)

    init {
        mDefaultTimerProvider = object : TimerProvider {
            override fun getTimecode(): Long = System.currentTimeMillis() - mTimerStart
            override fun getDuration(): Long = -1L
        }
    }

    fun addProgressBar(progressBar: ProgressBar, onFinish: () -> Unit) {
        progressBarList.add(Pair(progressBar, onFinish))
    }

    /**
     *  Starts the timer. All progress bars will have their progress set to
     *  zero, after which this timer will update it periodically. Whenever
     *  a registered event in [events] should be triggered this function
     *  will do so.
     */
    @Synchronized
    fun startTimer(refreshInterval: Long = DEFAULT_REFRESH_INTERVAL) {
        if (mTimerThread != null) return
        if (!::mTimerProvider.isInitialized) mTimerProvider = mDefaultTimerProvider

        progressBarList.map { it.first.progress = 0 }

        text = context.resources.getString(R.string.zero_time)

        mTimerStart = System.currentTimeMillis()
        mTimerThread = Executors.newSingleThreadScheduledExecutor()

        /**
         *  For each timer tick this function checks if we have an
         *  [events] entry which corresponds to the current minute
         *  and second of time. If an [events] entry matches, the
         *  corresponding [Unit] function is executed.
         */
        mTimerThread?.scheduleWithFixedDelay({
            Handler(Looper.getMainLooper()).post {
                val timecodeMs = mTimerProvider.getTimecode()
                val timecodeTotalSeconds = timecodeMs / MILLISECONDS_PER_SECOND
                val minutes = ((timecodeTotalSeconds / SECONDS_PER_MINUTE) % SECONDS_PER_MINUTE).toInt()
                val seconds = (timecodeTotalSeconds % SECONDS_PER_MINUTE).toInt()

                text = context.getString(R.string.timer_value, minutes, seconds)

                // Only update non-max progress bars.
                progressBarList
                    .filter { it.first.progress < it.first.max }
                    .map {
                    it.first.progress = timecodeMs.toInt() / 100
                    if (it.first.progress >= it.first.max) {
                        it.second()
                    }
                }

                events.filter { it.first.first == minutes && it.first.second == seconds }
                    .map { it.second() /** Second parameter in the pair, being the [Unit] */ }
            }
        }, refreshInterval, refreshInterval, TimeUnit.MILLISECONDS)

        visibility = View.VISIBLE
    }

    /**
     *  Clears all registered timer events.
     */
    @Synchronized
    fun resetEvents() {
        events = ArrayList()
    }

    /**
     *  Add an event to be triggered after a certain amount of time
     *  after calling [startTimer].
     *
     *  @param minute Amount of minutes before execution.
     *  @param second Amount of seconds before execution.
     *  @param func What to execute.
     */
    @Synchronized
    fun addEventAt(minute: Int, second: Int, func: () -> Unit) {
        events.add((Pair(Pair(minute, second), func)))
    }

    @Synchronized
    fun removeEventAt(minute: Int, second: Int) {
        events = events.filter { it.first.first != minute && it.first.first != second } as ArrayList
    }

    /**
     *  Stops the timer and removes all registered events.
     *
     *  @param resetEvents If set to false this timer can be re-used.
     */
    @Synchronized
    fun stopTimer(resetEvents: Boolean = true) {
        if (mTimerThread == null) {
            return
        }

        if (resetEvents) {
            resetEvents()
        }

        mTimerThread?.shutdown()
        mTimerThread = null

        visibility = View.INVISIBLE
        text = context.resources.getString(R.string.zero_time)
    }

    @Synchronized
    fun isRunning(): Boolean = mTimerThread != null

    companion object {

        const val DEFAULT_REFRESH_INTERVAL = 100L
        const val MILLISECONDS_PER_SECOND = 1000L
        const val SECONDS_PER_MINUTE = 60
    }
}
