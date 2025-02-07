package com.achrafapps.answerit

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView

class Settings : AppCompatActivity() {
    var Points = 5
    var Rounds = 10
    var cancelMusic = true
    var music = true
    var switchState = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        Retive()
    }

    fun plus1(view: View){

        if((Points<Rounds||switchState) && Points<99) {

            if(music){
                val mediaPlayer = MediaPlayer.create(this, R.raw.plus_click_audio)

                mediaPlayer.start()

            }
            Points++

            findViewById<TextView>(R.id.pointsTxt).text = Points.toString()

        }

    }

    fun plus2(view: View) {

        if (Rounds < 99) {

            if(music){

                val mediaPlayer = MediaPlayer.create(this, R.raw.plus_click_audio)

                mediaPlayer.start()

            }

            Rounds++

            findViewById<TextView>(R.id.roundsTxt).text = Rounds.toString()

        }
    }

    fun minus1(view: View) {

        if (Points > 2) {

            if(music){

                val mediaPlayer = MediaPlayer.create(this, R.raw.minus_audio)

                mediaPlayer.start()


            }

            Points--

            findViewById<TextView>(R.id.pointsTxt).text = Points.toString()

        }
    }

    fun minus2(view: View){

        if(Points<Rounds && Rounds>2) {

            if(music){

                val mediaPlayer = MediaPlayer.create(this, R.raw.minus_audio)

                mediaPlayer.start()


            }

            Rounds--

            findViewById<TextView>(R.id.roundsTxt).text = Rounds.toString()



        }
    }

    fun Apply(view:View){

        val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        editor.putInt("WinPoints", Points)

        editor.putInt("RoundsMax", Rounds)

        editor.putBoolean("switchState", switchState)

        editor.apply()

        cancelMusic =false

        Cancel(view)

    }

    fun Retive(){

        val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

        Points = sharedPreferences.getInt("WinPoints", 3)

        Rounds = sharedPreferences.getInt("RoundsMax", 5)

        switchState= sharedPreferences.getBoolean("switchState", false)

        music = sharedPreferences.getBoolean("music", true)

        findViewById<TextView>(R.id.pointsTxt).text = Points.toString()

        findViewById<TextView>(R.id.roundsTxt).text = Rounds.toString()

        findViewById<Switch>(R.id.switch1).isChecked = switchState

        setSwitch()
    }

    fun SwitchClick(view: View){

        switchState = !switchState
        if(music) {
            val mediaPlayer: MediaPlayer = if (switchState) {

                MediaPlayer.create(this, R.raw.plus_click_audio)


            } else {

                MediaPlayer.create(this, R.raw.minus_audio)

            }

            mediaPlayer.start()
        }

        setSwitch()

    }

    fun setSwitch(){

        if (switchState){

            findViewById<LinearLayout>(R.id.linear_rounds).visibility = View.GONE

        }else{

            findViewById<LinearLayout>(R.id.linear_rounds).visibility = View.VISIBLE

            if(Rounds<Points){

                Rounds = Points

                findViewById<TextView>(R.id.roundsTxt).text = Rounds.toString()

            }

        }

    }


    fun Cancel(view:View){

        val bundle = intent.extras!!

         val intent = Intent(this, AddPlayers()::class.java)
      val Names = bundle.getStringArrayList("Names")!!
     val playersNum = bundle.getInt("playersNum")
    intent.putExtra("Names", Names)
                intent.putExtra("playersNum", playersNum)
                if(music) {
                    if (cancelMusic) {
      val mediaPlayer = MediaPlayer.create(this, R.raw.minus_audio)

     mediaPlayer.start()
                    } else {

     val mediaPlayer = MediaPlayer.create(this, R.raw.plus_click_audio)

    mediaPlayer.start()
                    }
                }
              startActivity(intent)

                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)


    }

    override fun onBackPressed() {

        Cancel(View(this))

    }

}