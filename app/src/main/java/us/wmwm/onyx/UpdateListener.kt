package us.wmwm.onyx

import android.animation.ValueAnimator
import com.airbnb.lottie.LottieAnimationView

class UpdateListener(val lot: LottieAnimationView) : ValueAnimator.AnimatorUpdateListener {
    var count = 0.0f
    var init:Float = 0f
    override fun onAnimationUpdate(p0: ValueAnimator) {
        val diff = p0.animatedFraction-init
        count+=diff
        init = p0.animatedFraction
        if(p0.animatedFraction==1f) {
            init = 0f
        }
        if(count>=1.5f) {
            lot.pauseAnimation()
            lot.removeUpdateListener(this)
        }
    }

}