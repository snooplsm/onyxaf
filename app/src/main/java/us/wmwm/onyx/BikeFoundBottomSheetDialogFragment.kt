package us.wmwm.onyx

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel
import us.wmwm.onyx.databinding.FragmentBikeFoundBottomSheetBinding

class BikeFoundBottomSheetDialogFragment : BottomSheetDialogFragment() {

    var _b: FragmentBikeFoundBottomSheetBinding?=null

    val b: FragmentBikeFoundBottomSheetBinding get() = _b!!

    val vm: BikeFoundBottomSheetDialogFragmentViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _b = FragmentBikeFoundBottomSheetBinding.inflate(inflater)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.device.observe(viewLifecycleOwner, Observer {
            b.bikeName.text = it.name
            b.yes.visible()

            b.no.visible()

            b.no.setOnClickListener {
                vm.onNo()
                dismiss()
            }

            b.yes.setOnClickListener {
                vm.onyes()
                dismiss()
            }
        })
        b.rename.setOnClickListener {
            vm.onRename()
        }

        vm.rename.observe(viewLifecycleOwner, Observer {
            b.edit.visible()
            b.edit.setText(it.name)
            b.bikeName.hide()
            b.rename.hide()
            b.save.visible()
            b.edit.handler.postDelayed({
                b.edit.requestFocus()
                //b.edit.setSelection(b.edit.length())
            },100)
        })

        vm.normal.observe(viewLifecycleOwner, Observer {
            b.edit.hide()
            b.bikeName.visible()
            b.rename.visible()
            b.save.hide()
        })

        b.save.setOnClickListener {
            vm.save(b.edit.text.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (view!!.parent as View).setBackgroundColor(Color.TRANSPARENT)
    }
}

class BikeFoundBottomSheetDialogFragmentViewModel : ViewModel() {

    val device = MutableLiveData<BluetoothDvc>()

    val noYes = MutableLiveData<Pair<NoYes,BluetoothDvc>>()

    val rename = MutableLiveData<BluetoothDvc>()

    val normal = MutableLiveData<BluetoothDvc>()

    fun init(dvc: BluetoothDvc) {
        device.postValue(dvc)
    }

    fun onNo() {
        noYes.postValue(NoYes.no to device.value!!)
    }

    fun onyes() {
        noYes.postValue(NoYes.yes to device.value!!)
    }

    fun onRename() {
        rename.postValue(device.value)
    }

    fun save(toString: String) {
        device.postValue(device.value!!.copy(
                name=toString
        ))
        normal.postValue(device.value)
    }
}

enum class NoYes {
    no,yes
}