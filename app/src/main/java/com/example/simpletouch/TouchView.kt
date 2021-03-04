package com.example.simpletouch

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.View

class TouchView(context : Context?, attrs : AttributeSet?) : View(context, attrs){

    private var paint: Paint

    private var circlePoint = Point()
    private var circleRadius = 20

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        paint = Paint()
        paint.color = Color.WHITE
        paint.isAntiAlias = true
        paint.strokeWidth = 5f
        paint.style = Paint.Style.FILL
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas?) {
        canvas!!.drawCircle(circlePoint.x.toFloat(), circlePoint.y.toFloat(), circleRadius.toFloat(), paint)
    }

    fun setCircle(x: Int, y: Int, radius: Int){
        circlePoint.x = x
        circlePoint.y = y
        circleRadius = radius
    }
}