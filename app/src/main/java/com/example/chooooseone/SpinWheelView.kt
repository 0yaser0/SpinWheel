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

    private var paint: Paint? = null
    private var radius: Float? = null
    private var angleEnclosedBySector: Int = 0
    private var circleViews: ArrayList<CircleView?> = arrayListOf()
    private var sectors: MutableList<SectorModel> = mutableListOf()

    private var paddingLeftInDp: Int = 0
    private var paddingTopInDp: Int = 0
    private var paddingRightInDp: Int = 0
    private var paddingBottomInDp: Int = 0
    private var isInit: Boolean = false

    init {
        paint = Paint()
        paint?.color = Color.parseColor("#1F56FF")
        paint?.style = Paint.Style.FILL

        setBackgroundColor(Color.TRANSPARENT)

        addDefaultSector() // Ensure default sector is added with label.
    }

    private fun addDefaultSector() {
        val randomColor = Color.rgb(
            (0..255).random(),
            (0..255).random(),
            (0..255).random()
        )
        val defaultLabel = "Default Sector"  // Assign a label to the default sector
        val defaultSector = SectorModel(randomColor, defaultLabel)  // Add label here
        sectors.add(defaultSector)
        angleEnclosedBySector = COMPLETE_ANGLE / sectors.size
        requestLayout() // Recalculate layout after adding the default sector
    }

    private fun init(
        sectors: List<SectorModel>,
        paddingLeftInDp: Int = 16,
        paddingTopInDp: Int = 16,
        paddingRightInDp: Int = 16,
        paddingBottomInDp: Int = 16,
    ) {
        this.sectors.clear()
        this.sectors.addAll(sectors)
        angleEnclosedBySector = COMPLETE_ANGLE / sectors.size

        this.paddingLeftInDp = paddingLeftInDp
        this.paddingTopInDp = paddingTopInDp
        this.paddingRightInDp = paddingRightInDp
        this.paddingBottomInDp = paddingBottomInDp
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
        requestLayout() // Recalculate layout after adding a new sector
    }

    private var currentRotation = 0f
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
        if (radius == null || paint == null) return

        for (i in sectors.indices) {
            val sector = sectors[i]
            val sectorAngle = i * angleEnclosedBySector.toFloat()
            val angleInRadians = Math.toRadians((sectorAngle + angleEnclosedBySector / 2f).toDouble())

            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 40f
                textAlign = Paint.Align.CENTER
            }

            val x = (width / 2f + radius!! * cos(angleInRadians)).toFloat()
            val y = (height / 2f + radius!! * sin(angleInRadians)).toFloat()

            paint?.color = sector.color
            canvas.drawArc(0f, 0f, width.toFloat(), height.toFloat(), sectorAngle, angleEnclosedBySector.toFloat(), true, paint!!)

            // Draw the label for each sector, including the default one
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
                childView.pivotX = radius!!
                childView.pivotY = radius!!
                childView.rotation = it
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, width) // Make the width equal to height to ensure it's circular
        radius = width / 2.0f

        if (!isInit && sectors.isNotEmpty()) {
            isInit = true

            for (i in 0 until sectors.size) {
                val circleView = CircleView(context).also {
                    it.init(
                        22.5f + i * 45f,
                        i,
                        sectors[i].color,
                        angleEnclosedBySector,
                        radius!!,
                        paddingLeftInDp,
                        paddingTopInDp,
                        paddingRightInDp,
                        paddingBottomInDp
                    )

                    addView(it, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT))
                }

                circleViews.add(circleView)
                circleView.measure(width, height)
            }
        }
    }
}

