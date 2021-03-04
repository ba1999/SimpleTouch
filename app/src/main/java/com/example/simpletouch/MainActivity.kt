package com.example.simpletouch

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val Tag = "MainActivity"

    private lateinit var mGestDetector : GestureDetector
    private val tvGesture : TextView by lazy { findViewById(R.id.tvGesture)}
    private val touchview : TouchView by lazy { findViewById(R.id.touchView)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mGListener = MyGestureListener()
        mGestDetector = GestureDetector(this, mGListener)

        touchview.setBackgroundColor(Color.BLUE)

        touchview.setOnTouchListener { v, e->
            mGestDetector.onTouchEvent(e)
        }
    }

    //eigenen GestureListener implementieren als inner class, um auf Variablen zugreifen zu können
    inner class MyGestureListener() : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            tvGesture.text = getString(R.string.position_down,
                    e!!.x.toString(), e.y.toString())
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?,
                             velocityX: Float, velocityY: Float): Boolean {
            tvGesture.text = getString(R.string.position_fling,
                    e1!!.x.toString(), e1.y.toString(),
                    e2!!.x.toString(), e2.y.toString())
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            tvGesture.text = getString(R.string.position_longPress,
                    e!!.x.toString(), e.y.toString())
        }
    }
}