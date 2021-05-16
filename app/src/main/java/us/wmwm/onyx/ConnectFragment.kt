package us.wmwm.onyx

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.location.LocationManager
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import us.wmwm.onyx.databinding.BluetoothDeviceViewBinding
import us.wmwm.onyx.databinding.FragmentConnectBinding
import us.wmwm.onyx.db.BluetoothDvc


class ConnectFragment : Fragment() {

    private var _b: FragmentConnectBinding? = null

    val b get() = _b!!

    val vm: ConnectFragmentViewModel by viewModel()
    val bfsVm: BikesFoundBottomSheetDialogFragmentViewModel by sharedViewModel()
    val bfVm: BikeConnectDialogFragmentViewModel by sharedViewModel()

    val bluetoothAdapter: BluetoothAdapter by inject()

    lateinit var requestPermissionLauncher:ActivityResultLauncher<String>

    private val regex = "[0-9]{8,9}".toRegex()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _b = FragmentConnectBinding.inflate(inflater)
        return b.root
    }

    val receivers = mapOf(BluetoothAdapter.ACTION_DISCOVERY_FINISHED to object :
            BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            vm.onDoneDiscovering()
        }

    },
            BluetoothDevice.ACTION_BOND_STATE_CHANGED to object :
                    BroadcastReceiver() {
                @SuppressLint("MissingPermission")
                override fun onReceive(p0: Context, p1: Intent) {
                    val device = p1.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                            ?: return
                    if (device.bondState == BluetoothDevice.BOND_BONDING) {
                        device.setPin("1234".toByteArray())
                    } else {
                        vm.onBonded(device)
                    }
                }
            },
            BluetoothDevice.ACTION_FOUND to object : BroadcastReceiver() {
                override fun onReceive(p0: Context, p1: Intent) {
                    val dev = p1.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)!!
                    val claz = p1.getParcelableExtra<BluetoothClass>(BluetoothDevice.EXTRA_CLASS)!!

                    val name = p1.getStringExtra(BluetoothDevice.EXTRA_NAME) ?: return
                    if (!regex.matches(name)) {
                        return
                    }
                    val major = claz.majorDeviceClass
                    if (major != 7936) {
                        return
                    }
                    val rssi = p1.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    vm.onDevice(
                            BluetoothDvc(
                                    device = dev.address,
                                    type = claz.majorDeviceClass,
                                    name = name,
                                    rssi = rssi.toInt()
                                )
                    )

                }
            },
            BluetoothDevice.ACTION_ACL_CONNECTED to object : BroadcastReceiver() {
                override fun onReceive(p0: Context?, p1: Intent?) {
                    println("connected through action")
                }

            },
            BluetoothDevice.ACTION_ACL_DISCONNECTED to object : BroadcastReceiver() {
                override fun onReceive(p0: Context, p1: Intent) {
                    val dev = p1.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)!!
                    vm.onDisconnected(dev)
                }

            }
    )

    override fun onResume() {
        super.onResume()
        checkLocationServicesAndBluetoothAreOn()
        vm.onResume()
    }

    private fun checkLocationServicesAndBluetoothAreOn() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val bluetoothEnabled = bluetoothAdapter.isEnabled
        vm.onNeedGpsOrBluetooth(isGpsEnabled, bluetoothEnabled)
    }

    override fun onStart() {
        super.onStart()
        receivers.forEach {
            requireActivity().registerReceiver(it.value, IntentFilter(it.key).apply {
                priority = IntentFilter.SYSTEM_HIGH_PRIORITY
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        receivers.forEach {
            requireActivity().unregisterReceiver(it.value)
        }
    }

    fun discovery(passive:Boolean=false) {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val bluetoothEnabled = bluetoothAdapter.isEnabled
        if (!isGpsEnabled || !bluetoothEnabled) {
            showLocationAndOrBluetoothRequired(isGpsEnabled, bluetoothEnabled)
        }

        val needed = listOfNotNull(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_ADMIN
        )
                .filter {
                    checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
                }
        when {
            needed.isEmpty() -> vm.discovery()
            !passive -> {
                requestPermissionLauncher.launch(
                    needed[0])
            }
        }
    }

    fun showLocationAndOrBluetoothRequired(gpsEnabled: Boolean, bluetoothEnabled: Boolean) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.needGpsOrBt.observe(viewLifecycleOwner, Observer {
            val any = listOf(it.first, it.second).any { !it }
            if (!it.first) {
                listOf(b.bt, b.enableBt).forEach { it.visible() }
            } else {
                listOf(b.bt, b.enableBt).forEach { it.gone() }
            }
            if (!it.second) {
                listOf(b.location, b.enableLocation).forEach { it.visible() }
            } else {
                listOf(b.location, b.enableLocation).forEach { it.gone() }
            }
            if (any) {
                b.connect.gone()
            } else {
                b.connect.visible()
            }
        })

        b.connect.setOnClickListener {
            it.hide()
            discovery()
        }

        vm.countdown.observe(viewLifecycleOwner, Observer {
            b.countdown.text = it.toString()
        })

        vm.onDiscovering.observe(viewLifecycleOwner, Observer {
            if (it) {
                b.connect.text = ""
                b.connecting.visible()
                b.connect.gone()
                b.scanning.visible()
                b.countdown.visible()
            } else {
                b.connect.text = "connect to bike"
                b.connecting.gone()
                b.connect.visible()
                b.scanning.gone()
                b.countdown.gone()
            }
        })

        vm.onDevices.observe(viewLifecycleOwner, Observer {
            bfsVm.init(it)
            val frag = childFragmentManager.findFragmentByTag("bfsVm")
            if(frag==null) {
                BikesFoundBottomSheetDialogFragment().show(childFragmentManager, "bfsVm")
            }
        })

        bfVm.noYes.observe(viewLifecycleOwner, Observer {
            if (it.first == NoYes.yes) {
                vm.onRightDevice(it.second)
            } else {
                vm.onWrongDevice(it.second)
            }
        })

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    discovery(false)
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }

    }

    private fun showDenied() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}

class BluetoothDeviceAdapter : androidx.recyclerview.widget.ListAdapter<BluetoothDvc, BaseViewHolder>(object : DiffUtil.ItemCallback<BluetoothDvc>() {

    override fun areItemsTheSame(oldItem: BluetoothDvc, newItem: BluetoothDvc): Boolean {
        return oldItem.device == newItem.device
    }

    override fun areContentsTheSame(oldItem: BluetoothDvc, newItem: BluetoothDvc): Boolean {
        return oldItem.device == newItem.device && oldItem.rssi==newItem.rssi
    }


}) {

    var onClick:(device: BluetoothDvc)->Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(BluetoothDeviceView(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)
            click = this@BluetoothDeviceAdapter.onClick
        })
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val v = holder.view
        when (v) {
            is BluetoothDeviceView -> v.bind(currentList[position])
        }
    }
}

class BluetoothDeviceView(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    val b = BluetoothDeviceViewBinding.inflate(LayoutInflater.from(context), this)

    var click:(device: BluetoothDvc)->Unit = {}

    init {
        val root = b.root as ConstraintLayout
        root.clipChildren = false
        root.clipToPadding = false
    }

    fun bind(bluetoothDvc: BluetoothDvc) {
        b.device.text = bluetoothDvc.nickname?:bluetoothDvc.name
        bluetoothDvc.nickname?.let {
            b.label.visible()
        }?: kotlin.run {
            b.label.gone()
        }
        val feet = formatToFeet(bluetoothDvc.rssi)
        val str = when(feet) {
            Integer.MAX_VALUE -> "∞"
            else-> feet.toString()
        }
        b.feet.text = str
        b.feet.background = ShapeDrawable(OvalShape())
        val color = when (feet) {
            0 -> R.color.nearby_green
            5 -> R.color.nearby_yellow
            10 -> R.color.nearby_orange
            else -> R.color.nearby_red
        }.run {
            ResourcesCompat.getColor(resources,this,null)
        }
        b.feet.backgroundTintList = ColorStateList.valueOf(color)

        //b.proximity.setTextColor(color)
        b.root.setOnClickListener {
            click(bluetoothDvc)
        }
    }

    fun formatToFeet(rssi: Int):Int {
        return when {
            rssi <= -80 -> 0
            rssi <= -50 -> 5
            rssi >= -40 -> 10
            rssi >= -30 -> 20
            else-> Integer.MAX_VALUE
        }
    }

}

class BaseViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

}

