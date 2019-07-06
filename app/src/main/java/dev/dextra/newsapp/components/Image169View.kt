package dev.dextra.newsapp.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class Image169View @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val ratio = RATIO_HEIGHT / RATIO_WIDTH

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(measuredWidth, (measuredWidth / ratio).toInt())
    }

    companion object {
        private const val RATIO_HEIGHT = 16f
        private const val RATIO_WIDTH = 9f
    }
}
