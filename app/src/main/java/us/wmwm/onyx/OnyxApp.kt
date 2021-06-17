package us.wmwm.onyx

import android.app.Application
import android.bluetooth.BluetoothAdapter
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import us.wmwm.onyx.bluetooth.BluetoothManager
import us.wmwm.onyx.connect.BikeConnectDialogFragmentViewModel
import us.wmwm.onyx.connect.BikesFoundBottomSheetDialogFragmentViewModel
import us.wmwm.onyx.connect.ConnectFragmentViewModel
import us.wmwm.onyx.connect.ConnectedFragmentViewModel
import us.wmwm.onyx.db.OnyxDb
import us.wmwm.onyx.settings.ReviewSettingsBottomSheetViewModel
import us.wmwm.onyx.settings.SettingsViewModule


class OnyxApp : Application() {

    val appModule = module {

        single {
            BluetoothAdapter.getDefaultAdapter()
        }

        single {
            FirebaseAnalytics.getInstance(get<OnyxApp>())
        }

        single {
            BluetoothManager(get())
        }

        single {
            this@OnyxApp
        }

        single {
            Room.databaseBuilder(
                applicationContext,
                OnyxDb::class.java, "onyx"
            ).build()
        }

        single {
            OnyxTracker(get())
        }

        viewModel {
            PulseViewModel(get(), get())
        }

        viewModel {
            MainActivityViewModel(get(),get())
        }
        viewModel {
            ConnectFragmentViewModel(get(),get(),get())
        }
        viewModel {
            SettingsViewModule(get(),get(), get())
        }
        viewModel {
            BikesFoundBottomSheetDialogFragmentViewModel(get())
        }
        viewModel {
            BikeConnectDialogFragmentViewModel(get())
        }
        viewModel {
            ReviewSettingsBottomSheetViewModel(get(),get(), get())
        }

        viewModel {
            ConnectedFragmentViewModel(get())
        }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidLogger()
            androidContext(this@OnyxApp)
            modules(appModule)
        }
    }
}