package com.achrafapps.answerit

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ShowScoreStrike : AppCompatActivity() {

    var playersNum = 1

    val PlayersLayouts = ArrayList<LinearLayout>()

    val PlayersTxts = ArrayList<TextView>()

    val ScoreTxts = ArrayList<TextView>()

    var Names = ArrayList<String>()

    var points = 0

    val Imgs = ArrayList<ImageView>()

    var ScoresArray = ArrayList<Int>()

    val winnersArray = ArrayList<Int>()

    var boolPlayers= ArrayList<Int>()

    var roundNum = -2

    var currentPlayer = 1

    var music = false

    lateinit var dialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_score)

        DefineArrays()

        Retive()
        ScoresArray[currentPlayer - 1] = points

        val runnable = {
        for (i in 0..<playersNum) {

            ScoreTxts[i].text = ScoresArray[i].toString()

        }
            if(currentPlayer == playersNum) {
                checkWin()
            }else{

                Next()
            }
            if(music) {
                val mediaPlayer = MediaPlayer.create(this, R.raw.plus_click_audio)
                mediaPlayer.start()
            }
    }
                        boolPlayers[currentPlayer-1] = 0

        Handler().postDelayed(runnable, 1200)

    }

    fun DefineArrays() {

        PlayersLayouts.add(findViewById(R.id.Layout1))
        PlayersLayouts.add(findViewById(R.id.Layout2))
        PlayersLayouts.add(findViewById(R.id.Layout3))
        PlayersLayouts.add(findViewById(R.id.Layout4))
        PlayersLayouts.add(findViewById(R.id.Layout5))
        PlayersLayouts.add(findViewById(R.id.Layout6))

        PlayersTxts.add(findViewById(R.id.player1txt))
        PlayersTxts.add(findViewById(R.id.player2txt))
        PlayersTxts.add(findViewById(R.id.player3txt))
        PlayersTxts.add(findViewById(R.id.player4txt))
        PlayersTxts.add(findViewById(R.id.player5txt))
        PlayersTxts.add(findViewById(R.id.player6txt))

        ScoreTxts.add(findViewById(R.id.score1txt))
        ScoreTxts.add(findViewById(R.id.score2txt))
        ScoreTxts.add(findViewById(R.id.score3txt))
        ScoreTxts.add(findViewById(R.id.score4txt))
        ScoreTxts.add(findViewById(R.id.score5txt))
        ScoreTxts.add(findViewById(R.id.score6txt))

        Imgs.add(findViewById(R.id.image1))
        Imgs.add(findViewById(R.id.image2))
        Imgs.add(findViewById(R.id.image3))
        Imgs.add(findViewById(R.id.image4))
        Imgs.add(findViewById(R.id.image5))
        Imgs.add(findViewById(R.id.image6))


    }

    fun Retive() {

        val bundle = intent.extras!!

        points =  bundle.getInt("points")

        ScoresArray =  bundle.getIntegerArrayList("scoresArray")!!

        boolPlayers =  bundle.getIntegerArrayList("boolPlayers")!!

        currentPlayer  =  bundle.getInt("currentPlayer")

        val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

        playersNum = sharedPreferences.getInt("PlayersNum", 2)

        music = sharedPreferences.getBoolean("music", true)

        val gson = Gson()

        val json = sharedPreferences.getString("Names", null)

        val type = object : TypeToken<ArrayList<String>>() {}.type

        Names = gson.fromJson(json, type)

        for (i in 0..<playersNum) {


                PlayersLayouts[i].visibility = View.VISIBLE

                if (Names[i] == "") {

                    Names[i] = "Player " + (i + 1)

                }

            PlayersTxts[i].text = Names[i]

            ScoreTxts[i].text = ScoresArray[i].toString()

            Imgs[i].visibility = View.GONE

        }

    }


    fun checkWin(){

       val max = ScoresArray.maxOrNull()

            for(i in 0..<playersNum){

                if (ScoresArray[i] == max){

                    winnersArray.add(i+1)

                }

            }

            if (winnersArray.size == 1){

              val runnable = { val intent = Intent(this, WinDisplay::class.java)

                intent.putExtra("winner", winnersArray[0])

                startActivity(intent)
              }

                Handler().postDelayed(runnable, 1500)

            }else{

                Tie()

            }

        }


    fun Tie(){


        roundNum = -1
        var aLose = false
        for (i in 0..<playersNum) {


            if (!winnersArray.contains(i+1) && !aLose) {
                    aLose =true
            }else{
                boolPlayers[i] = 1
            }

        }

        val runnable = {
            for (i in 0..<playersNum) {
                val playersTxt = PlayersTxts[i]
                val scoreTxt = ScoreTxts[i]
                if (!winnersArray.contains(i + 1)) {

                    playersTxt.setTextColor(Color.parseColor("#FA4343"))

                    scoreTxt.setTextColor(Color.parseColor("#FA4343"))

                    playersTxt.paintFlags = playersTxt.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                    scoreTxt.paintFlags = scoreTxt.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


                } else {
                    playersTxt.setTextColor(Color.parseColor("#7EE652"))

                    scoreTxt.setTextColor(Color.parseColor("#7EE652"))

                }
            }
            findViewById<Button>(R.id.Next).visibility= View.VISIBLE

            if(music) {
                val mediaPlayer = MediaPlayer.create(this, R.raw.minus_audio)
                mediaPlayer.start()
            }
        }

                if(aLose){

                    Handler().postDelayed(runnable, 1000)
                }else{
                    Next()
                }

    }

    fun Next2(view: View){

        val intent = Intent(this, Category::class.java)
        intent.putExtra("roundNum", roundNum)
        intent.putExtra("scoresArray", arrayListOf(0, 0, 0, 0, 0, 0))
        intent.putExtra("boolPlayers", boolPlayers)
        if(music) {
            val mediaPlayer = MediaPlayer.create(this, R.raw.button_audio2)
            mediaPlayer.start()
        }
        startActivity(intent)

    }

    fun Next(){

        val runnable = {  val intent = Intent(this, Category::class.java)
            intent.putExtra("roundNum", roundNum)
            intent.putExtra("scoresArray", ScoresArray)
            intent.putExtra("boolPlayers", boolPlayers)
            startActivity(intent)

        }

        Handler().postDelayed(runnable, 1500)

    }

    override fun onBackPressed() {
        if(findViewById<Button>(R.id.Next).visibility== View.VISIBLE) {
            dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR)
            dialog.setContentView(R.layout.dialogue)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    }

    fun yes(view: View) {

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

    }

