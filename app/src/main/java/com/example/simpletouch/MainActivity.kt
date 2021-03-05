package com.example.simpletouch

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import splitties.alertdialog.*
import splitties.toast.toast
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val NICKNAME = "Nickname"
    private val HSSIZE = "hsCount"
    private val HSNAME = "hsName"
    private val HSVALUE = "hsValue_"
    private val NOENTRY = 0

    private lateinit var mGestDetector : GestureDetector
    private val tvTime: TextView by lazy { findViewById(R.id.text_time)}
    private val touchview : TouchView by lazy { findViewById(R.id.touchView)}

    private var nickname = "anonymous"
    private var startTime : Long = 0
    private var elapsedTime : Long = 0
    private val MAXCLICKS = 10
    private var clickCount = 0
    val r = Random
    val CIRCLERADIUS = 20
    var pos = Point()

    private val HSCOUNT = 3                              //Anzahl Einträge in Liste
    private lateinit var hsList : ArrayList<Long>        //Werte in der Liste
    private lateinit var hsNameList : ArrayList<String>  //Namen + Werte



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
                 elapsedTime = SystemClock.elapsedRealtime() - startTime
                tvTime.visibility = View.VISIBLE
                tvTime.text = getString(R.string.timeString, elapsedTime.toString())
                // Game Reset
                clickCount = 0
                touchview.setCircle(w / 2, h / 2, CIRCLERADIUS)


                val entries = hsList.size
                if((entries < HSCOUNT) || ((entries == HSCOUNT) && (elapsedTime < hsList.get(HSCOUNT-1)))){
                    saveHighscore();

                }
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


    //Optionsmenü einbinden
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    //onClickListener für die Menü-Items setzen und entsprechend
    //reagieren
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_1 -> {
                onClickMenu1()
                return true
            }
            R.id.menu_2 -> {
                onClickMenu2()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //erster Menüpunkt zum Ändern des Nicknamens
    //Eingabe per AlertDialog
    private fun onClickMenu1() {
        toast(R.string.menu_item1)
        val editTextName = EditText(applicationContext)
        alertDialog (title = getString(R.string.save_name_title),
                message = getString(R.string.current_player, nickname),
                 view = editTextName) {
            okButton(){
                val name = editTextName.text.toString()
                if(name.isNotEmpty() && !name.isBlank()){
                    nickname = name
                    toast(getString(R.string.current_player, nickname))
                }
                else{
                    toast(R.string.no_name)
                }
            }
            cancelButton(){
                toast(R.string.no_name)
            }
        }.show()
    }

    //zweiter Menüpunkt zum Anzeigen der Highscore Liste
    //Anzeige ebenfalls über AlertDialog
    private fun onClickMenu2() {
        val listView = ListView(applicationContext)
        val adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, hsNameList)
        listView.adapter = adapter

        val msg = if(hsNameList.get(0).equals(getString(R.string.no_entry))) getString(R.string.no_entry) else getString(R.string.current_hs)

        alertDialog(title = getString(R.string.highscore), message = msg, view = listView) {
            okButton()
        }.show()
    }

    inline fun Context.alertDialog(title: CharSequence? = null, message: CharSequence? = null,
                                   @DrawableRes iconResource: Int = 0,
                                   view: View?,
                                   dialogConfig: AlertDialog.Builder.() -> Unit = {}
    ): AlertDialog {
        return AlertDialog.Builder(this).apply {
            this.title = title
            this.message  = message
            setIcon(iconResource)
            setView(view)
            dialogConfig()
        }.create()
    }


    //wird bei Schließen der App aufgerufen und speichert aktuelle
    //Highscore Liste sowie aktuellen nicknamen in SharedPreferences
    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")

        val sp = getPreferences(Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.putString(NICKNAME, nickname)

        edit.putInt(HSSIZE, hsList.size)
        for(i in 0 until hsList.size){
            edit.putLong(HSVALUE + i, hsList.get(i))
            edit.putString(HSNAME + i, hsNameList.get(i))
        }

        edit.apply()
    }

    //wird bei Wiederaufruf der App aufgerufen
    //liest die gespeicherten Daten aus SharedPreferences aus
    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")

        hsList = ArrayList()
        hsNameList = ArrayList()

        val sp = getPreferences(Context.MODE_PRIVATE)
        nickname = sp.getString(NICKNAME, "anonymous").toString()
        toast(getString(R.string.current_player, nickname))

        val hsCount = sp.getInt(HSSIZE, 0)
        if(hsCount == 0){
            hsList.add(NOENTRY.toLong())
            hsNameList.add(getString(R.string.no_entry))
        }
        else{
            for(i in 0 until hsCount){
                hsList.add(sp.getLong(HSVALUE +i, 0))
                hsNameList.add(sp.getString(HSNAME +i, "anonymous").toString())
            }
        }
    }


    private fun saveHighscore() {
        alertDialog(title = getString(R.string.save_highscore_title), message = getString(R.string.save_highscore_msg, nickname, elapsedTime.toString())){
            positiveButton(R.string.save){
                val count = hsList.size
                if(count == 0 || elapsedTime > hsList.get(count-1)){
                    hsNameList.removeAt(0)
                    hsList.removeAt(0)
                    hsList.add(elapsedTime)
                    hsNameList.add(getString(R.string.list_input, nickname, elapsedTime.toString()))
                }
                else{
                    for(i in 0..count){
                        if(elapsedTime < hsList.get(i)){
                            hsList.add(i, elapsedTime)
                            hsNameList.add(i, getString(R.string.list_input, nickname, elapsedTime.toString()))
                            break;
                        }
                    }
                }

                if(hsList.size > HSCOUNT){
                    hsList.removeAt(HSCOUNT)
                    hsNameList.removeAt(HSCOUNT)
                }
            }
            cancelButton()
        }.show()
    }
}
