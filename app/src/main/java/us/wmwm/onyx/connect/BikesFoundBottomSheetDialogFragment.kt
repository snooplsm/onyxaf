package us.wmwm.onyx.connect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import us.wmwm.onyx.OnyxTracker
import us.wmwm.onyx.R
import us.wmwm.onyx.common.SpaceItemDecoration
import us.wmwm.onyx.databinding.FragmentBikesFoundBottomSheetBinding

class BikesFoundBottomSheetDialogFragment : BottomSheetDialogFragment() {

    var _b: FragmentBikesFoundBottomSheetBinding? = null

    val b: FragmentBikesFoundBottomSheetBinding get() = _b!!

    val vm: BikesFoundBottomSheetDialogFragmentViewModel by sharedViewModel()

    val bfVm: BikeConnectDialogFragmentViewModel by sharedViewModel()

    val adapter = BluetoothDeviceAdapter()

    val t: OnyxTracker by inject()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _b = FragmentBikesFoundBottomSheetBinding.inflate(inflater)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.recyclerView.adapter = adapter
        b.recyclerView.itemAnimator = DefaultItemAnimator().apply {
            supportsChangeAnimations = false
            removeDuration=200
            addDuration=400
        }
        b.recyclerView.addItemDecoration(
            SpaceItemDecoration(
            firstTop = resources.getDimension(R.dimen.five),
            lastBottom = resources.getDimension(R.dimen.five)
        )
        )

        vm.device.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        //vm.conn

        adapter.onClick = {
            bfVm.init(it)
            val frag = childFragmentManager.findFragmentByTag("bfVm")
            if(frag==null) {
                val f = BikeConnectBottomSheetDialogFragment()
                t.screen(f.javaClass)
                f.show(childFragmentManager,"bfVm")
            }
        }

        vm.dismiss.observe(viewLifecycleOwner, Observer {
            dismiss()
        })
    }
}