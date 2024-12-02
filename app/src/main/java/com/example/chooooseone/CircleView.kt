package com.example.chooooseone


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Path
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class CircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var paint: Paint? = null
    var mWidth: Double? = null
    var halfCordLength: Double? = null
    var mHeight: Double? = null
    var dy: Double? = null
    var radius: Float? = null
    var index: Int? = null
    var rotation: Float? = null
    var angle: Int? = 0
    var paddingLeftInDp: Int = 0
    var paddingTopInDp: Int = 0
    var paddingRightInDp: Int = 0
    var paddingBottomInDp: Int = 0

    private var textView: TextView? = null

    init {
        paint = Paint()
        paint?.style = FILL
        paint?.isAntiAlias = true

        setBackgroundColor(Color.TRANSPARENT)

        textView = TextView(context).apply {
            gravity = Gravity.CENTER
            visibility = View.VISIBLE
            addView(this)

            layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }
        }
    }

    fun init(
        rotation: Float,
        index: Int = 0,
        color: Int,
        angle: Int,
        radius: Float,
        paddingLeftInDp: Int = 16,
        paddingTopInDp: Int = 16,
        paddingRightInDp: Int = 16,
        paddingBottomInDp: Int = 16
    ) {
        this.rotation = rotation
        this.index = index
        this.angle = angle
        textView?.text = index.toString()
        paint?.color = color

        val halfAngle = Math.toRadians((angle / 2).toDouble())
        val halfChordLength = 0.9 * radius * sin(halfAngle.toDouble())
        val verticalDistance = 0.9 * radius * cos(halfAngle.toDouble())

        mWidth = 2 * halfChordLength
        mHeight = radius.toDouble()
        dy = verticalDistance

        this.radius = radius
        this.halfCordLength = halfChordLength
        this.paddingLeftInDp = paddingLeftInDp
        this.paddingTopInDp = paddingTopInDp
        this.paddingRightInDp = paddingRightInDp
        this.paddingBottomInDp = paddingBottomInDp
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (mWidth != null && dy != null && paint != null && halfCordLength != null && radius != null && angle != null) {
            var originX = /*paddingLeftInDp.toFloat()*/ 0f
            originX += radius!!

            var originY = /*paddingTopInDp.toFloat()*/ 0f
            originY += radius!!

            val path = Path()
            path.moveTo(originX, originY)
            path.lineTo(originX - (mWidth!! / 2).toFloat(), (originY - dy!!).toFloat())

            val startX = originX - (mWidth!! / 2).toFloat()
            val startY = (originY - dy!!).toFloat()
            val endX = originX + (mWidth!! / 2).toFloat()
            val endY = (originY - dy!!).toFloat()

            var startAngle: Float = calculateAngle(originX, originY, startX, startY)
            var endAngle: Float = calculateAngle(originX, originY, endX, endY)

            if (startAngle < 0) startAngle += 360;
            if (endAngle < 0) endAngle += 360;

            path.arcTo(
                originX - 0.9f * radius!!,
                originY - 0.9f * radius!!,
                originX + 0.9f * radius!!,
                originY + 0.9f * radius!!,
                startAngle,
                angle!!.toFloat(),
                false
            )

            path.lineTo(originX, originY)
            canvas.drawPath(path, paint!!)
            path.close()

        }

        textView?.bringToFront()
    }


    private fun calculateAngle(
        centerX: Float,
        centerY: Float,
        pointX: Float,
        pointY: Float
    ): Float {
        return Math.toDegrees(
            atan2(
                (pointY - centerY).toDouble(),
                (pointX - centerX).toDouble()
            )
        ).toFloat()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(w, h)
    }
}