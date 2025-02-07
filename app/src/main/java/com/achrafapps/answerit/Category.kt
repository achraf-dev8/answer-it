package com.achrafapps.answerit

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Category : AppCompatActivity() {

    var catsArray = ArrayList<String>()
    var layoutsArray = ArrayList<LinearLayout>()
    lateinit var runnable: Runnable
    var i = 0
    var j = 0
    var roundNum = 1
    var boolArray = ArrayList<Int>()
    var nextPlayer = 0
    var points = 0
    lateinit var intent2: Intent
    lateinit var mediaPlayer: MediaPlayer
    var repeatPlayer = true
    var textbool = false
    var duration = 16
    var music = true
    var best = 0
    lateinit var dialog : Dialog
    var premium = false

    var roundsMax = 10

    var switchState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        intent2 = Intent(this, StartQuestion::class.java)

        mediaPlayer = MediaPlayer.create(this, R.raw.category_audio)

        Retive()

        if (music) {

            mediaPlayer.start()
        }


        mediaPlayer.setOnCompletionListener {

            if (repeatPlayer) {

                mediaPlayer.start()

            }

        }

        Define()

        changeCat()

    }

    fun Define() {

        catsArray =
            arrayListOf("Entertainment", "Geography", "Science", "Technology", "History", "Sports")

        layoutsArray.add(findViewById(R.id.entertainment_layout))
        layoutsArray.add(findViewById(R.id.geo_layout))
        layoutsArray.add(findViewById(R.id.science_layout))
        layoutsArray.add(findViewById(R.id.tech_layout))
        layoutsArray.add(findViewById(R.id.history_layout))
        layoutsArray.add(findViewById(R.id.sports_layout))


    }

    fun Retive() {

        val bundle = intent.extras!!

        roundNum = bundle.getInt("roundNum")

        boolArray = bundle.getIntegerArrayList("boolPlayers")!!

        points = bundle.getInt("points")

        if (roundNum > 0) {

            findViewById<TextView>(R.id.RoundTxt).text = "Round $roundNum"

        } else if (roundNum == -1) {

            findViewById<TextView>(R.id.RoundTxt).text = "Last One Standing"

            //اللهنا

            findViewById<TextView>(R.id.RoundTxt).textSize = 32F
        } else {

            if (roundNum == -2) {
                findViewById<TextView>(R.id.RoundTxt).text = "Best Streak"
            } else {

                findViewById<TextView>(R.id.RoundTxt).text = "Score:$points"

            }
            duration = 7
            intent2 = Intent(this, StartQuestionStrike::class.java)

        }

        var nextPlayerBool = false

        while (!nextPlayerBool) {

            if (boolArray[nextPlayer] == 1) {

                nextPlayerBool = true

            }

            nextPlayer++


        }

        val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

        music = sharedPreferences.getBoolean("music", true)

        premium = sharedPreferences.getBoolean("premium", false)

        best = sharedPreferences.getInt("best", 0)

        roundsMax = sharedPreferences.getInt("RoundsMax", 5)

        switchState= sharedPreferences.getBoolean("switchState", false)

        val gson = Gson()

        val json = sharedPreferences.getString("Names", null)

        val type = object : TypeToken<ArrayList<String>>() {}.type

        val Names = gson.fromJson<ArrayList<String>>(json, type)

        if (roundNum != -3) {

            if (Names[nextPlayer - 1] != "") {

                findViewById<TextView>(R.id.firstPlayerTxt).text = Names[nextPlayer - 1]

            } else {

                findViewById<TextView>(R.id.firstPlayerTxt).text = "Player $nextPlayer"
            }

        } else {

            best = sharedPreferences.getInt("best", 0)

            if (points > best) {

                findViewById<TextView>(R.id.RoundTxt).setTextColor(Color.parseColor("#FFEB32"))

            }

        }


    }

    @SuppressLint("WrongViewCast")
    fun changeCat() {

        val random = (0..<catsArray.size).random()

        runnable = Runnable {

            j++

            layoutsArray[i].visibility = View.GONE

            if (i == catsArray.size - 1) {
                i = 0
            } else {
                i++
            }

            layoutsArray[i].visibility = View.VISIBLE

            textbool = !textbool

            if (j <= duration + random) {
                Handler().postDelayed(runnable, 200)
            } else {

                SpinEnd()

            }

        }

        Handler().postDelayed(runnable, 200)


    }

    fun SpinEnd() {

        mediaPlayer.stop()

        repeatPlayer = false

        if (roundNum != -3) {
            findViewById<TextView>(R.id.firstPlayerTxt).visibility = View.VISIBLE
        }

        findViewById<Button>(R.id.nextBtn).visibility = View.VISIBLE

    }


    fun Next(view: View) {

        if(premium  || checkConnection()) {
            intent2.putExtra("category", catsArray[i])

            intent2.putExtra("currentPlayer", nextPlayer)

            intent2.putExtra("scoresArray", intent.extras!!.getIntegerArrayList("scoresArray")!!)

            intent2.putExtra("boolPlayers", boolArray)

            intent2.putExtra("pointsArray", arrayListOf(0, 0, 0, 0, 0, 0))

            intent2.putExtra("points", points)

            intent2.putExtra("roundNum", roundNum)

            if (music) {
                val mediaPlayer = MediaPlayer.create(this, R.raw.button_audio)

                mediaPlayer.start()
            }

            startActivity(intent2)
        }else{
            Toast.makeText(this, "Check your internet connection!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR)
        dialog.setContentView(R.layout.dialogue)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun yes(view: View) {
        mediaPlayer.stop()

        val intent = Intent(this, MainActivity()::class.java)

        startActivity(intent)

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun no(view: View) {

        if (music) {
            val mediaPlayer = MediaPlayer.create(this, R.raw.minus_audio)

            mediaPlayer.start()
        }

        dialog.dismiss()
    }

    fun checkConnection():Boolean{

        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        val connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!
            .state == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED

        return connected

    }

}