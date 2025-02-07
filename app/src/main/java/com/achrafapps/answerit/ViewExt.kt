package com.achrafapps.answerit

import android.view.View
import android.view.animation.AnimationUtils
import androidx.interpolator.view.animation.FastOutLinearInInterpolator

fun View.slideUp(animTime : Long, startOffSet : Long, slideType : Int){
    val slideUp = AnimationUtils.loadAnimation(context, slideType).apply {

        duration = animTime
        interpolator = FastOutLinearInInterpolator()
        this.startOffset = startOffSet

    }
    startAnimation(slideUp)

}