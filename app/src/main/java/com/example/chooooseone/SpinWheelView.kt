package com.example.chooooseone

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.cos
import kotlin.math.sin

private const val COMPLETE_ANGLE = 360

class SpinWheelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint().apply {
        color = Color.parseColor("#1F56FF")
        style = Paint.Style.FILL
    }
    private var radius: Float = 0f
    private var angleEnclosedBySector: Int = 0
    private val circleViews: ArrayList<CircleView?> = arrayListOf()
    private val sectors: MutableList<SectorModel> = mutableListOf()

    private var paddingLeftInDp: Int = 0
    private var paddingTopInDp: Int = 0
    private var paddingRightInDp: Int = 0
    private var paddingBottomInDp: Int = 0
    private var isInit: Boolean = false
    private var currentRotation = 0f

    init {
        setBackgroundColor(Color.TRANSPARENT)
    }

    fun addFirstSector(label: String?) {
        val randomColor = Color.rgb(
            (0..255).random(),
            (0..255).random(),
            (0..255).random()
        )
        val firstSector = SectorModel(randomColor, label)
        sectors.add(firstSector)
        angleEnclosedBySector = COMPLETE_ANGLE / sectors.size
        requestLayout()
    }

    fun init(
        sectors: List<SectorModel>,
        paddingLeftInDp: Int = 16,
        paddingTopInDp: Int = 16,
        paddingRightInDp: Int = 16,
        paddingBottomInDp: Int = 16
    ) {
        this.sectors.clear()
        this.sectors.addAll(sectors)
        angleEnclosedBySector = COMPLETE_ANGLE / this.sectors.size

        this.paddingLeftInDp = paddingLeftInDp
        this.paddingTopInDp = paddingTopInDp
        this.paddingRightInDp = paddingRightInDp
        this.paddingBottomInDp = paddingBottomInDp
        requestLayout()
    }

    fun addSector(label: String?) {
        val randomColor = Color.rgb(
            (0..255).random(),
            (0..255).random(),
            (0..255).random()
        )
        val newSector = SectorModel(randomColor, label)
        sectors.add(newSector)
        angleEnclosedBySector = COMPLETE_ANGLE / sectors.size
        requestLayout()
    }

    fun spin() {
        val randomRotations = (3..6).random() * 360f
        val randomStopAngle = (0 until sectors.size).random() * angleEnclosedBySector
        val finalRotation = currentRotation + randomRotations + randomStopAngle
        val randomDuration = (1000..4000).random()

        val objectAnimator = ObjectAnimator.ofFloat(this, "rotation", currentRotation, finalRotation)
        objectAnimator.duration = randomDuration.toLong()
        objectAnimator.interpolator = AccelerateDecelerateInterpolator()
        objectAnimator.repeatCount = 0
        objectAnimator.repeatMode = ObjectAnimator.RESTART
        objectAnimator.start()

        currentRotation = finalRotation
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (radius == 0f || sectors.isEmpty()) return

        for (i in sectors.indices) {
            val sector = sectors[i]
            val sectorAngle = i * angleEnclosedBySector.toFloat()
            val angleInRadians = Math.toRadians((sectorAngle + angleEnclosedBySector / 2f).toDouble())

            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 40f
                textAlign = Paint.Align.CENTER
            }

            val x = (width / 2f + radius * cos(angleInRadians) / 2).toFloat()
            val y = (height / 2f + radius * sin(angleInRadians) / 2).toFloat()

            paint.color = sector.color
            canvas.drawArc(0f, 0f, width.toFloat(), height.toFloat(), sectorAngle, angleEnclosedBySector.toFloat(), true, paint)

            sector.label?.let {
                canvas.drawText(it, x, y, textPaint)
            }
        }

        setPadding(paddingLeftInDp, paddingTopInDp, paddingRightInDp, paddingBottomInDp)
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val childWidth: Int = childView.measuredWidth
            val childHeight: Int = childView.measuredHeight
            val left = 0
            val top = 0

            childView.layout(left, top, left + childWidth, top + childHeight)

            circleViews[i]?.rotation?.let {
                childView.pivotX = radius
                childView.pivotY = radius
                childView.rotation = it
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, width)
        radius = width / 2.0f

        if (!isInit && sectors.isNotEmpty()) {
            isInit = true

            for (i in sectors.indices) {
                val circleView = CircleView(context).also {
                    it.init(
                        angleEnclosedBySector,
                        i,
                        radius
                    )
                    addView(it, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT))
                }

                circleViews.add(circleView)
                circleView.measure(width, width)
            }
        }
    }

}
