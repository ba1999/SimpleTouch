package com.example.simpletouch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val Tag = "MainActivity"

    private lateinit var mGestDetector : GestureDetector
    private val tvGesture : TextView by lazy { findViewById(R.id.tvGesture)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mGListener = MyGestureListener()
        mGestDetector = GestureDetector(this, mGListener)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        this.mGestDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
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