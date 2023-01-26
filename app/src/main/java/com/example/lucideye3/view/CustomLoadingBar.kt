package com.example.lucideye3.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.lucideye3.R

class CustomLoadingBar(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var animValue = 0
    private val strokeWidth = 35

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth.toFloat()
        paint.color = Color.WHITE
        val rectF = RectF()
        rectF[strokeWidth.toFloat(), strokeWidth.toFloat(), (width - strokeWidth).toFloat()] =
            (width - strokeWidth).toFloat()
        canvas.drawArc(rectF, 0f, 360f, false, paint)
        paint.color = ContextCompat.getColor(context, R.color.green)
        canvas.drawArc(rectF, animValue.toFloat(), 80f, false, paint)
    }

    fun setValue(animatedValue: Int) {
        animValue = animatedValue
        invalidate()
    }
}