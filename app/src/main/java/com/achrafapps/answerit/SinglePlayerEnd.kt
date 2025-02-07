package com.achrafapps.answerit

import android.content.Intent
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class SinglePlayerEnd : AppCompatActivity() {


    var music = false
    private var mInterstitialAd: InterstitialAd? = null
    var premium = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_player_end)
        Retive()
        LoadAd()
    }

   fun Retive(){

       val bundle = intent.extras!!

       val score = bundle.getInt("points")

       val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

       val best = sharedPreferences.getInt("best", 0)

      premium = sharedPreferences.getBoolean("premium", true)

           music = sharedPreferences.getBoolean("music", true)


       findViewById<TextView>(R.id.singleScoreTxt).text = "Score:$score"

       if(score > best){
           findViewById<TextView>(R.id.BestTxt).text = "New best score!"

           val editor = sharedPreferences.edit()

           editor.putInt("best", score)

           editor.apply()

           findViewById<TextView>(R.id.BestTxt).textSize = 34F

           val mediaPlayer = MediaPlayer.create(this, R.raw.winaudio)

           mediaPlayer.start()

       }else{

           findViewById<TextView>(R.id.BestTxt).text = "Best:$best"
       }

    }

    fun NewGame(vie: View){

        if(checkConnection()  || premium) {

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

    fun Cancel(view:View){

        if(music){

            val mediaPlayer = MediaPlayer.create(this, R.raw.button_audio2)

            mediaPlayer.start()

        }

        val intent = Intent(this, MainActivity::class.java)

        startActivity(intent)

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

        intent.putExtra("boolPlayers", arrayListOf(1, 0, 0, 0, 0, 0))

        intent.putExtra("points", 0)

        intent.putExtra("roundNum", -3)

        if (music) {
            val mediaPlayer = MediaPlayer.create(this, R.raw.button_audio)

            mediaPlayer.start()
        }

        startActivity(intent)


    }

    fun LoadAd() {

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-4393553200223427/1723297128",
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