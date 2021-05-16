package us.wmwm.onyx

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import us.wmwm.onyx.bluetooth.BluetoothManager
import us.wmwm.onyx.db.OnyxDb


class OnyxApp : Application() {

    val appModule = module {

        single {
            BluetoothAdapter.getDefaultAdapter()
        }

        single {
            BluetoothManager(get())
        }

        single {
            Room.databaseBuilder(
                applicationContext,
                OnyxDb::class.java, "onyx"
            ).build()
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
            SettingsViewModule(get())
        }
        viewModel {
            BikesFoundBottomSheetDialogFragmentViewModel(get())
        }
        viewModel {
            BikeConnectDialogFragmentViewModel(get())
        }
        viewModel {
            ReviewSettingsBottomSheetViewModel(get())
        }

        viewModel {
            ConnectedFragmentViewModel(get())
        }


    }

    override fun onCreate() {
        super.onCreate()
        val policy = ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)
        startKoin{
            androidLogger()
            androidContext(this@OnyxApp)
            modules(appModule)
        }
    }
}