package com.achrafapps.answerit

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources

class Mode : AppCompatActivity() {

    var modeNum = 1
    var BtnArray = ArrayList<Button>()
    var music = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode)

        BtnArray = arrayListOf(findViewById(R.id.raceBtn), findViewById(R.id.standingBtn), findViewById(R.id.strikeBtn))

        val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

        modeNum = sharedPreferences.getInt("modeNum", 1)

        music = sharedPreferences.getBoolean("music", true)

        SetMode()

    }

    fun ChangeMode(view: View){

        if(music) {
            val mediaPlayer = MediaPlayer.create(this, R.raw.minus_audio)

            mediaPlayer.start()
        }

        modeNum = BtnArray.indexOf(view) + 1

        SetMode()

    }

    fun SetMode(){


        val text = when(modeNum){


            1-> "Each round all players answer a question and the first to reach the winning points win the game, if the number of rounds ended before that the player with the most points wins the game, if a tie happened the mode changes to Last One Standing between the tied players."
            2-> "Each round all players answer a question, when someone fails to answer a question and at least one other person answers correctly, he is out of the game. The game continues until only one player remains."
            3-> "Every player keep answering questions until he fails to answer one, he gets a point for every right answer and the player with the most points wins the game, if a tie happened the mode changes to Last One Standing between the tied players."
            else -> "Race"

        }

        findViewById<TextView>(R.id.modeDesc).text = text

        for (i in 0..2){

            BtnArray[i].background = AppCompatResources.getDrawable(this, R.drawable.multi_player)

        }

        BtnArray[modeNum-1].background = AppCompatResources.getDrawable(this, R.drawable.single_player)

    }

    fun Apply(view: View){

        val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        editor.putInt("modeNum", modeNum)

        editor.apply()

        if(music) {
            val mediaPlayer = MediaPlayer.create(this, R.raw.plus_click_audio)

            mediaPlayer.start()
        }

        val bundle = intent.extras!!

        val intent = Intent(this, AddPlayers()::class.java)
        val Names = bundle.getStringArrayList("Names")!!
        val playersNum = bundle.getInt("playersNum")
        intent.putExtra("Names", Names)
        intent.putExtra("playersNum", playersNum)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onBackPressed() {

        val bundle = intent.extras!!

        val intent = Intent(this, AddPlayers()::class.java)
        val Names = bundle.getStringArrayList("Names")!!
        val playersNum = bundle.getInt("playersNum")
        intent.putExtra("Names", Names)
        intent.putExtra("playersNum", playersNum)

        startActivity(intent)

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

    }

}