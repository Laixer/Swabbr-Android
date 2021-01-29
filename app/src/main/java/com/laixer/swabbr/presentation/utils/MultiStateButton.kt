package com.laixer.swabbr.presentation.utils

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.laixer.swabbr.R

/**
 *  UI component for a button which contains an on and and off
 *  state. The drawable of the button depends on the state.
 */
class MultiStateButton(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {
    constructor(context: Context) : this(context, null)

    private lateinit var mOnDrawable: Drawable
    private lateinit var mOffDrawable: Drawable
    private var mPressedColor: Int = 0
    private var mDisabledColor: Int = 0
    var isOn: Boolean = false
        set(value) {
            field = value
            setImageDrawable(if (value) mOnDrawable else mOffDrawable)
            updateColorFilter(isEnabled)
        }

    init {
        mDisabledColor = context.getColor(R.color.multiStateButtonDisabled)
        val a: TypedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.MultiStateButton, 0, 0
        )

        try {
            val isOn = a.getBoolean(R.styleable.MultiStateButton_isOn, true)
            val offDrawable = a.getDrawable(R.styleable.MultiStateButton_offSrc)
            val pressedColor = a.getColor(
                R.styleable.MultiStateButton_pressedColor, context.getColor(R.color.multiStateButtonPressed)
            )

            init(isOn, offDrawable, pressedColor)
        } finally {
            a.recycle()
        }
    }

    fun init(isOn: Boolean, offDrawable: Drawable?, pressedColor: Int) {
        mOnDrawable = drawable
        mOffDrawable = offDrawable ?: mOnDrawable
        mPressedColor = pressedColor

        setOnTouchListener(object : OnTouchListener {
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                val imageView = view as ImageView
                if (!imageView.isClickable) return false

                if (event?.action == MotionEvent.ACTION_DOWN) {
                    imageView.drawable.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        mPressedColor, BlendModeCompat.SRC_IN
                    )
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    imageView.drawable.clearColorFilter()
                }
                return view.performClick()
            }
        })

        this.isOn = isOn
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        this.isClickable = enabled
        updateColorFilter(enabled)
    }

    fun toggleState(): Boolean {
        isOn = !isOn
        return isOn
    }

    fun updateColorFilter(enabled: Boolean) {
        if (enabled) {
            drawable.clearColorFilter()
        } else {
            drawable.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                mDisabledColor, BlendModeCompat.SRC_IN
            )
        }
        invalidate()
        requestLayout()
    }
}
