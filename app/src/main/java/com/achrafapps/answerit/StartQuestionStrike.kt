package com.achrafapps.answerit

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.doOnEnd
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class StartQuestionStrike : AppCompatActivity() {

    lateinit var question : Question
    var answersBtn = ArrayList<Button>()
    var notAnswered = false
    lateinit var progress : ObjectAnimator
    lateinit var progressBar : ProgressBar
    var pNotCanceled = true
    var Names= ArrayList<String>()
    var playersNum= 2
    var currentPlayer = 1
    var categoryName = "History"
    var points = 0
    var boolPlayers= ArrayList<Int>()
    var roundNum = 0
    lateinit var intent2 :Intent
    var best = 0
    var music = true
    lateinit var dialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_question)

                intent2 = Intent(this, ShowScoreStrike::class.java)

                        progressBar = findViewById(R.id.progressBar)
                        progress = ObjectAnimator.ofInt(progressBar, "progress", 0).setDuration(15000)

         Retive()

       progress.start()

                 val allQuestions = AllQuestions()

                   val category = when(categoryName){

                "History"-> allQuestions.catHistory
               "Technology"-> allQuestions.catTech
                 "Geography" -> allQuestions.catGeo
                 "Science" ->allQuestions.catScience
                "Sports" ->allQuestions.catSport
                "Entertainment" ->allQuestions.catEnter

                else -> allQuestions.catHistory

                                          }

                    val random = System.currentTimeMillis()%(category.size)

                  question = category[random.toInt()]

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

    @SuppressLint("ResourceAsColor")
    fun Done(answer : Int){

        var winMusic = false
        var musicOfwin = R.raw.right_audio

        findViewById<Button>(R.id.NextBtn).visibility = View.VISIBLE

        answersBtn[question.right-1].background = AppCompatResources.getDrawable(this, R.drawable.right_style)

        for(i in 0..3) {

            if (i != answer-1 && i != question.right-1) {

                answersBtn[i].background = AppCompatResources.getDrawable(this, R.drawable.answer_done_style)

            }

        }
            if (answer == question.right) {

                winMusic = true

                intent2 = Intent(this, Category::class.java)

                points++

            }else{

            if(music) {

                val mediaPlayer = MediaPlayer.create(this, R.raw.wrong_audio)

                mediaPlayer.start()

            }

            }

        if (roundNum == -3) {

            val PLayerTxt =    findViewById<TextView>(R.id.PlayerTxt)
          PLayerTxt.text = "Score:$points"

               if (points == best+1 && winMusic) {

                   PLayerTxt.text = "New Best Score!"

                   PLayerTxt.textSize = 37F

                   PLayerTxt.setTextColor(Color.parseColor("#FFEB32"))

                   musicOfwin = R.raw.winaudio

               }


            if(answer != question.right) {
                intent2 = Intent(this, SinglePlayerEnd::class.java)
            }

        }

        if(winMusic){

            if(music) {
                val mediaPlayer = MediaPlayer.create(this, musicOfwin)

                mediaPlayer.start()
            }

        }

        findViewById<TextView>(R.id.PlayerTxt).text = "Score:$points"
        
    }

    @SuppressLint("ResourceAsColor")
    fun Retive(){

        val bundle = intent.extras

        categoryName = bundle!!.getString("category")!!

        currentPlayer =  bundle.getInt("currentPlayer")

        boolPlayers =  bundle.getIntegerArrayList("boolPlayers")!!

        roundNum = bundle.getInt("roundNum")

        points = bundle.getInt("points")

           val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

        playersNum = sharedPreferences.getInt("PlayersNum", 2)

       music = sharedPreferences.getBoolean("music", true)

               if (roundNum != -3) {

                   val gson = Gson()

                   val json = sharedPreferences.getString("Names", null)

                   val type = object : TypeToken<ArrayList<String>>() {}.type

                   Names = gson.fromJson(json, type)
                  if (Names[currentPlayer - 1] != "") {

                 findViewById<TextView>(R.id.PlayerTxt).text = Names[currentPlayer - 1]
                } else {

                                        findViewById<TextView>(R.id.PlayerTxt).text = "Player " + currentPlayer
                                    }
                                }else{

                                    best = sharedPreferences.getInt("best", 0)

                                    if(points>best){

                                        findViewById<TextView>(R.id.PlayerTxt).setTextColor(Color.parseColor("#FFEB32"))
                                    }

                                    findViewById<TextView>(R.id.PlayerTxt).text = "Score:$points"


                                }
    }


    fun Next(view:View){

        intent2.putExtra("points", points)

        intent2.putExtra("currentPlayer", currentPlayer)

        intent2.putExtra("scoresArray", intent.extras!!.getIntegerArrayList("scoresArray")!!)

        intent2.putExtra("roundNum", roundNum)

        intent2.putExtra("boolPlayers", boolPlayers)

        startActivity(intent2)

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