package us.wmwm.onyx

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel
import us.wmwm.onyx.databinding.FragmentBikesFoundBottomSheetBinding

class BikesFoundBottomSheetDialogFragment : BottomSheetDialogFragment() {

    var _b: FragmentBikesFoundBottomSheetBinding? = null

    val b: FragmentBikesFoundBottomSheetBinding get() = _b!!

    val vm: BikesFoundBottomSheetDialogFragmentViewModel by sharedViewModel()

    val bfVm: BikeFoundBottomSheetDialogFragmentViewModel by sharedViewModel()

    val adapter = BluetoothDeviceAdapter()

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
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.recyclerView.adapter = adapter
        b.recyclerView.itemAnimator = DefaultItemAnimator().apply {
            supportsChangeAnimations = false
            removeDuration=200
            addDuration=400
        }

        vm.device.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        adapter.onClick = {
            bfVm.init(it)
            val frag = childFragmentManager.findFragmentByTag("bfVm")
            if(frag==null) {
                val f = BikeFoundBottomSheetDialogFragment()
                f.show(childFragmentManager,"bfVm")
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (view!!.parent as View).setBackgroundColor(Color.TRANSPARENT)
    }
}