package orc.zdertis420.playlistmaker.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import orc.zdertis420.playlistmaker.R

class PlayButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var imagePlay: Drawable? = null
    private var imagePause: Drawable? = null
    private val viewBounds = RectF()

    var isPlaying: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.PlayButtonView, defStyleAttr, defStyleRes).apply {
            imagePlay = getDrawable(R.styleable.PlayButtonView_image_play)
            imagePause = getDrawable(R.styleable.PlayButtonView_image_pause)
            recycle()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val left = paddingLeft.toFloat()
        val top = paddingTop.toFloat()
        val right = (w - paddingRight).toFloat()
        val bottom = (h - paddingBottom).toFloat()
        viewBounds.set(left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val drawable = if (isPlaying) imagePause else imagePlay
        drawable?.setBounds(
            viewBounds.left.toInt(),
            viewBounds.top.toInt(),
            viewBounds.right.toInt(),
            viewBounds.bottom.toInt()
        )
        drawable?.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val x = event.x
            val y = event.y
            if (x in 0f..width.toFloat() && y in 0f..height.toFloat()) {
                isPlaying = !isPlaying
                performClick()
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}