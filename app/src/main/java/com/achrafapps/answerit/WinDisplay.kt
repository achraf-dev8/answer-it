package com.achrafapps.answerit

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WinDisplay : AppCompatActivity() {

    var winner = 1
    val PlayersLayouts = ArrayList<LinearLayout>()
    val PlayersTxts = ArrayList<TextView>()
    val ScoreTxts = ArrayList<TextView>()
    var Names = ArrayList<String>()
    var ScoresArray = arrayListOf(0, 0, 0, 0, 0, 0)
    var playersNum = 2
    var modeNum = 1
    var boolPlayers = arrayListOf(0, 0, 0, 0, 0, 0)
    lateinit var dialog : Dialog
    var music = false
    private var mInterstitialAd: InterstitialAd? = null
    var premium = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_win_display)
        DefineArrays()
        RetiveAndSave()
        LoadAd()
    }

    fun DefineArrays(){

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

    }

   fun RetiveAndSave(){

       val runnable = {
       val bundle = intent.extras!!

       winner = bundle.getInt("winner")

       val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

       val scoresFirstTime = sharedPreferences.getBoolean("scoresFirstTime", false)


          premium = sharedPreferences.getBoolean("premium", false)
       modeNum = sharedPreferences.getInt("modeNum", 1)

       playersNum = sharedPreferences.getInt("PlayersNum", 2)

           music = sharedPreferences.getBoolean("music", true)

       val gson = Gson()

       var json = sharedPreferences.getString("Names", null)

       var type = object : TypeToken<ArrayList<String>>() {}.type

       Names = gson.fromJson(json, type)

       if(scoresFirstTime){

           json = sharedPreferences.getString("scoresArray", null)

           type = object : TypeToken<ArrayList<Int>>() {}.type

           ScoresArray = gson.fromJson(json, type)

       }

       ScoresArray[winner-1]++

       for (i in 0..<playersNum) {

           if (Names[i] == "") {

               Names[i] = "Player " + (i + 1)

           }

       }
           val winnerTxt =  findViewById<TextView>(R.id.winnerTxt)

       winnerTxt.text = Names[winner-1]
           winnerTxt.visibility = View.VISIBLE
           YoYo.with(Techniques.Swing).duration(1300).playOn(findViewById(R.id.winnerTxt))

           if(music) {
               val mediaPlayer = MediaPlayer.create(this, R.raw.winaudio)
               mediaPlayer.start()
           }

       for(i in 0..<playersNum){

           boolPlayers[i] = 1

       }

           animation2()

       }

       Handler().postDelayed(runnable, 1300)

   }

    fun animation2(){

        if(checkConnection()) {
            val runnable = {
                findViewById<TextView>(R.id.boardTxt).visibility = View.VISIBLE
                findViewById<TextView>(R.id.boardTxt).slideUp(1000, 0, R.anim.slide_up)
                for (i in 0..<playersNum) {

                    PlayersLayouts[i].visibility = View.VISIBLE

                    PlayersTxts[i].text = Names[i]

                    ScoreTxts[i].text = ScoresArray[i].toString()

                    PlayersLayouts[i].slideUp(1000, 0, R.anim.slide_up)

                }

                findViewById<Button>(R.id.newgameBtn).visibility = View.VISIBLE
                findViewById<Button>(R.id.newgameBtn).slideUp(1000, 0, R.anim.slide_up)
                findViewById<Button>(R.id.backBtn).visibility = View.VISIBLE
                findViewById<Button>(R.id.backBtn).slideUp(1000, 0, R.anim.slide_up)
                Save()
            }

            Handler().postDelayed(runnable, 1200)
        }else{
            Toast.makeText(this, "Check your internet connection!", Toast.LENGTH_SHORT).show()
        }

    }

    fun Save(){

        val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        val gson = Gson()

        val json = gson.toJson(ScoresArray)

        editor.putString("scoresArray", json)

        editor.putBoolean("scoresFirstTime", true)

        editor.apply()

    }

    fun NewGame(view: View){

        if(checkConnection() || premium) {

            if (mInterstitialAd != null && !premium) {
                mInterstitialAd?.show(this)
            } else {
                launchNext()
            }
            if(music) {
                val mediaPlayer = MediaPlayer.create(this, R.raw.button_audio)

                mediaPlayer.start()
            }

        }else{
            Toast.makeText(this, "Check your internet connection!", Toast.LENGTH_SHORT).show()
        }


    }

    fun Cancel(view: View){

if(music){

    val mediaPlayer = MediaPlayer.create(this, R.raw.button_audio2)

    mediaPlayer.start()

}
        onBackPressed()


    }
    override fun onBackPressed() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR)
        dialog.setContentView(R.layout.dialogue)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
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

    fun checkConnection():Boolean{

        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        val connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!
            .state == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED

        return connected

    }

    fun launchNext(){

        val intent = Intent(this, Category::class.java)

        intent.putExtra("scoresArray", arrayListOf(0, 0, 0, 0, 0, 0))

        intent.putExtra("boolPlayers", boolPlayers)

        intent.putExtra("points", 0)

        if (modeNum == 1) {
            intent.putExtra("roundNum", 1)

        } else if (modeNum == 2) {

            intent.putExtra("roundNum", -1)

        } else {

            intent.putExtra("roundNum", -2)

        }


    }

    fun LoadAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            "ca-app-pub-4393553200223427/8269929144",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }


                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    callBack()
                }
            })
    }


    fun callBack(){
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.

            }

            override fun onAdDismissedFullScreenContent() {
                launchNext()

            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {

                mInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.

            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.

            }
        }

    }


}