package us.wmwm.onyx

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val date = Date()
        val first = Date(packageManager.getPackageInfo(packageName,0).firstInstallTime)
        val expired = !BuildConfig.DEBUG && (first.before(BuildConfig.BUILD_TIME) || first.after(BuildConfig.EXPIRE_TIME))
        if(expired || date.before(BuildConfig.BUILD_TIME) || date.after(BuildConfig.EXPIRE_TIME)) {
            AppExpiredBottomSheetDialogFragment().show(supportFragmentManager,"expired")
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}