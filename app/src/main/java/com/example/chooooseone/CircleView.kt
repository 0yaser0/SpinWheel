package com.example.chooooseone

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
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

    private val fillPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLUE
        strokeWidth = 10f
        isAntiAlias = true
    }

    private val arrowPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
        isAntiAlias = true
    }

    private val textView: TextView = TextView(context).apply {
        gravity = android.view.Gravity.CENTER
        setTextColor(Color.BLACK)
    }

    private var radius = 0f
    private var angle = 0

    init {
        setBackgroundColor(Color.TRANSPARENT)

        addView(
            textView, LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                topToTop = LayoutParams.PARENT_ID
                bottomToBottom = LayoutParams.PARENT_ID
                startToStart = LayoutParams.PARENT_ID
                endToEnd = LayoutParams.PARENT_ID
            }
        )
    }

    fun init(
        color: Int,
        segmentAngle: Int,
        segmentRadius: Float,
    ) {
        fillPaint.color = color
        this.angle = segmentAngle
        this.radius = segmentRadius
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f

        drawSegment(canvas, centerX, centerY)
        drawArrow(canvas, centerX, centerY)
    }

    private fun drawSegment(canvas: Canvas, centerX: Float, centerY: Float) {
        val path = Path()
        path.moveTo(centerX, centerY)

        val halfAngle = Math.toRadians((angle / 2).toDouble())
        val chordLength = radius * sin(halfAngle).toFloat()
        val height = radius * cos(halfAngle).toFloat()

        val startX = centerX - chordLength
        val startY = centerY - height
        val endX = centerX + chordLength
        val endY = centerY - height

        path.lineTo(startX, startY)
        path.arcTo(
            centerX - radius, centerY - radius,
            centerX + radius, centerY + radius,
            -angle / 2f, angle.toFloat(), false
        )
        path.lineTo(centerX, centerY)
        path.close()

        canvas.drawPath(path, fillPaint)
        canvas.drawPath(path, borderPaint)
    }

    private fun drawArrow(canvas: Canvas, centerX: Float, centerY: Float) {
        val arrowPath = Path()
        val arrowLength = radius / 3
        val arrowWidth = arrowLength / 2

        arrowPath.moveTo(centerX, centerY - radius)
        arrowPath.lineTo(centerX - arrowWidth / 2, centerY - radius + arrowLength)
        arrowPath.lineTo(centerX + arrowWidth / 2, centerY - radius + arrowLength)
        arrowPath.close()

        canvas.drawPath(arrowPath, arrowPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    companion object {
        private fun calculateAngle(centerX: Float, centerY: Float, pointX: Float, pointY: Float): Float {
            return Math.toDegrees(
                atan2((pointY - centerY).toDouble(), (pointX - centerX).toDouble())
            ).toFloat()
        }
    }
}
