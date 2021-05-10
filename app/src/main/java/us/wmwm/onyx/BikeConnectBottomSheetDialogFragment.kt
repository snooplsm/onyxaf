package us.wmwm.onyx

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.viewmodel.ext.android.sharedViewModel
import us.wmwm.onyx.databinding.FragmentBikeFoundBottomSheetBinding
import us.wmwm.onyx.db.BluetoothDvc
import us.wmwm.onyx.db.OnyxDb

class BikeConnectBottomSheetDialogFragment : BottomSheetDialogFragment() {

    var _b: FragmentBikeFoundBottomSheetBinding?=null

    val b: FragmentBikeFoundBottomSheetBinding get() = _b!!

    val vm: BikeConnectDialogFragmentViewModel by sharedViewModel()

    val cm:MainActiivtyViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _b = FragmentBikeFoundBottomSheetBinding.inflate(inflater)
        return b.root
    }

    override fun onDestroyView() {
        _b?.edit?.handler?.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.oldBikeName.paintFlags = b.oldBikeName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        cm.connected.observe(viewLifecycleOwner, Observer {

        })

        vm.device.observe(viewLifecycleOwner, Observer {
            b.bikeName.text = it.nickname?:it.name
            b.oldBikeName.text = it.name

            when(it.nickname) {
                null-> View.GONE
                else->View.VISIBLE
            }
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
//        b.rename.setOnClickListener {
//            vm.onRename()
//        }

        vm.rename.observe(viewLifecycleOwner, Observer {
            val it = it.consume()?:return@Observer
            b.edit.visible()
            b.edit.setText(it.name)
            b.bikeName.hide()
            b.rename.gone()
            b.save.visible()
            b.edit?.handler.postDelayed({
                b.edit?.requestFocus()
                //b.edit.setSelection(b.edit.length())
            },100)
            b.edit.addTextChangedListener { k->
                if(k.toString().trim()!=it.name) {
                    b.dismiss.visible()
                }
            }
            b.dismiss.setOnClickListener {v->
                if(b.edit.text.toString()!=it.device) {
                    b.edit.setText(it.device)
                } else {
                    vm.onDismiss()
                }
            }
        })

        vm.normal.observe(viewLifecycleOwner, Observer {
            val it = it.consume()?:return@Observer
            b.bikeName.text = it.nickname?:it.name
            b.edit.hide()
            b.bikeName.visible()
            //b.rename.visible()
            b.save.gone()
            b.dismiss.gone()
        })

        b.save.setOnClickListener {
            vm.save(b.edit.text.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

}

class BikeConnectDialogFragmentViewModel(val db: OnyxDb) : ViewModel() {

    val device = MutableLiveData<BluetoothDvc>()

    val noYes = MutableLiveData<Pair<NoYes, BluetoothDvc>>()

    val rename = MutableLiveData<Consumable<BluetoothDvc>>()

    val normal = MutableLiveData<Consumable<BluetoothDvc>>()

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
        rename.postValue(Consumable(device.value!!))
    }

    fun save(toString: String) {
        val dev = device.value!!.copy(
            nickname = toString
        )
        Single.just(1)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { t1, t2 ->
                db.device().insertOrUpdate(dev)
            }

        device.postValue(dev)
        normal.postValue(Consumable(dev))
    }

    fun onDismiss() {
        device.postValue(device.value!!)
        normal.postValue(Consumable(device.value!!))
    }
}

enum class NoYes {
    no,yes
}