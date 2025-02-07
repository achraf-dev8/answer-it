package com.achrafapps.answerit



import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds


class MainActivity : AppCompatActivity() {

    var music = true

    var premium = false

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MyApplication)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

        music = sharedPreferences.getBoolean("music", true)
        premium = sharedPreferences.getBoolean("premium", false)
        setMusic()
        val editor = sharedPreferences.edit()
        editor.putBoolean("scoresFirstTime", false)
        editor.apply()

if(!premium) {
    MobileAds.initialize(this) {}
    val mAdView = findViewById<AdView>(R.id.adView)
    val adRequest = AdRequest.Builder().build()
    mAdView.loadAd(adRequest)
}else{
    findViewById<Button>(R.id.get_vpn_btn).visibility = View.GONE
}
                animation()


    }


    fun animation(){
        findViewById<Button>(R.id.multi_player_btn).slideUp(1000, 0, R.anim.slide_up)
        findViewById<Button>(R.id.single_player_btn).slideUp(1000, 0, R.anim.slide_up)
        findViewById<Button>(R.id.get_vpn_btn).slideUp(1000, 0, R.anim.slide_up)
        findViewById<ImageButton>(R.id.music_btn).slideUp(1000, 0, R.anim.slide_up)

        findViewById<ImageButton>(R.id.share_btn).slideUp(1000, 0, R.anim.slide_up2)
        findViewById<ImageButton>(R.id.star_btn).slideUp(1000, 0, R.anim.slide_up2)
        Handler().postDelayed({
            if(music) {
                val mediaPlayer = MediaPlayer.create(this, R.raw.logo_audio)
                mediaPlayer.start()
            }
            YoYo.with(Techniques.RubberBand).duration(1200).playOn(findViewById(R.id.logo))
        }, 1000)


    }

    fun MultiPlayer(view: View){

        if(premium  || checkConnection()) {

            val intent = Intent(this, AddPlayers::class.java)

            if (music) {
                val mediaPlayer = MediaPlayer.create(this, R.raw.button_audio)

                mediaPlayer.start()
            }
            startActivity(intent)
        }else{

            Toast.makeText(this, "Check your internet connection, or go premium!", Toast.LENGTH_SHORT).show()

        }

    }

    fun SinglePlayer(view: View){

        if(premium  || checkConnection()) {
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

        }else{
            Toast.makeText(this, "Check your internet connection, or go premium!", Toast.LENGTH_SHORT).show()
        }

    }

    fun Music(view: View){

        music = !music

        val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        editor.putBoolean("music", music)

        editor.apply()

        if(music){

            val mediaPlayer = MediaPlayer.create(this, R.raw.plus_click_audio)

            mediaPlayer.start()


        }else{

            val mediaPlayer = MediaPlayer.create(this, R.raw.minus_audio)

            mediaPlayer.start()

        }

        setMusic()



    }

    fun setMusic(){


        if(music){

            findViewById<ImageView>(R.id.music_btn).setImageResource(R.drawable.volume_vector)



        }else{

            findViewById<ImageView>(R.id.music_btn).setImageResource(R.drawable.no_volume_vector)

        }


    }

    fun checkConnection():Boolean{

        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

         val connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!
            .state == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED

        return connected

    }
    fun Rate(view: View){

        if (music) {
            val mediaPlayer = MediaPlayer.create(this, R.raw.button_audio2)

            mediaPlayer.start()
        }

            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.packageName)
                )
            )
        }

    fun Share(view: View){

        if (music) {
            val mediaPlayer = MediaPlayer.create(this, R.raw.button_audio2)

            mediaPlayer.start()
        }

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this game!")
        sendIntent.type = "text/plain"
        startActivity(sendIntent)

    }

    fun Premium(view: View){
            if (music) {
                val mediaPlayer = MediaPlayer.create(this, R.raw.button_audio2)

                mediaPlayer.start()
            }
        val intent = Intent(this, Start_VIP::class.java)
            startActivity(intent)
    }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)

    }




}
