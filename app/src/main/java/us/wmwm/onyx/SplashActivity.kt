package us.wmwm.onyx

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val date = Date()
        if(date.before(BuildConfig.BUILD_TIME) || date.after(BuildConfig.EXPIRE_TIME)) {
            AppExpiredBottomSheetDialogFragment().show(supportFragmentManager,"expired")
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
