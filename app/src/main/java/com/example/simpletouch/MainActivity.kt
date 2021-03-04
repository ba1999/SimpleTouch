package com.example.simpletouch

import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.os.SystemClock
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private val Tag = "MainActivity"

    private lateinit var mGestDetector : GestureDetector
    private val tvTime: TextView by lazy { findViewById(R.id.text_time)}
    private val touchview : TouchView by lazy { findViewById(R.id.touchView)}

    private var startTime : Long = 0
    private var elapsedTime : Long = 0
    private val MAXCLICKS = 10
    private var clickCount = 0
    val r = Random
    val CIRCLERADIUS = 20
    var pos = Point()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mGListener = MyGestureListener()
        mGestDetector = GestureDetector(this, mGListener)

        touchview.setBackgroundColor(Color.BLUE)

        val display = windowManager.defaultDisplay
        val width = display.width
        val height = display.height

        pos.x = width/2
        pos.y = height/2
        touchview.setCircle(pos.x, pos.y, CIRCLERADIUS)

        touchview.setOnTouchListener { v, e->
            mGestDetector.onTouchEvent(e)
        }


    }

    //eigenen GestureListener implementieren als inner class, um auf Variablen zugreifen zu können
    inner class MyGestureListener() : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            val x = e!!.x
            val y = e.y

            // Abbruch, wenn der Klick nicht im Kreis liegt
            if (!inCircle(x.toInt(), y.toInt())) return false

            clickCount++

            // To Dos beim ersten Klick
            if (clickCount == 1) {
                startTime = SystemClock.elapsedRealtime()
                tvTime.visibility = View.INVISIBLE
            }

            val w = touchview.width
            val h = touchview.height

            // To Dos beim letzten Klick
            if (clickCount == MAXCLICKS) {
                // Zeit stoppen und anzeigen
                val elapsedTime = SystemClock.elapsedRealtime() - startTime
                tvTime.visibility = View.VISIBLE
                tvTime.text = getString(R.string.timeString, elapsedTime.toString())
                // Game Reset
                clickCount = 0
                touchview.setCircle(w / 2, h / 2, CIRCLERADIUS)
                touchview.invalidate()
                return true
            }

            // neue Position
            pos.x = CIRCLERADIUS + r.nextInt(w - 2 * CIRCLERADIUS)
            pos.y = CIRCLERADIUS + r.nextInt(h - 2 * CIRCLERADIUS)

            touchview.setCircle(pos.x, pos.y, CIRCLERADIUS)
            touchview.invalidate()
            return true
        }


        fun inCircle(x: Int, y: Int) : Boolean {
            val distx = x - pos.x
            val disty = y - pos.y

            if (distx*distx + disty*disty < CIRCLERADIUS*CIRCLERADIUS) return true
            return false
        }
    }
}
