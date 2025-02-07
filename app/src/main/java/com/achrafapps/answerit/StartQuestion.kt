package com.achrafapps.answerit

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.doOnEnd
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class StartQuestion : AppCompatActivity() {

    lateinit var question : Question
    var answersBtn = ArrayList<Button>()
    var notAnswered = false
    lateinit var progress : ObjectAnimator
    lateinit var progressBar : ProgressBar
    var pNotCanceled = true
    var Names= ArrayList<String>()
    var playersNum= 2
    var currentPlayer = 1
    var nextPlayer = 2
    var categoryName = "History"
    var pointsArray = ArrayList<Int>()
    var boolPlayers= ArrayList<Int>()
    var NextPlayerBool = false
    var category = ArrayList<Question>()
    var music = true
    lateinit var dialog : Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_question)

        progressBar = findViewById(R.id.progressBar)
        progress = ObjectAnimator.ofInt(progressBar, "progress", 0).setDuration(15000)
        Retive()

        val allQuestions = AllQuestions()

         category = when(categoryName){

            "History"-> allQuestions.catHistory
            "Technology"-> allQuestions.catTech
            "Geography" -> allQuestions.catGeo
            "Science" ->allQuestions.catScience
            "Sports" ->allQuestions.catSport
            "Entertainment" ->allQuestions.catEnter
            else -> allQuestions.catHistory

        }

        setArray()

        answersBtn = arrayListOf(findViewById(R.id.ans1Btn), findViewById(R.id.ans2Btn), findViewById(R.id.ans3Btn), findViewById(R.id.ans4Btn))



       DefineQuestion()



        progress.doOnEnd {

            if(pNotCanceled){

                val TimeMsg = findViewById<TextView>(R.id.timeMsg)
                TimeMsg.visibility = View.VISIBLE

                YoYo.with(Techniques.Swing).duration(1000).repeat(2).playOn(TimeMsg)

                Done(0)

            }
        }



        Handler().postDelayed({startGame()}, 1800)



    }


    fun DefineQuestion(){

        findViewById<TextView>(R.id.subjectTxt).text = question.subject
        findViewById<TextView>(R.id.ans1Btn).text = question.ans1
        findViewById<TextView>(R.id.ans2Btn).text = question.ans2
        findViewById<TextView>(R.id.ans3Btn).text = question.ans3
        findViewById<TextView>(R.id.ans4Btn).text = question.ans4

    }

    fun Answer(view:View){

        if(notAnswered) {

            val answer = answersBtn.indexOf(view) + 1

            pNotCanceled = false

            progress.cancel()

            view.background = AppCompatResources.getDrawable(this, R.drawable.wrong_style)

            Done(answer)

        }


        notAnswered = false
    }

    fun Done(answer : Int){

        answersBtn[question.right-1].background = AppCompatResources.getDrawable(this, R.drawable.right_style)

        findViewById<Button>(R.id.NextBtn).visibility = View.VISIBLE

        GetNextPlayer()

        if (NextPlayerBool) {

            val NextPlayerTxt = findViewById<TextView>(R.id.NextPlayerTxt)

            NextPlayerTxt.visibility = View.VISIBLE

            if(Names[nextPlayer - 1] != "") {

                NextPlayerTxt.text = Names[nextPlayer - 1]

            }else{

                NextPlayerTxt.text = "Player $nextPlayer"
            }

        }

        for(i in 0..3){

            if (i != answer-1 && i != question.right - 1){

                answersBtn[i].background =  AppCompatResources.getDrawable(this, R.drawable.answer_done_style)

            }

        }

        if(answer == question.right) {

            pointsArray[currentPlayer - 1] = 1

            if(music) {
                val mediaPlayer = MediaPlayer.create(this, R.raw.right_audio)

                mediaPlayer.start()
            }

        }else{
            if(music) {
            val mediaPlayer = MediaPlayer.create(this, R.raw.wrong_audio)

            mediaPlayer.start()
            }
        }



    }

    fun Retive(){

        val bundle = intent.extras

        categoryName = bundle!!.getString("category")!!

        currentPlayer =  bundle.getInt("currentPlayer")

        pointsArray = bundle.getIntegerArrayList("pointsArray")!!

        boolPlayers =  bundle.getIntegerArrayList("boolPlayers")!!

        val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

        playersNum = sharedPreferences.getInt("PlayersNum", 2)

        music = sharedPreferences.getBoolean("music", true)

            val gson = Gson()

            val json = sharedPreferences.getString("Names", null)

            val type = object : TypeToken<ArrayList<String>>() {}.type

            Names = gson.fromJson(json, type)

        if(Names[currentPlayer-1] != "") {

            findViewById<TextView>(R.id.PlayerTxt).text = Names[currentPlayer - 1]
        }else{

            findViewById<TextView>(R.id.PlayerTxt).text = "Player " + currentPlayer
        }

    }

    fun GetNextPlayer(){

        if(currentPlayer != 6) {
            nextPlayer = currentPlayer

            while (nextPlayer <= playersNum && !NextPlayerBool) {

                if (boolPlayers[nextPlayer] == 1) {

                    NextPlayerBool = true

                }

                nextPlayer++

            }
        }

    }

    fun Next(view:View){

        var intent2 = Intent(this, ShowScore::class.java)

        if(NextPlayerBool){

            intent2 = Intent(this, StartQuestion::class.java)

            intent2.putExtra("currentPlayer", nextPlayer)

            intent2.putExtra("category", categoryName)

        }

        intent2.putExtra("pointsArray",pointsArray)

        intent2.putExtra("scoresArray", intent.extras!!.getIntegerArrayList("scoresArray")!!)

        intent2.putExtra("roundNum", intent.extras!!.getInt("roundNum"))

        intent2.putExtra("boolPlayers", boolPlayers)

        startActivity(intent2)

    }

    fun setArray(){

        val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

        val FirstTimeCats = sharedPreferences.getBoolean("FirstTime$categoryName", false)

        val gson = Gson()

            if (FirstTimeCats) {

                val json = sharedPreferences.getString(categoryName, null)

                val type = object : TypeToken<ArrayList<Question>>() {}.type

                category = gson.fromJson(json, type)

            }else{

                val editor = sharedPreferences.edit()

                editor.putBoolean("FirstTime$categoryName", true)

                editor.apply()
            }

        setQuestion()

        }

    fun setQuestion(){

        val random = (0..<category.size).random()
        
        question = category[random]

        category.remove(question)

        val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        val json = Gson().toJson(category)

        editor.putString(categoryName, json)

        if(category.isEmpty()){
            editor.putBoolean("FirstTime$categoryName", false)
        }
        editor.apply()
    }

    override fun onBackPressed() {

            dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR)
            dialog.setContentView(R.layout.dialogue)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

    }

    fun yes(view: View) {

        pNotCanceled = false

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

    fun startGame(){

        for (i in 0..<4) {

            answersBtn[i].visibility = View.VISIBLE

            answersBtn[i].slideUp(1000, 0, R.anim.slide_up)

        }
        Handler().postDelayed({startGame2()}, 1700)
    }

    fun startGame2() {
        progressBar.visibility = View.VISIBLE
        progress.start()
        notAnswered = true
    }


}

