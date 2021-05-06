package us.wmwm.onyx

import android.app.Application
import android.bluetooth.BluetoothAdapter
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import us.wmwm.onyx.bluetooth.BluetoothManager

class OnyxApp : Application() {

    val appModule = module {

        single {
            BluetoothAdapter.getDefaultAdapter()
        }

        single {
            BluetoothManager(get())
        }

        viewModel {
            PulseViewModel(get())
        }

        viewModel {
            MainActiivtyViewModel(get(),get())
        }
        viewModel {
            ConnectFragmentViewModel(get(),get())
        }
        viewModel {
            SettingsViewModule(get())
        }
        viewModel {
            BikesFoundBottomSheetDialogFragmentViewModel()
        }
        viewModel {
            BikeFoundBottomSheetDialogFragmentViewModel()
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