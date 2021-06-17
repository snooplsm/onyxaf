package us.wmwm.onyx.connect

import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import us.wmwm.onyx.*
import us.wmwm.onyx.common.Consumable
import us.wmwm.onyx.databinding.FragmentBikeFoundBottomSheetBinding
import us.wmwm.onyx.databinding.FragmentMotorStaysBinding
import us.wmwm.onyx.db.BluetoothDvc
import us.wmwm.onyx.db.OnyxDb

class BikeConnectBottomSheetDialogFragment : BottomSheetDialogFragment() {

    var _b: FragmentBikeFoundBottomSheetBinding?=null

    val b: FragmentBikeFoundBottomSheetBinding get() = _b!!

    val vm: BikeConnectDialogFragmentViewModel by sharedViewModel()

    val cm: MainActivityViewModel by sharedViewModel()

    val t: OnyxTracker by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _b = FragmentBikeFoundBottomSheetBinding.inflate(inflater)
        return b.root
    }

    override fun onDestroyView() {
        //_b?.edit?.handler?.removeCallbacksAndMessages(null)
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

            val vis = when(it.nickname) {
                null-> View.GONE
                else-> View.VISIBLE
            }
            b.bikeName.visible()
            b.oldBikeName.visibility = vis

            b.yes.visible()

            b.yes.setOnClickListener {
                vm.onyes()
                dismiss()
            }

            b.dismiss.setOnClickListener {
                dismiss()
            }
        })
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

class MotorStayBottomSheetDialogFragment : BottomSheetDialogFragment() {

    val t: OnyxTracker by inject()

    var _b:FragmentMotorStaysBinding?=null
    val b:FragmentMotorStaysBinding
    get() {
        return _b!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _b = FragmentMotorStaysBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val str = resources.getString(R.string.however_some_people_want_to_part_with_money_to_show_support_so_if_you_send_25_to_ryan_gravener_on_venmo_with_a_comment_of_your_address_i_ll_mail_you_some_motor_stays)

        val span = str.replaceWithSpans {
            when(it) {
                "@Ryan-Gravener"-> SpannableStringBuilder(it).apply {
                    setSpan(object : URLSpan("https://venmo.com/code?user_id=2182932026359808304") {
                        override fun onClick(widget: View) {
                            super.onClick(widget)
                            t.trackVenmoClicked()
                        }
                    },0,it.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                else-> SpannableStringBuilder(it)
            }
        }
        b.venmoString.movementMethod = LinkMovementMethod.getInstance()
        b.venmoString.text = span
    }
}