package com.achrafapps.answerit

import android.content.Intent
import android.media.MediaPlayer
import com.google.android.gms.ads.AdRequest
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class AddPlayers : AppCompatActivity() {

    val PlayersLayouts = ArrayList<LinearLayout>()
    var Players = ArrayList<EditText>()
    var Adds = ArrayList<LinearLayout>()
    var FirstTime= false
    var Names = ArrayList<String>()
    var boolPlayers = ArrayList<Int>()
    var modeNum = 1
    var playersNum = 2
    var music = true
    private var mInterstitialAd: InterstitialAd? = null
    var premium = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_players)


        LoadAd()

        boolPlayers = arrayListOf(0, 0, 0, 0, 0, 0)

        DefineArrays()
        Retive()
    }

    fun DefineArrays(){



        Names= arrayListOf("", "", "", "", "", "")

        PlayersLayouts.add(findViewById(R.id.linearLayout1))
        PlayersLayouts.add(findViewById(R.id.linearLayout2))
        PlayersLayouts.add(findViewById(R.id.linearLayout3))
        PlayersLayouts.add(findViewById(R.id.linearLayout4))
        PlayersLayouts.add(findViewById(R.id.linearLayout5))
        PlayersLayouts.add(findViewById(R.id.linearLayout6))

        Players.add(findViewById(R.id.Player1))
        Players.add(findViewById(R.id.Player2))
        Players.add(findViewById(R.id.Player3))
        Players.add(findViewById(R.id.Player4))
        Players.add(findViewById(R.id.Player5))
        Players.add(findViewById(R.id.Player6))


        Adds.add(findViewById(R.id.linearLayout33))
        Adds.add(findViewById(R.id.linearLayout44))
        Adds.add(findViewById(R.id.linearLayout55))
        Adds.add(findViewById(R.id.linearLayout66))


    }
    fun plus(view:View){

        playersNum++

        findViewById<Button>(R.id.minus_1).visibility = View.VISIBLE

        findViewById<Button>(R.id.minus_2).visibility = View.VISIBLE

        Adds[playersNum -3].visibility = View.GONE

       PlayersLayouts[playersNum-1].visibility = View.VISIBLE

      if(playersNum<6) { Adds[playersNum -2].visibility = View.VISIBLE}

        Players[playersNum-1].text = null

        if(music){

        val mediaPlayer = MediaPlayer.create(this, R.raw.plus_click_audio)

        mediaPlayer.start()
        }
    }

    fun minus(view:View){


        val PlayersNames = ArrayList<Editable>()


    val minused : Int = when(view){
      findViewById<Button>(R.id.minus_1) -> 1
        findViewById<Button>(R.id.minus_2) -> 2
     findViewById<Button>(R.id.minus_3) -> 3
      findViewById<Button>(R.id.minus_4) -> 4
    findViewById<Button>(R.id.minus_5) -> 5
       findViewById<Button>(R.id.minus_6) -> 6
                      else -> 3
                  }
        if (playersNum == 3){

                    findViewById<Button>(R.id.minus_1).visibility = View.INVISIBLE
                    findViewById<Button>(R.id.minus_2).visibility = View.INVISIBLE

                }


                 for(i in 0..<playersNum){
           if(i != minused-1) {
              PlayersNames.add(Players[i].text)

                                   }
                               }

          playersNum--


                  for(i in 0..<playersNum){

             Players[i].text = PlayersNames[i]

                    }

         Adds[playersNum -2].visibility = View.VISIBLE

           PlayersLayouts[playersNum].visibility = View.GONE

      if(playersNum<5) {
          Adds[playersNum -1].visibility = View.GONE
      }

    if(music){

     val mediaPlayer = MediaPlayer.create(this, R.raw.minus_audio)

      mediaPlayer.start()
    }

    }

    fun Start(view: View){

        Save()


        if(checkConnection() || premium) {
            if (music) {

                val mediaPlayer = MediaPlayer.create(this, R.raw.button_audio)

                mediaPlayer.start()

            }
            if (mInterstitialAd != null && !premium) {
                mInterstitialAd?.show(this)
            } else {
                launchNext()
            }
        }else{

            Toast.makeText(this, "Check your internet connection!", Toast.LENGTH_SHORT).show()

        }


    }


    fun settings(view: View){
        if(music){

            val mediaPlayer = MediaPlayer.create(this, R.raw.button_audio2)

            mediaPlayer.start()


        }

        val intent = Intent(this, Settings::class.java)
        setNames()
        intent.putExtra("Names", Names)
        intent.putExtra("playersNum", playersNum)
        startActivity(intent)
}

    fun Mode(view: View){
        if(music){

            val mediaPlayer = MediaPlayer.create(this, R.raw.button_audio2)

            mediaPlayer.start()


        }

        val intent = Intent(this, Mode::class.java)

        setNames()
        intent.putExtra("Names", Names)
        intent.putExtra("playersNum", playersNum)

        startActivity(intent)
    }


fun Retive(){

    val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

    modeNum = sharedPreferences.getInt("modeNum", 1)

    music = sharedPreferences.getBoolean("music", true)

    premium = sharedPreferences.getBoolean("premium", false)

    val bundle = intent.extras

    if(bundle?.getInt("playersNum") != null) {

        Names = bundle.getStringArrayList("Names")!!

        playersNum = bundle.getInt("playersNum")

    }else {
        FirstTime = sharedPreferences.getBoolean("FirstTime", false)

        playersNum = sharedPreferences.getInt("PlayersNum", 2)

        if (FirstTime) {

            val gson = Gson()

            val json = sharedPreferences.getString("Names", null)

            val type = object : TypeToken<ArrayList<String>>() {}.type

            Names = gson.fromJson(json, type)

        }
    }

    for(i in 0..<playersNum){

    PlayersLayouts[i].visibility= View.VISIBLE

        val name = SpannableStringBuilder(Names[i])
        Players[i].text= name
    }

    if(playersNum !=2){
        findViewById<LinearLayout>(R.id.linearLayout33).visibility = View.GONE
        findViewById<Button>(R.id.minus_1).visibility = View.VISIBLE
        findViewById<Button>(R.id.minus_2).visibility = View.VISIBLE
    if(playersNum !=6){

        Adds[playersNum-2].visibility = View.VISIBLE
    }
}

    val text = when(modeNum){

        1-> "Race"
        2-> "Last One Standing"
        3-> "Best Streak"
        else -> "Race"

    }

    findViewById<Button>(R.id.modeBtn).text = text

    if (modeNum != 1){

        findViewById<ImageView>(R.id.settingsImg).visibility = View.GONE

    }


}

    fun Save(){


        setNames()

        val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        val gson = Gson()

        val json = gson.toJson(Names)

        editor.putString("Names", json)

        editor.putBoolean("FirstTime", true)

        editor.putInt("PlayersNum", playersNum)

        editor.apply()

    }

    fun setNames(){

        for(i in 0..<playersNum){

            Names[i] = Players[i].text.toString()

            boolPlayers[i] = 1

        }

    }

    fun checkConnection():Boolean{

        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        val connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!
            .state == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED

        return connected

    }

    override fun onBackPressed() {

        val intent = Intent(this, MainActivity()::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

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
        startActivity(intent)

    }

    fun LoadAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            "ca-app-pub-4393553200223427/1298941083",
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
