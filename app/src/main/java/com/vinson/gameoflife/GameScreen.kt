package com.vinson.gameoflife

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.collections.ArrayList

class GameScreen(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val paint : Paint = Paint()
    private val density : Float
    private lateinit var dp : DeviceProfile

    private var updatedCells = mutableListOf<Cell>()

    private var downPos = PointF(0f, 0f)

    init {
        paint.color = Color.BLACK
        density = context.resources.displayMetrics.scaledDensity
    }

    fun setup(deviceProfile: DeviceProfile) {
        dp = deviceProfile
        paint.strokeWidth = dp.lineWidth
        println("device ${dp.cellWidth}, ${dp.lineWidth}")
        postInvalidate()
    }

    fun refresh(nextUpdatedCells : ArrayList<Cell>) {
        updatedCells = nextUpdatedCells
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        println("Vinson onDraw")

        drawGrid(canvas)

        if (updatedCells.size > 0) {
            for (cell in updatedCells) {
                drawCell(canvas, cell)
            }
            updatedCells.clear()
        }
    }

    private fun drawGrid(canvas: Canvas) {
        for (i in 0..dp.colAndRow) {
            canvas.drawLine(0f, i * dp.gridWidth, dp.availableWidth, i * dp.gridWidth, paint)
            canvas.drawLine(i * dp.gridWidth, 0f, i * dp.gridWidth, dp.availableWidth, paint)
        }
    }

    private fun drawCell(canvas: Canvas, cell: Cell) {
        paint.color = if (cell.live) Color.BLACK
        else Color.WHITE
        canvas.drawCircle((cell.pos.x + 0.5f) * dp.gridWidth, (cell.pos.y + 0.5f) * dp.gridWidth, dp.cellWidth / 2f, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            downPos.set(event.x, event.y)
        }
        return super.onTouchEvent(event)
    }

    fun getClickCellPos(): Point {
        return Point((downPos.x / dp.gridWidth).toInt(), (downPos.y / dp.gridWidth).toInt())
    }
}