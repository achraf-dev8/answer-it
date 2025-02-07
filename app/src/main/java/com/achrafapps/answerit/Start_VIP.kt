package com.achrafapps.answerit

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.work.impl.model.Preference
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.common.collect.ImmutableList


class Start_VIP : AppCompatActivity() {

    var isPremium = false
    var billingClient: BillingClient? = null
    var purchaseUpdateListener: PurchasesUpdatedListener? = null
    var isReady = false
    var subKeyOne: String? = null
    var toastV = true
    var music = true

    @SuppressLint("StaticFieldLeak")
    var btnNext: Button? = null
    private var inAppProductDetail: ProductDetails? = null
    private var bolPurchaseValue = false
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: Editor

    @SuppressLint("MissingInflatedId", "CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_vip)

        val sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)

        music = sharedPreferences.getBoolean("music", true)

        animation()

        btnNext = findViewById(R.id.purchaseBtn)
        purchaseUpdateListener = PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    handlePurchase(this@Start_VIP, purchase)
                }
            }
        }

        billingClient = BillingClient.newBuilder(this)
            .setListener(purchaseUpdateListener!!)
            .enablePendingPurchases()
            .build()

        setupConnection(this)



        btnNext!!.setOnClickListener {

            inAppProductDetail?.let { it1 -> purchase(it1) }

            if(music) {
                val mediaPlayer = MediaPlayer.create(this, R.raw.button_audio2)
                mediaPlayer.start()
            }
        }

    }

    fun animation(){
        findViewById<Button>(R.id.linearLayout).slideUp(1500, 0, R.anim.slide_up)
        findViewById<Button>(R.id.linearLayout7).slideUp(1500, 0, R.anim.slide_up)
        findViewById<Button>(R.id.linearLayout8).slideUp(1500, 0, R.anim.slide_up)
        Handler().postDelayed({
            if(music) {
                val mediaPlayer = MediaPlayer.create(this, R.raw.logo_audio)
                mediaPlayer.start()
            }
            YoYo.with(Techniques.Swing).duration(1500).playOn(findViewById(R.id.purchaseBtn))
        }, 2000)


    }



        override fun onResume() {
            super.onResume()
            purchaseUpdateListener =
                PurchasesUpdatedListener { billingResult: BillingResult, purchases: List<Purchase?>? ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                        for (purchase in purchases) {
                            handlePurchase(this, purchase!!)
                        }
                    }
                }
            billingClient = BillingClient.newBuilder(this)
                .setListener(purchaseUpdateListener!!)
                .enablePendingPurchases()
                .build()
            setupConnection(this)
        }

        private fun setupConnection(context: Context) {
            billingClient!!.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        isReady = true
                        getOldPurchases(this@Start_VIP)
                        showProductsAvailable()
                    }
                }

                override fun onBillingServiceDisconnected() {
                    isReady = false
                    setupConnection(this@Start_VIP)
                }
            })
        }

        @SuppressLint("ResourceAsColor")
        private fun purchase(inAppProductDetails: ProductDetails) {
            if (isReady) {
                val productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(inAppProductDetails).build()
                )

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()

                val billingResult = billingClient!!.launchBillingFlow(this, billingFlowParams)
            } else {
                setupConnection(this)
            }
        }

        private fun showProductsAvailable() {
            val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("purchase")
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    )
                )
                .build()

            billingClient?.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
                if (productDetailsList != null && productDetailsList.size == 1) {
                    inAppProductDetail = productDetailsList[0]

                    runOnUiThread {
                        val price = inAppProductDetail!!.oneTimePurchaseOfferDetails!!.formattedPrice
                        btnNext?.text = "Only for $price"
                    }
                }
            }
        }

        private fun getOldPurchases(context: Context) {
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()

            billingClient!!.queryPurchasesAsync(params) { billingResult, purchases ->
                if (purchases.size > 0) {
                    for (purchase in purchases) {
                        if (purchase.isAcknowledged && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            context.getSharedPreferences("PREF_PREIMIUM", Context.MODE_PRIVATE)
                                .edit()
                                .putBoolean(subKeyOne, true)
                                .apply()

                            isPremium = true
                            bolPurchaseValue = true
                            val consumeParams = ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.purchaseToken)
                                .build()

                            billingClient!!.consumeAsync(consumeParams) { _, _ ->
                                isPremium = false


                            }


                        }
                    }
                } else {
                    bolPurchaseValue = false
                }
            }
        }

        private fun handlePurchase(context: Context, purchase: Purchase) {
            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            val acknowledgePurchaseResponseListener =
                AcknowledgePurchaseResponseListener { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        isPremium = true
                        billingClient?.consumeAsync(consumeParams) { _, _ ->
                            isPremium = true

                        }
                    }
                }


            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {

                if (purchase.isAcknowledged) {
                    isPremium = true
                    val orderId = purchase.orderId
                    billingClient?.consumeAsync(consumeParams) { _, _ ->
                        isPremium = false
                    }
                }
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient?.acknowledgePurchase(
                        acknowledgePurchaseParams,
                        acknowledgePurchaseResponseListener
                    )
                }
            }

            if(toastV) {
                Toast.makeText(this, "Thank you! now you are premium!", Toast.LENGTH_SHORT).show()
                toastV = false
            }
            sharedPreferences = getSharedPreferences("shared", MODE_PRIVATE)
                editor = sharedPreferences.edit()
                editor.putBoolean("premium", true)
                editor.apply()

            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)}, 2000
            )


        }
    }
