package com.example.chooooseone

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Path
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.cos
import kotlin.math.sin

class CircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val fillPaint: Paint = Paint().apply {
        style = FILL
        isAntiAlias = true
    }

    private val borderPaint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLUE
        strokeWidth = 10f
        isAntiAlias = true
    }

    private val arrowPaint: Paint = Paint().apply {
        style = FILL
        color = Color.RED
        isAntiAlias = true
    }

    private var textView: TextView = TextView(context).apply {
        gravity = Gravity.CENTER
        setTextColor(Color.BLACK)
    }

    private var radius: Float = 0f
    private var angle: Int = 0

    init {
        setBackgroundColor(Color.TRANSPARENT)

        addView(textView, LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            topToTop = LayoutParams.PARENT_ID
            bottomToBottom = LayoutParams.PARENT_ID
            startToStart = LayoutParams.PARENT_ID
            endToEnd = LayoutParams.PARENT_ID
        })
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
        this.angle = angle
        this.radius = radius
        fillPaint.color = color
        textView.text = index.toString()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val originX = width / 2f
        val originY = height / 2f

        val path = Path()
        path.moveTo(originX, originY)

        val halfAngle = Math.toRadians((angle / 2).toDouble())
        val halfChordLength = radius * sin(halfAngle)
        val verticalDistance = radius * cos(halfAngle)

        val startX = originX - halfChordLength.toFloat()
        val startY = originY - verticalDistance.toFloat()
        val endX = originX + halfChordLength.toFloat()
        val endY = startY

        path.lineTo(startX, startY)
        path.arcTo(
            originX - radius,
            originY - radius,
            originX + radius,
            originY + radius,
            -angle / 2f,
            angle.toFloat(),
            false
        )
        path.lineTo(originX, originY)
        path.close()

        canvas.drawPath(path, fillPaint)
        canvas.drawPath(path, borderPaint)

        drawArrow(canvas, originX, originY)
    }

    private fun drawArrow(canvas: Canvas, centerX: Float, centerY: Float) {
        val arrowPath = Path()
        val arrowLength = radius / 3
        val arrowWidth = arrowLength / 2

        arrowPath.moveTo(centerX, centerY - radius)
        arrowPath.lineTo(centerX - arrowWidth / 2, centerY - radius + arrowLength)  // Base gauche
        arrowPath.lineTo(centerX + arrowWidth / 2, centerY - radius + arrowLength)  // Base droite
        arrowPath.close()

        canvas.drawPath(arrowPath, arrowPaint)
    }
}
