package us.wmwm.onyx

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.RawRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView

class LottieTabView(context: Context, attrs: AttributeSet?=null) : ConstraintLayout(context, attrs) {

    val lottie: LottieAnimationView

    var end:Int = 0

    init {
        inflate(context, R.layout.view_lottie_tab, this)
        lottie = findViewById(R.id.lottie)
        lottie.addLottieOnCompositionLoadedListener {
            end = it.endFrame.div(2).toInt()
            lottie.frame = end
        }
    }

    fun bind(@RawRes res:Int) {
        lottie.setAnimation(res)
    }

    fun play() {
        lottie.removeAllAnimatorListeners()
        lottie.removeAllUpdateListeners()
        lottie.addAnimatorUpdateListener(UpdateListener(lottie))
        lottie.resumeAnimation()
    }

    fun stop() {
        lottie.removeAllAnimatorListeners()
        lottie.removeAllUpdateListeners()
        lottie.pauseAnimation()
        lottie.frame = end
    }
}